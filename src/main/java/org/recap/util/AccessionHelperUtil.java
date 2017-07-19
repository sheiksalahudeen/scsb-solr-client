package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.*;
import org.recap.model.request.ItemCheckInRequest;
import org.recap.model.request.ItemCheckinResponse;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.ItemBarcodeHistoryDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.resolver.BibDataResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
@EnableAsync
public class AccessionHelperUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccessionHelperUtil.class);

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Autowired
    private ItemBarcodeHistoryDetailsRepository itemBarcodeHistoryDetailsRepository;

    @Autowired
    private AccessionService accessionService;

    @Value("${scsb.url}")
    private String scsbUrl;


    public Object processRecords(Set<AccessionResponse> accessionResponses, List<Map<String, String>> responseMaps,
                               AccessionRequest accessionRequest, List<ReportDataEntity> reportDataEntitys,
                               String owningInstitution, boolean writeToReport) {
        String customerCode = accessionRequest.getCustomerCode();
        String itemBarcode = accessionRequest.getItemBarcode();
        // Check item availability
        List<ItemEntity> itemEntityList = getItemEntityList(itemBarcode, customerCode);
        boolean itemExists = checkItemBarcodeAlreadyExist(itemEntityList);
        if(itemExists) { // If available check deaccessioned item or not

            boolean isDeaccessionedItem = isItemDeaccessioned(itemEntityList);
            if(isDeaccessionedItem) { // If deacccessioned item make it available
                String response = accessionService.reAccessionItem(itemEntityList);
                if (response.equals(RecapConstants.SUCCESS)) {
                    response = accessionService.indexReaccessionedItem(itemEntityList);
                    accessionService.saveItemChangeLogEntity(RecapConstants.REACCESSION,RecapConstants.ITEM_ISDELETED_TRUE_TO_FALSE,itemEntityList);
                }
                setAccessionResponse(accessionResponses, itemBarcode, response);
                reportDataEntitys.addAll(createReportDataEntityList(accessionRequest, response));
            } else { // else, error response
                String itemAreadyAccessionedMessage;
                if(CollectionUtils.isNotEmpty(itemEntityList.get(0).getBibliographicEntities())) {
                    String itemAreadyAccessionedOwnInstBibId = itemEntityList.get(0).getBibliographicEntities() != null ? itemEntityList.get(0).getBibliographicEntities().get(0).getOwningInstitutionBibId() : " ";
                    String itemAreadyAccessionedOwnInstHoldingId = itemEntityList.get(0).getHoldingsEntities() != null ? itemEntityList.get(0).getHoldingsEntities().get(0).getOwningInstitutionHoldingsId() : " ";
                    itemAreadyAccessionedMessage = RecapConstants.ITEM_ALREADY_ACCESSIONED + RecapConstants.OWN_INST_BIB_ID + itemAreadyAccessionedOwnInstBibId + RecapConstants.OWN_INST_HOLDING_ID + itemAreadyAccessionedOwnInstHoldingId + RecapConstants.OWN_INST_ITEM_ID + itemEntityList.get(0).getOwningInstitutionItemId();
                } else {
                    itemAreadyAccessionedMessage = RecapConstants.ITEM_ALREADY_ACCESSIONED;
                }
                setAccessionResponse(accessionResponses, itemBarcode, itemAreadyAccessionedMessage);
                reportDataEntitys.addAll(createReportDataEntityList(accessionRequest, itemAreadyAccessionedMessage));
            }

        } else { // If not available

            // Call ILS - Bib Data API
            StopWatch individualStopWatch = new StopWatch();
            individualStopWatch.start();
            for (Iterator<BibDataResolver> bibDataResolverIterator = accessionService.getBibDataResolvers().iterator(); bibDataResolverIterator.hasNext(); ) {
                BibDataResolver bibDataResolver = bibDataResolverIterator.next();
                if (bibDataResolver.isInterested(owningInstitution)) {
                    String bibData = null;
                    try {
                        // Calling ILS - Bib Data API
                        bibData = bibDataResolver.getBibData(itemBarcode, customerCode);
                    } catch (Exception e) { // Process dummy record if record not found in ILS
                        processException(accessionResponses, accessionRequest, reportDataEntitys, owningInstitution, e);
                        break;
                    } finally {
                        individualStopWatch.stop();
                        logger.info("Time taken to get bib data from {} ILS : {}" , owningInstitution, individualStopWatch.getTotalTimeSeconds());
                    }
                    try { // Check whether owningInsitutionItemId attached with another barcode.

                        Object unmarshalObject = bibDataResolver.unmarshal(bibData);
                        ItemEntity itemEntity = bibDataResolver.getItemEntityFromRecord(unmarshalObject);
                        boolean accessionProcess = bibDataResolver.isAccessionProcess(itemEntity, owningInstitution);

                        // Process XML Record
                        if(accessionProcess) { // Accession process
                            processXMLForAccession(accessionResponses, responseMaps, accessionRequest, reportDataEntitys,
                                    owningInstitution, bibDataResolver, unmarshalObject);
                        } else {  // If attached

                            String oldBarcode = itemEntity.getBarcode();
                            // update item record with new barcode. Accession Process
                            processXMLForAccession(accessionResponses, responseMaps, accessionRequest, reportDataEntitys,
                                    owningInstitution, bibDataResolver, unmarshalObject);
                            // Move item record information to history table
                            ItemBarcodeHistoryEntity itemBarcodeHistoryEntity = prepareBarcodeHistoryEntity(itemEntity, itemBarcode, oldBarcode);
                            itemBarcodeHistoryDetailsRepository.save(itemBarcodeHistoryEntity);
                        }
                    } catch (Exception e) {
                        if(writeToReport) {
                            processException(accessionResponses, accessionRequest, reportDataEntitys, owningInstitution, e);
                        } else {
                            return accessionRequest;
                        }
                    }
                    generateAccessionSummaryReport(responseMaps, owningInstitution);
                    break;
                }
            }

        }

        // Save report
        accessionService.saveReportEntity(owningInstitution, reportDataEntitys);

        return accessionResponses;
    }

    private void processXMLForAccession(Set<AccessionResponse> accessionResponses, List<Map<String, String>> responseMaps, AccessionRequest accessionRequest, List<ReportDataEntity> reportDataEntitys, String owningInstitution, BibDataResolver bibDataResolver, Object unmarshalObject) throws Exception {
        String response = bibDataResolver.processXml(accessionResponses, unmarshalObject,
                responseMaps, owningInstitution, reportDataEntitys, accessionRequest);
        callCheckin(accessionRequest.getItemBarcode(),owningInstitution);
    }


    /**
     * Gets owning institution for the given customer code.
     *
     * @param customerCode the customer code
     * @return the owning institution
     */
    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            CustomerCodeEntity customerCodeEntity = customerCodeDetailsRepository.findByCustomerCode(customerCode);
            if (null != customerCodeEntity) {
                owningInstitution = customerCodeEntity.getInstitutionEntity().getInstitutionCode();
            }
        } catch (Exception e) {
            logger.error(RecapConstants.EXCEPTION,e);
        }
        return owningInstitution;
    }

    /**
     * Get item entity list for the given item barcode and customer code.
     *
     * @param itemBarcode  the item barcode
     * @param customerCode the customer code
     * @return the list
     */
    public List<ItemEntity> getItemEntityList(String itemBarcode, String customerCode){
        return itemDetailsRepository.findByBarcodeAndCustomerCode(itemBarcode,customerCode);
    }

    /**
     * This method checks item barcode already exist for the given item list.
     *
     * @param itemEntityList the item entity list
     * @return the boolean
     */
    public boolean checkItemBarcodeAlreadyExist(List<ItemEntity> itemEntityList){
        boolean itemExists = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            itemExists = true;
        }
        return itemExists;
    }

    /**
     * This method checks is item deaccessioned for the given item list.
     *
     * @param itemEntityList the item entity list
     * @return the boolean
     */
    public boolean isItemDeaccessioned(List<ItemEntity> itemEntityList){
        boolean itemDeleted = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            for(ItemEntity itemEntity : itemEntityList){
                return itemEntity.isDeleted();
            }
        }
        return itemDeleted;
    }

    /**
     * Sets accession response.
     *
     * @param accessionResponseList the accession response list
     * @param itemBarcode           the item barcode
     * @param message               the message
     */
    public void setAccessionResponse(Set<AccessionResponse> accessionResponseList, String itemBarcode, String message){
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode(itemBarcode);
        accessionResponse.setMessage(message);
        accessionResponseList.add(accessionResponse);
    }

    /**
     * Create report data entity list for accessioned item.
     *
     * @param accessionRequest the accession request
     * @param response         the response
     * @return the list
     */
    public List<ReportDataEntity> createReportDataEntityList(AccessionRequest accessionRequest,String response){
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        if(StringUtils.isNotBlank(accessionRequest.getCustomerCode())) {
            ReportDataEntity reportDataEntityCustomerCode = new ReportDataEntity();
            reportDataEntityCustomerCode.setHeaderName(RecapConstants.CUSTOMER_CODE);
            reportDataEntityCustomerCode.setHeaderValue(accessionRequest.getCustomerCode());
            reportDataEntityList.add(reportDataEntityCustomerCode);
        }
        if(StringUtils.isNotBlank(accessionRequest.getItemBarcode())) {
            ReportDataEntity reportDataEntityItemBarcode = new ReportDataEntity();
            reportDataEntityItemBarcode.setHeaderName(RecapConstants.ITEM_BARCODE);
            reportDataEntityItemBarcode.setHeaderValue(accessionRequest.getItemBarcode());
            reportDataEntityList.add(reportDataEntityItemBarcode);
        }
        ReportDataEntity reportDataEntityMessage = new ReportDataEntity();
        reportDataEntityMessage.setHeaderName(RecapConstants.MESSAGE);
        reportDataEntityMessage.setHeaderValue(response);
        reportDataEntityList.add(reportDataEntityMessage);
        return reportDataEntityList;
    }

    /**
     * This method is used to generate AccessionSummary Report
     * <p>
     * It saves the data in report_t and report_data_t
     *
     * @param responseMapList   the response map list
     * @param owningInstitution the owning institution
     */
    public void generateAccessionSummaryReport(List<Map<String,String>> responseMapList,String owningInstitution){
        int successBibCount = 0;
        int successItemCount = 0;
        int failedBibCount = 0;
        int failedItemCount = 0;
        int exitsBibCount = 0;
        String reasonForFailureBib = "";
        String reasonForFailureItem = "";

        for(Map responseMap : responseMapList){
            successBibCount = successBibCount + (responseMap.get(RecapConstants.SUCCESS_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.SUCCESS_BIB_COUNT) : 0);
            failedBibCount = failedBibCount + (responseMap.get(RecapConstants.FAILED_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.FAILED_BIB_COUNT) : 0);
            if(failedBibCount == 0){
                if(StringUtils.isEmpty((String)responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))){
                    successItemCount = 1;
                }else{
                    failedItemCount = 1;
                }
            }
            exitsBibCount = exitsBibCount + (responseMap.get(RecapConstants.EXIST_BIB_COUNT)!=null ? (Integer) responseMap.get(RecapConstants.EXIST_BIB_COUNT) : 0);

            if (!StringUtils.isEmpty((String) responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE)) && !reasonForFailureBib.contains(responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE).toString())) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(responseMap.get(RecapConstants.REASON_FOR_BIB_FAILURE));
                stringBuilder.append(",");
                stringBuilder.append(reasonForFailureBib);
                reasonForFailureBib = stringBuilder.toString();
            }
            if ((!StringUtils.isEmpty((String) responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))) && StringUtils.isEmpty(reasonForFailureBib) &&
                    !reasonForFailureItem.contains((String) responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(responseMap.get(RecapConstants.REASON_FOR_ITEM_FAILURE));
                stringBuilder.append(",");
                stringBuilder.append(reasonForFailureItem);
                reasonForFailureItem = stringBuilder.toString();
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
        reasonForFailureBib = reasonForFailureBib.replaceAll("\n",",");
        reasonForFailureBib = reasonForFailureBib.replaceAll(",$", "");
        reasonForBibFailureReportDataEntity.setHeaderValue(reasonForFailureBib);
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_ITEM_REASON);
        if(reasonForFailureItem.startsWith("\n")){
            reasonForFailureItem = reasonForFailureItem.substring(1,reasonForFailureItem.length()-1);
        }
        reasonForFailureItem = reasonForFailureItem.replaceAll("\n",",");
        reasonForFailureItem = reasonForFailureItem.replaceAll(",$", "");
        reasonForItemFailureReportDataEntity.setHeaderValue(reasonForFailureItem);
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        reportDetailRepository.save(reportEntityList);
    }

    public void processException(Set<AccessionResponse> accessionResponsesList, AccessionRequest accessionRequest,
                                 List<ReportDataEntity> reportDataEntityList, String owningInstitution, Exception ex) {
        String response = ex.getMessage();
        if(StringUtils.equalsIgnoreCase(response, RecapConstants.ITEM_BARCODE_NOT_FOUND_MSG)) {
            logger.error(RecapConstants.LOG_ERROR + response);
        } else {
            response = RecapConstants.EXCEPTION + response;
            ex.printStackTrace();
        }
        //Create dummy record
        response = accessionService.createDummyRecordIfAny(response, owningInstitution, reportDataEntityList, accessionRequest);
        accessionService.getAccessionHelperUtil().setAccessionResponse(accessionResponsesList, accessionRequest.getItemBarcode(), response);
        reportDataEntityList.addAll(accessionService.getAccessionHelperUtil().createReportDataEntityList(accessionRequest, response));
    }


    public List<AccessionRequest> removeDuplicateRecord(List<AccessionRequest> trimmedAccessionRequests) {
        Set<AccessionRequest> accessionRequests = new HashSet<>(trimmedAccessionRequests);
        return new ArrayList<>(accessionRequests);
    }

    @Async
    public void callCheckin(String itemBarcode, String owningInstitutionId) {
        ResponseEntity<ItemCheckinResponse> responseEntity = null;
        ItemCheckInRequest itemRequestInfo = new ItemCheckInRequest();
        RestTemplate restTemplate = new RestTemplate();
        try {
            itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcode));
            itemRequestInfo.setItemOwningInstitution(owningInstitutionId);
            HttpEntity request = new HttpEntity<>(itemRequestInfo,getHttpHeadersAuth());
            responseEntity =restTemplate.exchange(scsbUrl + RecapConstants.SERVICE_PATH.CHECKIN_ITEM, HttpMethod.POST,request  , ItemCheckinResponse.class);
        } catch (RestClientException ex) {
            logger.error(RecapConstants.EXCEPTION, ex);
        } catch (Exception ex) {
            logger.error(RecapConstants.EXCEPTION, ex);
        }
    }

    private HttpHeaders getHttpHeadersAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
        return headers;
    }

    public ItemBarcodeHistoryEntity prepareBarcodeHistoryEntity(ItemEntity itemEntity, String newBarcode, String oldBarcode) {
        ItemBarcodeHistoryEntity itemBarcodeHistoryEntity = new ItemBarcodeHistoryEntity();
        itemBarcodeHistoryEntity.setOwningingInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
        itemBarcodeHistoryEntity.setOwningingInstitutionItemId(itemEntity.getOwningInstitutionItemId());
        itemBarcodeHistoryEntity.setOldBarcode(oldBarcode);
        itemBarcodeHistoryEntity.setNewBarcode(newBarcode);
        itemBarcodeHistoryEntity.setCreatedDate(new Date());
        return itemBarcodeHistoryEntity;
    }

}
