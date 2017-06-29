package org.recap.service.accession.callable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.service.accession.BulkAccessionService;
import org.recap.service.accession.resolver.BibDataResolver;
import org.recap.util.AccessionHelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by sheiks on 26/05/17.
 */
@Component
@Scope("prototype")
public class BibDataCallable implements Callable{
    private static final Logger logger = LoggerFactory.getLogger(BibDataCallable.class);
    @Autowired
    private BulkAccessionService bulkAccessionService;

    @Autowired
    private AccessionHelperUtil accessionHelperUtil;
    private AccessionRequest accessionRequest;
    private boolean writeToReport;

    @Override
    public Object call() throws Exception {

        List<Map<String, String>> responseMapList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        Set<AccessionResponse> accessionResponses = new HashSet<>();
        String response = null;
        String itemBarcode = accessionRequest.getItemBarcode();
        String customerCode = accessionRequest.getCustomerCode();
        String owningInstitution = accessionHelperUtil.getOwningInstitution(customerCode);
        List<ItemEntity> itemEntityList = accessionHelperUtil.getItemEntityList(itemBarcode, customerCode);
        boolean itemExists = accessionHelperUtil.checkItemBarcodeAlreadyExist(itemEntityList);
        boolean isDeaccessionedItem = accessionHelperUtil.isItemDeaccessioned(itemEntityList);

        if(!itemExists) {
            if(StringUtils.isBlank(owningInstitution)) {
                accessionHelperUtil.setAccessionResponse(accessionResponses, accessionRequest.getItemBarcode(), RecapConstants.OWNING_INST_EMPTY);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.OWNING_INST_EMPTY));
            } else if (accessionRequest.getItemBarcode().length() > 45) {
                accessionHelperUtil.setAccessionResponse(accessionResponses, accessionRequest.getItemBarcode(), RecapConstants.INVALID_BARCODE_LENGTH);
                reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, RecapConstants.INVALID_BARCODE_LENGTH));
            } else {
                    try {
                        StopWatch individualStopWatch = new StopWatch();
                        individualStopWatch.start();
                        for (Iterator<BibDataResolver> iterator = getBibDataResolvers().iterator(); iterator.hasNext(); ) {
                            BibDataResolver bibDataResolver = iterator.next();
                            if (bibDataResolver.isInterested(owningInstitution)) {
                                String bibData = null;
                                try {
                                    bibData = bibDataResolver.getBibData(itemBarcode, customerCode);
                                } catch (Exception e) {
                                    accessionHelperUtil.processBibDataApiException(accessionResponses, accessionRequest, reportDataEntityList, owningInstitution, e);
                                    break;
                                }
                                try {
                                    response = bibDataResolver.processXml(accessionResponses, bibData,
                                            responseMapList, owningInstitution, reportDataEntityList, accessionRequest);
                                } catch (Exception e) {
                                    if(writeToReport) {
                                        processException(e, reportDataEntityList, accessionResponses);
                                    } else {
                                        return accessionRequest;
                                    }
                                }
                                break;
                            }
                        }
                        individualStopWatch.stop();
                        logger.info("Time taken to get bib data from {} ILS : {}" , owningInstitution, individualStopWatch.getTotalTimeSeconds());
                    } catch (Exception ex) {
                        response = processException(ex, reportDataEntityList, accessionResponses);
                }
                accessionHelperUtil.generateAccessionSummaryReport(responseMapList, owningInstitution);
            }
        } else if(isDeaccessionedItem){
            response = bulkAccessionService.reAccessionItem(itemEntityList);
            if (response.equals(RecapConstants.SUCCESS)) {
                response = bulkAccessionService.indexReaccessionedItem(itemEntityList);
                bulkAccessionService.saveItemChangeLogEntity(RecapConstants.REACCESSION,RecapConstants.ITEM_ISDELETED_TRUE_TO_FALSE,itemEntityList);
            }
            accessionHelperUtil.setAccessionResponse(accessionResponses, accessionRequest.getItemBarcode(), response);
            reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
        } else {
            String itemAreadyAccessionedMessage;
            if(CollectionUtils.isNotEmpty(itemEntityList.get(0).getBibliographicEntities())) {
                String itemAreadyAccessionedOwnInstBibId = itemEntityList.get(0).getBibliographicEntities() != null ? itemEntityList.get(0).getBibliographicEntities().get(0).getOwningInstitutionBibId() : " ";
                String itemAreadyAccessionedOwnInstHoldingId = itemEntityList.get(0).getHoldingsEntities() != null ? itemEntityList.get(0).getHoldingsEntities().get(0).getOwningInstitutionHoldingsId() : " ";
                itemAreadyAccessionedMessage = RecapConstants.ITEM_ALREADY_ACCESSIONED + RecapConstants.OWN_INST_BIB_ID + itemAreadyAccessionedOwnInstBibId + RecapConstants.OWN_INST_HOLDING_ID + itemAreadyAccessionedOwnInstHoldingId + RecapConstants.OWN_INST_ITEM_ID + itemEntityList.get(0).getOwningInstitutionItemId();
            } else {
                itemAreadyAccessionedMessage = RecapConstants.ITEM_ALREADY_ACCESSIONED;
            }
            accessionHelperUtil.setAccessionResponse(accessionResponses, accessionRequest.getItemBarcode(), itemAreadyAccessionedMessage);
            reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, itemAreadyAccessionedMessage));
        }
        bulkAccessionService.saveReportEntity(owningInstitution, reportDataEntityList);
        return accessionResponses;
    }

    private String processException(Exception ex, List<ReportDataEntity> reportDataEntityList, Set<AccessionResponse> accessionResponses) {
        String response;
        logger.error(RecapConstants.LOG_ERROR, ex);
        ex.printStackTrace();
        response = RecapConstants.EXCEPTION + ex.getMessage();
        accessionHelperUtil.setAccessionResponse(accessionResponses, accessionRequest.getItemBarcode(), response);
        reportDataEntityList.addAll(accessionHelperUtil.createReportDataEntityList(accessionRequest, response));
        return response;
    }

    public void setAccessionRequest(AccessionRequest accessionRequest) {
        this.accessionRequest = accessionRequest;
    }

    public List<BibDataResolver> getBibDataResolvers() {
        return bulkAccessionService.getBibDataResolvers();
    }

    public void setWriteToReport(boolean writeToReport) {
        this.writeToReport = writeToReport;
    }
}
