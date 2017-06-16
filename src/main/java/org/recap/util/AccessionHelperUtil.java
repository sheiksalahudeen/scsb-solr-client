package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by sheiks on 26/05/17.
 */
@Service
public class AccessionHelperUtil {

    private static final Logger logger = LoggerFactory.getLogger(AccessionHelperUtil.class);

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private ReportDetailRepository reportDetailRepository;

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

    public List<ItemEntity> getItemEntityList(String itemBarcode, String customerCode){
        return itemDetailsRepository.findByBarcodeAndCustomerCode(itemBarcode,customerCode);
    }

    public boolean checkItemBarcodeAlreadyExist(List<ItemEntity> itemEntityList){
        boolean itemExists = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            itemExists = true;
        }
        return itemExists;
    }

    public boolean isItemDeaccessioned(List<ItemEntity> itemEntityList){
        boolean itemDeleted = false;
        if (itemEntityList != null && !itemEntityList.isEmpty()) {
            for(ItemEntity itemEntity : itemEntityList){
                return itemEntity.isDeleted();
            }
        }
        return itemDeleted;
    }

    public boolean isItemBarcodeEmpty(String itemBarcode) {
        if(StringUtils.isBlank(itemBarcode)) {
            return true;
        }
        return false;
    }

    public void setAccessionResponse(Set<AccessionResponse> accessionResponseList, String itemBarcode, String message){
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode(itemBarcode);
        accessionResponse.setMessage(message);
        accessionResponseList.add(accessionResponse);
    }

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

}
