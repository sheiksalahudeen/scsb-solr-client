package org.recap.service.transfer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.controller.TransferController;
import org.recap.model.jpa.*;
import org.recap.model.transfer.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.service.accession.AccessionDAO;
import org.recap.service.accession.DummyDataService;
import org.recap.service.accession.SolrIndexService;
import org.recap.util.HelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by sheiks on 19/07/17.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);


    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    AccessionDAO accessionDAO;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    DummyDataService dummyDataService;

    @Autowired
    SolrIndexService solrIndexService;

    @Autowired
    HelperUtil helperUtil;


    @Transactional
    public List<ItemTransferResponse> processItemTransfer(TransferRequest transferRequest, InstitutionEntity institutionEntity) {
        List<ItemTransferResponse> itemTransferResponses = new ArrayList<>();
        List<ItemTransferRequest> itemTransfers = transferRequest.getItemTransfers();
        if(CollectionUtils.isNotEmpty(itemTransfers)) {
            for (Iterator<ItemTransferRequest> iterator = itemTransfers.iterator(); iterator.hasNext(); ) {
                ItemTransferRequest itemTransferRequest = iterator.next();
                TransferValidationResponse transferValidationResponse = validateItemTransferRequest(itemTransferRequest, institutionEntity.getInstitutionId());
                if(transferValidationResponse.isValid()) {
                    try {
                        Date currentDate = new Date();
                        BibliographicEntity sourceBib = transferValidationResponse.getSourceBib();
                        HoldingsEntity sourceHoldings = transferValidationResponse.getSourceHoldings();
                        ItemEntity sourceItem = transferValidationResponse.getSourceItem();

                        BibliographicEntity destBib = getDestinationBib(transferValidationResponse, currentDate,institutionEntity.getInstitutionId());
                        HoldingsEntity destHoldings = getDestinationHoldings(transferValidationResponse, currentDate, institutionEntity.getInstitutionId());


                        // todo : unlink from source
                        unlinkRecordsForItemTransfer(sourceBib, sourceHoldings, sourceItem, currentDate);

                        // todo : link to destination
                        linkRecordsForItemTransfer(destBib, destHoldings, sourceItem, currentDate);

                        // todo : process for orphan records
                        processOrphanRecords(sourceBib);

                        saveAndIndexBib(sourceBib, sourceHoldings, destBib, destHoldings, Arrays.asList(sourceItem));

                        transferValidationResponse.setMessage(RecapConstants.TRANSFER.SUCCESSFULLY_RELINKED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        transferValidationResponse.setMessage(RecapConstants.TRANSFER.RELINKED_FAILED);
                    }
                }
                ItemTransferResponse itemTransferResponse = new ItemTransferResponse(transferValidationResponse.getMessage(), itemTransferRequest);
                itemTransferResponses.add(itemTransferResponse);

                String requestString = helperUtil.getJsonString(itemTransferRequest);
                String responseString = transferValidationResponse.getMessage();
                saveReportForTransfer(requestString, responseString, institutionEntity.getInstitutionCode(), RecapConstants.TRANSFER.TRANSFER_TYPES.ITEM_TRANSFER);
            }
        }
        return itemTransferResponses;
    }

    private TransferValidationResponse validateItemTransferRequest(ItemTransferRequest itemTransferRequest, Integer institutionId) {
        TransferValidationResponse transferValidationResponse = new TransferValidationResponse(true);
        ItemSource source = itemTransferRequest.getSource();
        if(null != source) {
            ItemDestination destination = itemTransferRequest.getDestination();
            if(null != destination) {
                if(StringUtils.equals(source.getOwningInstitutionItemId(), destination.getOwningInstitutionItemId())) {
                    validSourceBibAndHoldingsAndItem(source, institutionId, transferValidationResponse);
                    if(transferValidationResponse.isValid()) {
                        validDestinationBibAndHoldingsAndItem(destination, institutionId, transferValidationResponse);
                    }
                } else {
                    transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_DESTINATION_ITEM_IDS_NOT_MATCHING);
                }

            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DESTINATION_EMPTY);
            }

        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_EMPTY);
        }
        return transferValidationResponse;
    }

    private void validDestinationBibAndHoldingsAndItem(ItemDestination destination, Integer owningInstitutionId, TransferValidationResponse transferValidationResponse) {
        String owningInstitutionBibId = destination.getOwningInstitutionBibId();
        if(StringUtils.isNotBlank(owningInstitutionBibId)) {
            String owningInstitutionHoldingsId = destination.getOwningInstitutionHoldingsId();
            if(StringUtils.isNotBlank(owningInstitutionHoldingsId)) {
                String owningInstitutionItemId = destination.getOwningInstitutionItemId();
                if(StringUtils.isNotBlank(owningInstitutionItemId)) {
                    BibliographicEntity destinationBibEntity =
                            bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(owningInstitutionId, owningInstitutionBibId);
                    if(null != destinationBibEntity) {
                        transferValidationResponse.setDestBib(destinationBibEntity);
                        HoldingsEntity holdingsEntity = matchHoldingIdWithHoldings(owningInstitutionHoldingsId, destinationBibEntity);
                        if(null == holdingsEntity) {
                            // todo : check with other entity
                            holdingsEntity = holdingsDetailsRepository.
                                    findByOwningInstitutionHoldingsIdAndOwningInstitutionId(owningInstitutionHoldingsId, owningInstitutionId);
                            if(null != holdingsEntity) {
                                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_HOLDINGS_ATTACHED_WITH_DIFF_BIB);
                            }
                        } else {
                            transferValidationResponse.setDestHoldings(holdingsEntity);
                        }
                        transferValidationResponse.setDestHoldingsId(owningInstitutionHoldingsId);
                    } else {
                        transferValidationResponse.setDestinationBibId(owningInstitutionBibId);
                    }
                } else {
                    transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_OWN_INST_ITEM_ID_EMPTY);
                }
            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_OWN_INST_HOLDINGS_ID_EMPTY);
            }

        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_OWN_INST_BIB_ID_EMPTY);
        }
    }

    private void validSourceBibAndHoldingsAndItem(ItemSource source, Integer owningInstitutionId, TransferValidationResponse transferValidationResponse) {
        String owningInstitutionBibId = source.getOwningInstitutionBibId();
        if(StringUtils.isNotBlank(owningInstitutionBibId)) {
            String owningInstitutionHoldingsId = source.getOwningInstitutionHoldingsId();
            if(StringUtils.isNotBlank(owningInstitutionHoldingsId)) {
                String owningInstitutionItemId = source.getOwningInstitutionItemId();
                if(StringUtils.isNotBlank(owningInstitutionItemId)) {
                    BibliographicEntity sourceBibEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(owningInstitutionId, owningInstitutionBibId);
                    if(null != sourceBibEntity) {
                        transferValidationResponse.setSourceBib(sourceBibEntity);
                        HoldingsEntity holdingsEntity = matchHoldingIdWithHoldings(owningInstitutionHoldingsId, sourceBibEntity);
                        if (holdingsEntity == null) {
                            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_HOLDING_NOT_UNDER_SOURCE_BIB);
                        } else {
                            transferValidationResponse.setSourceHoldings(holdingsEntity);
                            ItemEntity itemEntity = matchItemIdWithItem(owningInstitutionItemId, holdingsEntity);
                            if(null == itemEntity) {
                                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_ITEM_NOT_UNDER_SOURCE_HOLDING);
                            } else {
                                transferValidationResponse.setSourceItem(itemEntity);
                            }
                        }
                    } else {
                        transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_BIB_NOT_EXIST);
                    }
                } else {
                    transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_OWN_INST_ITEM_ID_EMPTY);
                }
            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_OWN_INST_HOLDINGS_ID_EMPTY);
            }
        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_OWN_INST_BIB_ID_EMPTY);
        }
    }

    private ItemEntity matchItemIdWithItem(String owningInstitutionItemId, HoldingsEntity holdingsEntity) {
        List<ItemEntity> itemEntities = holdingsEntity.getItemEntities();
        if(CollectionUtils.isNotEmpty(itemEntities)) {
            for (Iterator<ItemEntity> iterator = itemEntities.iterator(); iterator.hasNext(); ) {
                ItemEntity itemEntity = iterator.next();
                if(StringUtils.equals(itemEntity.getOwningInstitutionItemId(), owningInstitutionItemId)) {
                    return itemEntity;
                }
            }
        }
        return null;
    }

    @Transactional
    public List<HoldingTransferResponse> processHoldingTransfer(TransferRequest transferRequest, InstitutionEntity institutionEntity) {
        List<HoldingTransferResponse> holdingTransferResponses = new ArrayList<>();
        List<HoldingsTransferRequest> holdingTransfers = transferRequest.getHoldingTransfers();
        if(CollectionUtils.isNotEmpty(holdingTransfers)) {
            for (Iterator<HoldingsTransferRequest> iterator = holdingTransfers.iterator(); iterator.hasNext(); ) {
                HoldingsTransferRequest holdingsTransferRequest = iterator.next();
                Integer institutionId = institutionEntity.getInstitutionId();
                TransferValidationResponse transferValidationResponse =
                        validateHoldingTransferRequest(holdingsTransferRequest, institutionId);
                if(transferValidationResponse.isValid()) {
                    try {
                        Date currentDate = new Date();
                        BibliographicEntity sourceBib = transferValidationResponse.getSourceBib();
                        HoldingsEntity sourceHoldings = transferValidationResponse.getSourceHoldings();
                        ItemEntity sourceItem = transferValidationResponse.getSourceItem();

                        BibliographicEntity destBib = getDestinationBib(transferValidationResponse, currentDate, institutionId);

                        // todo : unlink from source
                        unlinkRecords(sourceBib, sourceHoldings, sourceItem);

                        // todo : link to destination
                        linkRecords(destBib, sourceHoldings, currentDate);

                        sourceBib.setLastUpdatedDate(currentDate);
                        sourceBib.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);

                        // todo : process for orphan records
                        processOrphanRecords(sourceBib);

                        saveAndIndexBib(sourceBib, sourceHoldings, destBib, sourceHoldings, sourceHoldings.getItemEntities());

                        transferValidationResponse.setMessage(RecapConstants.TRANSFER.SUCCESSFULLY_RELINKED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        transferValidationResponse.setMessage(RecapConstants.TRANSFER.RELINKED_FAILED);
                    }

                }
                HoldingTransferResponse holdingTransferResponse = new HoldingTransferResponse(transferValidationResponse.getMessage(), holdingsTransferRequest);
                holdingTransferResponses.add(holdingTransferResponse);

                String requestString = helperUtil.getJsonString(holdingsTransferRequest);
                String responseString = transferValidationResponse.getMessage();
                saveReportForTransfer(requestString, responseString, institutionEntity.getInstitutionCode(), RecapConstants.TRANSFER.TRANSFER_TYPES.HOLDINGS_TRANSFER);
            }
        }

        return holdingTransferResponses;
    }

    private void saveAndIndexBib(BibliographicEntity sourceBib, HoldingsEntity sourceHoldings, BibliographicEntity destBib,
                                 HoldingsEntity destHoldings,List<ItemEntity> itemEntities) {
        accessionDAO.saveBibRecord(sourceBib);
        accessionDAO.saveBibRecord(destBib);

        writeChangeLog(sourceBib.getBibliographicId(), sourceHoldings.getHoldingsId(), itemEntities, destBib.getBibliographicId(), destHoldings.getHoldingsId());

        solrIndexService.indexByBibliographicId(sourceBib.getBibliographicId());
        solrIndexService.indexByBibliographicId(destBib.getBibliographicId());
    }

    private void processOrphanRecords(BibliographicEntity sourceBib) {
        List<HoldingsEntity> holdingsEntities = sourceBib.getHoldingsEntities();
        if(CollectionUtils.isEmpty(holdingsEntities)) {
            sourceBib.setDeleted(true);
        } else {
            boolean allDeleted = true;
            for (Iterator<HoldingsEntity> iterator = holdingsEntities.iterator(); iterator.hasNext(); ) {
                HoldingsEntity holdingsEntity =  iterator.next();
                boolean allItemDeleted = allItemDeleted(holdingsEntity);
                if(allItemDeleted) {
                    holdingsEntity.setDeleted(true);
                }
                allDeleted = allDeleted & holdingsEntity.isDeleted();
            }
            if(allDeleted) {
                sourceBib.setDeleted(true);
            }
        }
    }

    private boolean allItemDeleted(HoldingsEntity holdingsEntity) {
        List<ItemEntity> itemEntities = holdingsEntity.getItemEntities();
        boolean allItemDeleted = true;
        if(CollectionUtils.isNotEmpty(itemEntities)){
            for (Iterator<ItemEntity> iterator = itemEntities.iterator(); iterator.hasNext(); ) {
                ItemEntity itemEntity = iterator.next();
                allItemDeleted = allItemDeleted & itemEntity.isDeleted();
            }
        }
        return allItemDeleted;
    }

    private void unlinkRecords(BibliographicEntity sourceBib, HoldingsEntity sourceHoldings, ItemEntity sourceItem) {
        sourceHoldings.getBibliographicEntities().remove(sourceBib);
        sourceBib.getHoldingsEntities().remove(sourceHoldings);
        List<ItemEntity> itemEntities = sourceHoldings.getItemEntities();
        if(CollectionUtils.isNotEmpty(itemEntities)) {
            for (Iterator<ItemEntity> iterator = itemEntities.iterator(); iterator.hasNext(); ) {
                ItemEntity itemEntity = iterator.next();
                itemEntity.getBibliographicEntities().remove(sourceBib);
            }
        }
        sourceBib.getItemEntities().removeAll(itemEntities);
    }

    private void unlinkRecordsForItemTransfer(BibliographicEntity sourceBib, HoldingsEntity sourceHoldings, ItemEntity sourceItem, Date updatedDate) {
        sourceItem.getBibliographicEntities().remove(sourceBib);
        sourceItem.getHoldingsEntities().remove(sourceHoldings);
        sourceBib.getItemEntities().remove(sourceItem);
        sourceHoldings.getItemEntities().remove(sourceItem);

        sourceBib.setLastUpdatedDate(updatedDate);
        sourceBib.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
        sourceHoldings.setLastUpdatedDate(updatedDate);
        sourceHoldings.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
    }

    private void linkRecords(BibliographicEntity destBib, HoldingsEntity sourceHoldings, Date updatedDate) {
        sourceHoldings.getBibliographicEntities().add(destBib);
        sourceHoldings.setLastUpdatedDate(updatedDate);
        sourceHoldings.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);

        List<ItemEntity> itemEntities = sourceHoldings.getItemEntities();
        if(CollectionUtils.isNotEmpty(itemEntities)) {
            for (Iterator<ItemEntity> iterator = itemEntities.iterator(); iterator.hasNext(); ) {
                ItemEntity itemEntity = iterator.next();
                itemEntity.getBibliographicEntities().add(destBib);
                itemEntity.setLastUpdatedDate(updatedDate);
                itemEntity.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
            }
        }
        destBib.getItemEntities().addAll(itemEntities);
        destBib.getHoldingsEntities().add(sourceHoldings);

        destBib.setLastUpdatedDate(updatedDate);
        destBib.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
    }

    private void linkRecordsForItemTransfer(BibliographicEntity destBib, HoldingsEntity destHoldings, ItemEntity sourceItem, Date updatedDate) {
        sourceItem.getHoldingsEntities().add(destHoldings);
        sourceItem.getBibliographicEntities().add(destBib);

        destHoldings.getItemEntities().add(sourceItem);
        destBib.getItemEntities().add(sourceItem);

        if(!destHoldings.getBibliographicEntities().contains(destBib)) {
            destHoldings.getBibliographicEntities().add(destBib);
        }

        if(!destBib.getHoldingsEntities().contains(destHoldings)) {
            destBib.getHoldingsEntities().add(destHoldings);
        }

        destBib.setLastUpdatedDate(updatedDate);
        destHoldings.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
        sourceItem.setLastUpdatedDate(updatedDate);
        sourceItem.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
        destBib.setLastUpdatedDate(updatedDate);
        destBib.setLastUpdatedBy(RecapConstants.TRANSFER.TRANSFER);
    }

    private TransferValidationResponse validateHoldingTransferRequest(HoldingsTransferRequest holdingsTransferRequest, Integer institutionId) {
        TransferValidationResponse transferValidationResponse = new TransferValidationResponse(true);
        Source source = holdingsTransferRequest.getSource();
        if(null != source) {
            Destination destination = holdingsTransferRequest.getDestination();
            if(null != destination) {

                if(StringUtils.equals(source.getOwningInstitutionHoldingsId(), destination.getOwningInstitutionHoldingsId())) {
                    validSourceBibAndHoldings(source, institutionId, transferValidationResponse);
                    if(transferValidationResponse.isValid()) {
                        validDestinationBibAndHoldings(destination, institutionId, transferValidationResponse);
                    } else {
                        return transferValidationResponse;
                    }
                } else {
                    transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_DESTINATION_HOLDINGS_IDS_NOT_MATCHING);
                }

            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DESTINATION_EMPTY);
            }

        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_EMPTY);
        }
        return transferValidationResponse;
    }

    private HoldingsEntity getDestinationHoldings(TransferValidationResponse holdingTransferValidationResponse, Date currentDate,Integer institutionId) {
        HoldingsEntity destHoldings = holdingTransferValidationResponse.getDestHoldings();
        if(null == destHoldings) {
            destHoldings = dummyDataService.getHoldingsWithDummyDetails(institutionId, currentDate, RecapConstants.TRANSFER.TRANSFER, holdingTransferValidationResponse.getDestHoldingsId());
            destHoldings.setBibliographicEntities(new ArrayList<>());
            destHoldings.setItemEntities(new ArrayList<>());
        }
        return destHoldings;
    }

    private BibliographicEntity getDestinationBib(TransferValidationResponse transferValidationResponse, Date currentDate, Integer institutionId) {
        BibliographicEntity destBib = transferValidationResponse.getDestBib();
        if(null == destBib) {
            destBib = new BibliographicEntity();
            dummyDataService.updateBibWithDummyDetails(institutionId, destBib, currentDate, RecapConstants.TRANSFER.TRANSFER, transferValidationResponse.getDestinationBibId());
            destBib.setItemEntities(new ArrayList<>());
            destBib.setHoldingsEntities(new ArrayList<>());
        }
        return destBib;
    }

    private TransferValidationResponse validDestinationBibAndHoldings(Destination destination, Integer owningInstitutionId,
                                                                      TransferValidationResponse transferValidationResponse) {
        String owningInstitutionBibId = destination.getOwningInstitutionBibId();
        if(StringUtils.isNotBlank(owningInstitutionBibId)) {
            String owningInstitutionHoldingsId = destination.getOwningInstitutionHoldingsId();
            if(StringUtils.isNotBlank(owningInstitutionHoldingsId)) {
                BibliographicEntity destinationBibEntity =
                        bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(owningInstitutionId, owningInstitutionBibId);
                if(null != destinationBibEntity) {
                    transferValidationResponse.setDestBib(destinationBibEntity);
                } else {
                    transferValidationResponse.setDestinationBibId(owningInstitutionBibId);
                }
            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_OWN_INST_HOLDINGS_ID_EMPTY);
            }

        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.DEST_OWN_INST_BIB_ID_EMPTY);
        }
        return transferValidationResponse;
    }

    private void validSourceBibAndHoldings(Source source, Integer owningInstitutionId, TransferValidationResponse transferValidationResponse) {
        String owningInstitutionBibId = source.getOwningInstitutionBibId();
        if(StringUtils.isNotBlank(owningInstitutionBibId)) {
            String owningInstitutionHoldingsId = source.getOwningInstitutionHoldingsId();
            if(StringUtils.isNotBlank(owningInstitutionHoldingsId)) {
                BibliographicEntity sourceBibEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(owningInstitutionId, owningInstitutionBibId);
                if(null != sourceBibEntity) {
                    transferValidationResponse.setSourceBib(sourceBibEntity);
                    HoldingsEntity holdingsEntity = matchHoldingIdWithHoldings(owningInstitutionHoldingsId, sourceBibEntity);
                    if (holdingsEntity == null) {
                        transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_HOLDING_NOT_UNDER_SOURCE_BIB);
                    } else {
                        transferValidationResponse.setSourceHoldings(holdingsEntity);
                    }
                } else {
                    transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_BIB_NOT_EXIST);
                }
            } else {
                transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_OWN_INST_HOLDINGS_ID_EMPTY);
            }
        } else {
            transferValidationResponse.setInvalidWithMessage(RecapConstants.TRANSFER.SOURCE_OWN_INST_BIB_ID_EMPTY);
        }
    }

    private HoldingsEntity matchHoldingIdWithHoldings(String owningInstitutionHoldingsId, BibliographicEntity bibliographicEntity) {
        List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
        if(CollectionUtils.isNotEmpty(holdingsEntities)) {
            for (Iterator<HoldingsEntity> iterator = holdingsEntities.iterator(); iterator.hasNext(); ) {
                HoldingsEntity holdingsEntity = iterator.next();
                if(StringUtils.equals(holdingsEntity.getOwningInstitutionHoldingsId(), owningInstitutionHoldingsId)) {
                    return holdingsEntity;
                }
            }
        }
        return null;
    }

    /**
     * Create report data entity list for transfer API.
     *
     *
     * @param institution
     * @param requestString
     * @param responseString
     * @param transferType
     * @return the list of ReportDataEntity
     */
    public List<ReportDataEntity> createReportDataEntityList(String institution, String requestString, String responseString, String transferType){
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntityTransferType = new ReportDataEntity();
        reportDataEntityTransferType.setHeaderName(RecapConstants.TRANSFER.TRANSFER_TYPE);
        reportDataEntityTransferType.setHeaderValue(transferType);
        reportDataEntityList.add(reportDataEntityTransferType);

        ReportDataEntity reportDataEntityInstitution = new ReportDataEntity();
        reportDataEntityInstitution.setHeaderName(RecapConstants.TRANSFER.INSTITUTION);
        reportDataEntityInstitution.setHeaderValue(institution);
        reportDataEntityList.add(reportDataEntityInstitution);

        ReportDataEntity reportDataEntityRequest = new ReportDataEntity();
        reportDataEntityRequest.setHeaderName(RecapConstants.TRANSFER.REQUEST);
        reportDataEntityRequest.setHeaderValue(requestString);
        reportDataEntityList.add(reportDataEntityRequest);

        ReportDataEntity reportDataEntityResponse = new ReportDataEntity();
        reportDataEntityResponse.setHeaderName(RecapConstants.TRANSFER.RESPONSE);
        reportDataEntityResponse.setHeaderValue(responseString);
        reportDataEntityList.add(reportDataEntityResponse);
        return reportDataEntityList;
    }

    public void saveReportForTransfer(String requestString, String responseString, String institution, String transferType) {
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();

        reportDataEntityList.addAll(createReportDataEntityList(institution,
                requestString, responseString, transferType));
        helperUtil.saveReportEntity(institution,RecapConstants.TRANSFER_REPORT, RecapConstants.TRANSFER_REPORT, reportDataEntityList);
    }

    public void writeChangeLog(Integer sourceBibId, Integer sourceHoldingsId, List<ItemEntity> itemEntities, Integer destinationBibId, Integer destinationHoldingsId) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Item transfered from source (bibId : ")
                .append(sourceBibId). append(" , holdingsId : "). append(sourceHoldingsId).append(") to destination (bibId : ")
                .append(destinationBibId). append(" , holdingsId : "). append(destinationHoldingsId).append(")");
        helperUtil.saveItemChangeLogEntity(RecapConstants.TRANSFER.TRANSFER, stringBuffer.toString(), itemEntities);
    }

    class TransferValidationResponse {
        private boolean valid;
        private String message;
        private BibliographicEntity sourceBib;
        private BibliographicEntity destBib;
        private HoldingsEntity sourceHoldings;
        private HoldingsEntity destHoldings;
        private ItemEntity sourceItem;
        private String destinationBibId;
        private String destHoldingsId;

        public TransferValidationResponse(boolean valid) {
            this.valid = valid;
        }

        public TransferValidationResponse(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public BibliographicEntity getSourceBib() {
            return sourceBib;
        }

        public void setSourceBib(BibliographicEntity sourceBib) {
            this.sourceBib = sourceBib;
        }

        public BibliographicEntity getDestBib() {
            return destBib;
        }

        public void setDestBib(BibliographicEntity destBib) {
            this.destBib = destBib;
        }

        public HoldingsEntity getSourceHoldings() {
            return sourceHoldings;
        }

        public void setSourceHoldings(HoldingsEntity sourceHoldings) {
            this.sourceHoldings = sourceHoldings;
        }

        public String getDestinationBibId() {
            return destinationBibId;
        }

        public void setDestinationBibId(String destinationBibId) {
            this.destinationBibId = destinationBibId;
        }

        public HoldingsEntity getDestHoldings() {
            return destHoldings;
        }

        public void setDestHoldings(HoldingsEntity destHoldings) {
            this.destHoldings = destHoldings;
        }

        public ItemEntity getSourceItem() {
            return sourceItem;
        }

        public void setSourceItem(ItemEntity sourceItem) {
            this.sourceItem = sourceItem;
        }

        public String getDestHoldingsId() {
            return destHoldingsId;
        }

        public void setDestHoldingsId(String destHoldingsId) {
            this.destHoldingsId = destHoldingsId;
        }

        public void setInvalidWithMessage(String message) {
            this.setValid(false);
            this.setMessage(message);
        }
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }
}
