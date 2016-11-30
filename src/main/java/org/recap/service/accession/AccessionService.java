package org.recap.service.accession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
@Service
public class AccessionService {

    Logger log = Logger.getLogger(AccessionService.class);

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    MarcUtil marcUtil;

    @Autowired
    SolrIndexService solrIndexService;

    @PersistenceContext
    private EntityManager entityManager;

    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(customerCode);
            if (null != customerCodeEntity) {
                owningInstitution = customerCodeEntity.getInstitutionEntity().getInstitutionCode();
            }
        } catch (Exception e) {
            log.error("Exception " + e);
        }
        return owningInstitution;
    }

    @Transactional
    public String processRequest(String itemBarcode, String owningInstitution) {
        String response = null;
        RestTemplate restTemplate = new RestTemplate();

        String ilsBibDataURL = getILSBibDataURL(owningInstitution);
        String bibDataResponse = null;
        if (StringUtils.isNotBlank(ilsBibDataURL)) {
            try {
                bibDataResponse = restTemplate.getForObject(ilsBibDataURL + itemBarcode, String.class);
            } catch (HttpClientErrorException ex) {
                response = "Item Barcode not found";
                return response;
            } catch (Exception e) {
                response = ilsBibDataURL + "Service is Unavailable.";
                return response;
            }
        }
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = marcUtil.readMarcXml(bibDataResponse);
        }
        List<Map<String,String>> responseMapList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(records)) {
            try {
                for (Record record : records) {
                    Map responseMap = marcToBibEntityConverter.convert(record, owningInstitution);
                    responseMapList.add(responseMap);
                    BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get("bibliographicEntity");
                    List<ReportEntity> reportEntityList = (List<ReportEntity>) responseMap.get("reportEntities");
                    if (CollectionUtils.isNotEmpty(reportEntityList)) {
                        reportDetailRepository.save(reportEntityList);
                    }
                    if (bibliographicEntity != null) {
                        BibliographicEntity savedBibliographicEntity =updatebiBliographicEntity(bibliographicEntity);

                        if (null != savedBibliographicEntity) {
                            solrIndexService.indexByBibliographicId(savedBibliographicEntity.getBibliographicId());
                            response = RecapConstants.SUCCESS;
                        }
                    }
                }
                generateAccessionSummaryReport(responseMapList,owningInstitution);
            } catch (Exception e) {
                response = e.getMessage();
                log.error(e.getMessage());
            }

        }

        return response;
    }

    private void generateAccessionSummaryReport(List<Map<String,String>> responseMapList,String owningInstitution){
        int successBibCount = 0;
        int successItemCount = 0;
        int failedBibCount = 0;
        int failedItemCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        String reasonForFailureItem = "";
        String itemBarcode = "";

        for(Map responseMap : responseMapList){
            successBibCount = successBibCount + (responseMap.get(RecapConstants.SUCCESS_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.SUCCESS_BIB_COUNT) : 0);
            successItemCount = successItemCount + (responseMap.get(RecapConstants.SUCCESS_ITEM_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.SUCCESS_ITEM_COUNT) : 0);
            failedBibCount = failedBibCount + (responseMap.get(RecapConstants.FAILED_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.FAILED_BIB_COUNT) : 0);
            failedItemCount = failedItemCount + (responseMap.get(RecapConstants.FAILED_ITEM_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.FAILED_ITEM_COUNT) : 0);
            exitsBibCount = exitsBibCount + (responseMap.get(RecapConstants.EXIST_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.EXIST_BIB_COUNT) : 0);

            if(!StringUtils.isEmpty(responseMap.get(RecapConstants.ITEMBARCODE).toString())){
                itemBarcode = responseMap.get(RecapConstants.ITEMBARCODE).toString();
            }
            if(!StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE))){
                if(!reasonForFailureBib.contains(responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE).toString())){
                    reasonForFailureBib =  responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE).toString()+ "," +reasonForFailureBib;
                }
            }
            if(!StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))){
                if(!reasonForFailureItem.contains((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))){
                    reasonForFailureItem = (String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE)+ "," +reasonForFailureItem;
                }
            }
        }

        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(owningInstitution);

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_SUCCESS_COUNT);
        successBibCountReportDataEntity.setHeaderValue(String.valueOf(successBibCount));
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_SUCCESS_COUNT);
        successItemCountReportDataEntity.setHeaderValue(String.valueOf(successItemCount));
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName(RecapConstants.NUMBER_OF_BIB_MATCHES);
        existsBibCountReportDataEntity.setHeaderValue(String.valueOf(exitsBibCount));
        reportDataEntities.add(existsBibCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_FAILURE_COUNT);
        failedBibCountReportDataEntity.setHeaderValue(String.valueOf(failedBibCount));
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_FAILURE_COUNT);
        failedItemCountReportDataEntity.setHeaderValue(String.valueOf(failedItemCount));
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_BIB_REASON);
        if(reasonForFailureBib.startsWith("\n")){
            reasonForFailureBib = reasonForFailureBib.substring(1,reasonForFailureBib.length()-1);
        }
        reasonForFailureBib = reasonForFailureBib.replaceAll(",$", "");
        reasonForBibFailureReportDataEntity.setHeaderValue(reasonForFailureBib);
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_ITEM_REASON);
        if(reasonForFailureItem.startsWith("\n")){
            reasonForFailureItem = reasonForFailureItem.substring(1,reasonForFailureItem.length()-1);
        }
        reasonForFailureItem = reasonForFailureItem.replaceAll(",$", "");
        if(!StringUtils.isEmpty(itemBarcode) && successItemCount == 0){
            reasonForItemFailureReportDataEntity.setHeaderValue(itemBarcode+"-"+reasonForFailureItem);
        }else{
            reasonForItemFailureReportDataEntity.setHeaderValue(reasonForFailureItem);
        }
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        reportDetailRepository.save(reportEntityList);
    }

    private String getILSBibDataURL(String owningInstitution) {
        if (owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON)) {
            return ilsprincetonBibData;
        }
        return null;
    }

    public BibliographicEntity updatebiBliographicEntity(BibliographicEntity bibliographicEntity) {
        BibliographicEntity savedBibliographicEntity=null;
        BibliographicEntity fetchBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionId(),bibliographicEntity.getOwningInstitutionBibId());
        if(fetchBibliographicEntity ==null) { // New Bib Record

            savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
            entityManager.refresh(savedBibliographicEntity);
        }else{ // Existing bib Record
            // Bib
            fetchBibliographicEntity.setContent(bibliographicEntity.getContent());
            fetchBibliographicEntity.setCreatedBy(bibliographicEntity.getCreatedBy());
            fetchBibliographicEntity.setCreatedDate(bibliographicEntity.getCreatedDate());
            fetchBibliographicEntity.setDeleted(bibliographicEntity.isDeleted());
            fetchBibliographicEntity.setLastUpdatedBy(bibliographicEntity.getLastUpdatedBy());
            fetchBibliographicEntity.setLastUpdatedDate(bibliographicEntity.getLastUpdatedDate());

            // Holding
            List<HoldingsEntity> fetchHoldingsEntities =fetchBibliographicEntity.getHoldingsEntities();
            List<HoldingsEntity> holdingsEntities = new ArrayList<HoldingsEntity>(bibliographicEntity.getHoldingsEntities());

            log.info("fetchHoldingsEntities = "+fetchHoldingsEntities.size());
            log.info("holdingsEntities = "+holdingsEntities.size());

            for (Iterator iholdings = holdingsEntities.iterator(); iholdings.hasNext();) {
                HoldingsEntity holdingsEntity =(HoldingsEntity) iholdings.next();
                for (int j=0;j<fetchHoldingsEntities.size();j++) {
                    HoldingsEntity fetchHolding=fetchHoldingsEntities.get(j);
                    if(fetchHolding.getOwningInstitutionHoldingsId().equalsIgnoreCase(holdingsEntity.getOwningInstitutionHoldingsId())  && fetchHolding.getOwningInstitutionId().intValue() == holdingsEntity.getOwningInstitutionId().intValue()) {
                        fetchHolding = copytoHoldingsEntity(fetchHolding,holdingsEntity);
                        iholdings.remove();
                    }
                }
            }
            fetchHoldingsEntities.addAll(holdingsEntities);
            log.info("Holding Final Count = "+fetchHoldingsEntities.size());

            // Item
            List<ItemEntity> fetchItemsEntities =fetchBibliographicEntity.getItemEntities();
            List<ItemEntity> itemsEntities = new ArrayList<ItemEntity>(bibliographicEntity.getItemEntities());

            log.info("fetchHoldingsEntities = "+fetchItemsEntities.size());
            log.info("holdingsEntities = "+itemsEntities.size());

            for (Iterator iItems=itemsEntities.iterator();iItems.hasNext();) {
                ItemEntity itemEntity =(ItemEntity) iItems.next();
                for (Iterator ifetchItems=fetchItemsEntities.iterator();ifetchItems.hasNext();) {
                    ItemEntity fetchItem=(ItemEntity) ifetchItems.next();
                    if(fetchItem.getOwningInstitutionItemId().equalsIgnoreCase(itemEntity.getOwningInstitutionItemId())  && fetchItem.getOwningInstitutionId().intValue() == itemEntity.getOwningInstitutionId().intValue()) {
                        fetchItem = copytoHoldingsEntity(fetchItem,itemEntity);
                        iItems.remove();
                    }
                }
            }
            fetchItemsEntities.addAll(itemsEntities);
            log.info("Item Final Count = "+fetchItemsEntities.size());

            fetchBibliographicEntity.setHoldingsEntities(fetchHoldingsEntities);
            fetchBibliographicEntity.setItemEntities(fetchItemsEntities);

            savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(fetchBibliographicEntity);
            entityManager.refresh(fetchBibliographicEntity);
        }
        return savedBibliographicEntity;
    }

    private HoldingsEntity copytoHoldingsEntity(HoldingsEntity fetchHoldingsEntity,HoldingsEntity holdingsEntity){
        fetchHoldingsEntity.setContent(holdingsEntity.getContent()); ;
        fetchHoldingsEntity.setCreatedBy(holdingsEntity.getCreatedBy());
        fetchHoldingsEntity.setCreatedDate(holdingsEntity.getCreatedDate());
        fetchHoldingsEntity.setDeleted(holdingsEntity.isDeleted());
        fetchHoldingsEntity.setLastUpdatedBy(holdingsEntity.getLastUpdatedBy());
        fetchHoldingsEntity.setLastUpdatedDate(holdingsEntity.getLastUpdatedDate());
        return fetchHoldingsEntity;
    }

    private ItemEntity copytoHoldingsEntity(ItemEntity fetchItemEntity,ItemEntity itemEntity){
        fetchItemEntity.setBarcode(itemEntity.getBarcode()); ;
        fetchItemEntity.setCreatedBy(itemEntity.getCreatedBy());
        fetchItemEntity.setCreatedDate(itemEntity.getCreatedDate());
        fetchItemEntity.setDeleted(itemEntity.isDeleted());
        fetchItemEntity.setLastUpdatedBy(itemEntity.getLastUpdatedBy());
        fetchItemEntity.setLastUpdatedDate(itemEntity.getLastUpdatedDate());
        fetchItemEntity.setCallNumber(itemEntity.getCallNumber());
        fetchItemEntity.setCustomerCode(itemEntity.getCustomerCode());
        fetchItemEntity.setCallNumberType(itemEntity.getCallNumberType());
        fetchItemEntity.setItemAvailabilityStatusId(itemEntity.getItemAvailabilityStatusId());
        fetchItemEntity.setCopyNumber(itemEntity.getCopyNumber());
        fetchItemEntity.setCollectionGroupId(itemEntity.getCollectionGroupId());
        fetchItemEntity.setUseRestrictions(itemEntity.getUseRestrictions());
        fetchItemEntity.setVolumePartYear(itemEntity.getVolumePartYear());
        return fetchItemEntity;
    }
}
