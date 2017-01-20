package org.recap.service.deAccession;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONException;
import org.recap.RecapConstants;
import org.recap.model.deAccession.DeAccessionDBResponseEntity;
import org.recap.model.deAccession.DeAccessionRequest;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenchulakshmig on 28/9/16.
 */
@Component
public class DeAccessionService {

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    DeAccessSolrDocumentService deAccessSolrDocumentService;

    public Map<String, String> deAccession(DeAccessionRequest deAccessionRequest) {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = deAccessionItemsInDB(deAccessionRequest.getItemBarcodes());
        processAndSave(deAccessionDBResponseEntities);
        if (!org.springframework.util.CollectionUtils.isEmpty(deAccessionDBResponseEntities)) {
            Map<String, String> resultMap = new HashMap<>();
            List<Integer> bibIds = new ArrayList<>();
            List<Integer> holdingsIds = new ArrayList<>();
            List<Integer> itemIds = new ArrayList<>();
            Map<String, Integer> ownInstAndItemIdMap = new HashMap<>();
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(RecapConstants.FAILURE)) {
                    resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus() + " - " + deAccessionDBResponseEntity.getReasonForFailure());
                } else if (deAccessionDBResponseEntity.getStatus().equalsIgnoreCase(RecapConstants.SUCCESS)) {
                    resultMap.put(deAccessionDBResponseEntity.getBarcode(), deAccessionDBResponseEntity.getStatus());
                    bibIds.addAll(deAccessionDBResponseEntity.getBibliographicIds());
                    holdingsIds.addAll(deAccessionDBResponseEntity.getHoldingIds());
                    itemIds.add(deAccessionDBResponseEntity.getItemId());
                    ownInstAndItemIdMap.put(deAccessionDBResponseEntity.getInstitutionCode(), deAccessionDBResponseEntity.getItemId());
                }
            }
            checkAndCancelHoldsIfExists(ownInstAndItemIdMap);
            deAccessionItemsInSolr(bibIds, holdingsIds, itemIds);
            return resultMap;
        }
        return null;
    }

    public List<DeAccessionDBResponseEntity> deAccessionItemsInDB(List<String> itemBarcodeList) {
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity;
        List<String> responseItemBarcodeList = new ArrayList<>();
        Date currentDate = new Date();
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeIn(itemBarcodeList);

        try {
            String barcode = null;
            for(ItemEntity itemEntity : itemEntityList) {
                try {
                    barcode = itemEntity.getBarcode();
                    responseItemBarcodeList.add(barcode);
                    if(itemEntity.isDeleted()) {
                        deAccessionDBResponseEntity = prepareFailureResponse(barcode, RecapConstants.REQUESTED_ITEM_DEACCESSIONED, itemEntity);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    } else {
                        List<HoldingsEntity> holdingsEntities = itemEntity.getHoldingsEntities();
                        List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
                        Integer itemId = itemEntity.getItemId();
                        List<Integer> holdingsIds = processHoldings(holdingsEntities);
                        List<Integer> bibliographicIds = processBibs(bibliographicEntities);
                        itemDetailsRepository.markItemAsDeleted(itemId, RecapConstants.GUEST, currentDate);
                        updateBibliographicWithLastUpdatedDate(itemId, RecapConstants.GUEST,currentDate);
                        deAccessionDBResponseEntity = prepareSuccessResponse(barcode, itemEntity, holdingsIds, bibliographicIds);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    }
                } catch (Exception ex) {
                    deAccessionDBResponseEntity = prepareFailureResponse(barcode, "Exception" + ex, null);
                    deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                }
            }
            if (responseItemBarcodeList.size() != itemBarcodeList.size()) {
                for (String itemBarcode : itemBarcodeList) {
                    if (!responseItemBarcodeList.contains(itemBarcode)) {
                        deAccessionDBResponseEntity = prepareFailureResponse(itemBarcode, RecapConstants.ITEM_BARCDE_DOESNOT_EXIST, null);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return deAccessionDBResponseEntities;
    }

    public List<ReportEntity> processAndSave(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities) {
        List<ReportEntity> reportEntities = new ArrayList<>();
        ReportEntity reportEntity = null;
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                List<String> owningInstitutionBibIds = deAccessionDBResponseEntity.getOwningInstitutionBibIds();
                if (CollectionUtils.isNotEmpty(owningInstitutionBibIds)) {
                    for (String owningInstitutionBibId : owningInstitutionBibIds) {
                        reportEntity = generateReportEntity(deAccessionDBResponseEntity, owningInstitutionBibId);
                        reportEntities.add(reportEntity);
                    }
                } else {
                    reportEntity = generateReportEntity(deAccessionDBResponseEntity, null);
                    reportEntities.add(reportEntity);
                }
            }
            if (!CollectionUtils.isEmpty(reportEntities)) {
                reportDetailRepository.save(reportEntities);
            }
        }
        return reportEntities;
    }

    private ReportEntity generateReportEntity(DeAccessionDBResponseEntity deAccessionDBResponseEntity, String owningInstitutionBibId) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.DEACCESSION_REPORT);
        reportEntity.setType(RecapConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(RecapConstants.DATE_OF_DEACCESSION);
        dateReportDataEntity.setHeaderValue(formatter.format(new Date()));
        reportDataEntities.add(dateReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getInstitutionCode())) {
            reportEntity.setInstitutionName(deAccessionDBResponseEntity.getInstitutionCode());

            ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
            owningInstitutionReportDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
            owningInstitutionReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getInstitutionCode());
            reportDataEntities.add(owningInstitutionReportDataEntity);
        } else {
            reportEntity.setInstitutionName("NA");
        }

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(RecapConstants.BARCODE);
        barcodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getBarcode());
        reportDataEntities.add(barcodeReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(owningInstitutionBibId)) {
            ReportDataEntity owningInstitutionBibIdReportDataEntity = new ReportDataEntity();
            owningInstitutionBibIdReportDataEntity.setHeaderName(RecapConstants.OWNING_INST_BIB_ID);
            owningInstitutionBibIdReportDataEntity.setHeaderValue(owningInstitutionBibId);
            reportDataEntities.add(owningInstitutionBibIdReportDataEntity);
        }

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getCollectionGroupCode())) {
            ReportDataEntity collectionGroupCodeReportDataEntity = new ReportDataEntity();
            collectionGroupCodeReportDataEntity.setHeaderName(RecapConstants.COLLECTION_GROUP_CODE);
            collectionGroupCodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getCollectionGroupCode());
            reportDataEntities.add(collectionGroupCodeReportDataEntity);
        }

        ReportDataEntity statusReportDataEntity = new ReportDataEntity();
        statusReportDataEntity.setHeaderName(RecapConstants.STATUS);
        statusReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getStatus());
        reportDataEntities.add(statusReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getReasonForFailure())) {
            ReportDataEntity reasonForFailureReportDataEntity = new ReportDataEntity();
            reasonForFailureReportDataEntity.setHeaderName(RecapConstants.REASON_FOR_FAILURE);
            reasonForFailureReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getReasonForFailure());
            reportDataEntities.add(reasonForFailureReportDataEntity);
        }

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;
    }

    private DeAccessionDBResponseEntity prepareSuccessResponse(String itemBarcode, ItemEntity itemEntity, List<Integer> holdingIds, List<Integer> bibliographicIds) throws JSONException {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setStatus(RecapConstants.SUCCESS);
        populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
        deAccessionDBResponseEntity.setHoldingIds(holdingIds);
        deAccessionDBResponseEntity.setBibliographicIds(bibliographicIds);
        return deAccessionDBResponseEntity;
    }

    private DeAccessionDBResponseEntity prepareFailureResponse(String itemBarcode, String reasonForFailure, ItemEntity itemEntity) {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setStatus(RecapConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(reasonForFailure);
        if (itemEntity != null) {
            try {
                populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return deAccessionDBResponseEntity;
    }

    private void populateDeAccessionDBResponseEntity(ItemEntity itemEntity, DeAccessionDBResponseEntity deAccessionDBResponseEntity) throws JSONException {
        InstitutionEntity institutionEntity = itemEntity.getInstitutionEntity();
        if (institutionEntity != null) {
            deAccessionDBResponseEntity.setInstitutionCode(institutionEntity.getInstitutionCode());
        }
        CollectionGroupEntity collectionGroupEntity = itemEntity.getCollectionGroupEntity();
        if (collectionGroupEntity != null) {
            deAccessionDBResponseEntity.setCollectionGroupCode(collectionGroupEntity.getCollectionGroupCode());
        }
        deAccessionDBResponseEntity.setItemId(itemEntity.getItemId());
        List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
        List<String> owningInstitutionBibIds = new ArrayList<>();
        for (BibliographicEntity bibliographicEntity : bibliographicEntities) {
            String owningInstitutionBibId = bibliographicEntity.getOwningInstitutionBibId();
            owningInstitutionBibIds.add(owningInstitutionBibId);
        }
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(owningInstitutionBibIds);
    }

    private List<Integer> processBibs(List<BibliographicEntity> bibliographicEntities) throws JSONException {
        List<Integer> bibliographicIds = new ArrayList<>();
        for (BibliographicEntity bibliographicEntity : bibliographicEntities) {
            Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
            String owningInstitutionBibId = bibliographicEntity.getOwningInstitutionBibId();
            Long nonDeletedItemsCount = bibliographicDetailsRepository.getNonDeletedItemsCount(owningInstitutionId, owningInstitutionBibId);
            if (nonDeletedItemsCount == 1) {
                bibliographicIds.add(bibliographicEntity.getBibliographicId());
            }
        }
        if (CollectionUtils.isNotEmpty(bibliographicIds)) {
            bibliographicDetailsRepository.markBibsAsDeleted(bibliographicIds, RecapConstants.GUEST, new Date());
        }
        return bibliographicIds;
    }

    private List<Integer> processHoldings(List<HoldingsEntity> holdingsEntities) throws JSONException {
        List<Integer> holdingIds = new ArrayList<>();
        for (HoldingsEntity holdingsEntity : holdingsEntities) {
            Integer owningInstitutionId = holdingsEntity.getOwningInstitutionId();
            String owningInstitutionHoldingsId = holdingsEntity.getOwningInstitutionHoldingsId();
            Long nonDeletedItemsCount = holdingsDetailsRepository.getNonDeletedItemsCount(owningInstitutionId, owningInstitutionHoldingsId);
            if (nonDeletedItemsCount == 1) {
                holdingIds.add(holdingsEntity.getHoldingsId());
            }
        }
        if (CollectionUtils.isNotEmpty(holdingIds)) {
            holdingsDetailsRepository.markHoldingsAsDeleted(holdingIds, RecapConstants.GUEST, new Date());
        }
        return holdingIds;
    }

    public void deAccessionItemsInSolr(List<Integer> bibIds, List<Integer> holdingsIds, List<Integer> itemIds) {
        if (CollectionUtils.isNotEmpty(bibIds)) {
            deAccessSolrDocumentService.updateIsDeletedBibByBibId(bibIds);
        }
        if (CollectionUtils.isNotEmpty(holdingsIds)) {
            deAccessSolrDocumentService.updateIsDeletedHoldingsByHoldingsId(holdingsIds);
        }
        if (CollectionUtils.isNotEmpty(itemIds)) {
            deAccessSolrDocumentService.updateIsDeletedItemByItemIds(itemIds);
        }
    }

    public void checkAndCancelHoldsIfExists(Map<String, Integer> ownInstAndItemIdMap) {
        if (ownInstAndItemIdMap != null && ownInstAndItemIdMap.size() > 0) {
            try {
                List<Integer> itemIds = new ArrayList<>();
                itemIds.addAll(ownInstAndItemIdMap.values());
                List<RequestItemEntity> requestItemEntities = requestItemDetailsRepository.findByItemIdIn(itemIds);
                if (CollectionUtils.isNotEmpty(requestItemEntities)) {
                    requestItemDetailsRepository.deleteByItemIdIn(itemIds);
                    //TODO
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateBibliographicWithLastUpdatedDate(Integer itemId,String userName,Date lastUpdatedDate){
        ItemEntity itemEntity = itemDetailsRepository.findByItemId(itemId);
        List<BibliographicEntity> bibliographicEntityList = itemEntity.getBibliographicEntities();
        List<Integer> bibliographicIdList = new ArrayList<>();
        for(BibliographicEntity bibliographicEntity : bibliographicEntityList){
            bibliographicIdList.add(bibliographicEntity.getBibliographicId());
        }
    }
}
