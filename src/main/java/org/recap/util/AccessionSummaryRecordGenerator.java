package org.recap.util;

import org.recap.RecapConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.springframework.util.StringUtils;
import java.util.*;

/**
 * Created by hemalathas on 22/11/16.
 */
public class AccessionSummaryRecordGenerator {

    /**
     * This method is used to prepare accession summary report.
     *
     * @param reportEntityList the report entity list
     * @return the list
     */
    public List<AccessionSummaryRecord> prepareAccessionSummaryReportRecord(List<ReportEntity> reportEntityList){
        Integer bibSuccessCount = 0;
        Integer itemSuccessCount = 0;
        Integer bibFailureCount = 0;
        Integer itemFailureCount = 0;
        Integer existingBibCount = 0;
        List<AccessionSummaryRecord> accessionSummaryRecordList = new ArrayList<>();
        Map<String,Integer> bibFailureReasonCountMap = new HashMap<>();
        Map<String,Integer> itemFailureReasonCountMap = new HashMap<>();

        for(ReportEntity reportEntity : reportEntityList){
            for(ReportDataEntity reportDataEntity : reportEntity.getReportDataEntities()){
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.BIB_SUCCESS_COUNT)){
                    bibSuccessCount = bibSuccessCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.ITEM_SUCCESS_COUNT)){
                    itemSuccessCount = itemSuccessCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.BIB_FAILURE_COUNT)){
                    bibFailureCount = Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.ITEM_FAILURE_COUNT)){
                    itemFailureCount = Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.NUMBER_OF_BIB_MATCHES)){
                    existingBibCount = existingBibCount + Integer.parseInt(reportDataEntity.getHeaderValue());
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.FAILURE_BIB_REASON) && !StringUtils.isEmpty(reportDataEntity.getHeaderValue())){
                    Integer bibCount = bibFailureReasonCountMap.get(reportDataEntity.getHeaderValue());
                    if(bibCount != null){
                        bibFailureReasonCountMap.put(reportDataEntity.getHeaderValue(),bibCount+bibFailureCount);
                    }else{
                        bibFailureReasonCountMap.put(reportDataEntity.getHeaderValue(),bibFailureCount);
                    }
                }
                if(reportDataEntity.getHeaderName().equalsIgnoreCase(RecapConstants.FAILURE_ITEM_REASON) && !StringUtils.isEmpty(reportDataEntity.getHeaderValue())){
                    Integer itemCount = itemFailureReasonCountMap.get(reportDataEntity.getHeaderValue());
                    if(itemCount != null){
                        itemFailureReasonCountMap.put(reportDataEntity.getHeaderValue(),itemCount+itemFailureCount);
                    }else{
                        itemFailureReasonCountMap.put(reportDataEntity.getHeaderValue(),itemFailureCount);
                    }
                }
            }
        }

        AccessionSummaryRecord accessionSummaryRecord = new AccessionSummaryRecord();
        accessionSummaryRecord.setSuccessBibCount(bibSuccessCount.toString());
        accessionSummaryRecord.setSuccessItemCount(itemSuccessCount.toString());
        accessionSummaryRecord.setNoOfBibMatches(existingBibCount.toString());
        if(bibFailureReasonCountMap.size() != 0){
            Map.Entry<String, Integer> bibEntry = bibFailureReasonCountMap.entrySet().iterator().next();
            accessionSummaryRecord.setReasonForFailureBib(bibEntry.getKey());
            accessionSummaryRecord.setFailedBibCount(bibEntry.getValue().toString());
            bibFailureReasonCountMap.remove(bibEntry.getKey());
        }
        if(itemFailureReasonCountMap.size() != 0){
            Map.Entry<String, Integer> ItemEntry = itemFailureReasonCountMap.entrySet().iterator().next();
            accessionSummaryRecord.setReasonForFailureItem(ItemEntry.getKey());
            accessionSummaryRecord.setFailedItemCount(ItemEntry.getValue().toString());
            itemFailureReasonCountMap.remove(ItemEntry.getKey());
        }
        accessionSummaryRecordList.add(accessionSummaryRecord);

        if(itemFailureReasonCountMap.size() != 0 && bibFailureReasonCountMap.size() <= itemFailureReasonCountMap.size()){
            int count =0;
            while (count < bibFailureReasonCountMap.size()){
                AccessionSummaryRecord accessionSummaryRec1 = new AccessionSummaryRecord();
                Map.Entry<String, Integer> bibEntries = bibFailureReasonCountMap.entrySet().iterator().next();
                accessionSummaryRec1.setReasonForFailureBib(bibEntries.getKey());
                accessionSummaryRec1.setFailedBibCount(bibEntries.getValue().toString());
                bibFailureReasonCountMap.remove(bibEntries.getKey());
                Map.Entry<String, Integer> ItemEntries = itemFailureReasonCountMap.entrySet().iterator().next();
                accessionSummaryRec1.setReasonForFailureItem(ItemEntries.getKey());
                accessionSummaryRec1.setFailedItemCount(ItemEntries.getValue().toString());
                itemFailureReasonCountMap.remove(ItemEntries.getKey());
                accessionSummaryRecordList.add(accessionSummaryRec1);
                count+=1;
            }
            if(itemFailureReasonCountMap.size() != 0){
                for(String key : itemFailureReasonCountMap.keySet()){
                    AccessionSummaryRecord accessionSummaryRec = new AccessionSummaryRecord();
                    accessionSummaryRec.setReasonForFailureItem(key);
                    accessionSummaryRec.setFailedItemCount(itemFailureReasonCountMap.get(key).toString());
                    accessionSummaryRecordList.add(accessionSummaryRec);
                }
            }
        }else if(bibFailureReasonCountMap.size() != 0 && bibFailureReasonCountMap.size() > itemFailureReasonCountMap.size()){
            int count =0;
            while (count < itemFailureReasonCountMap.size()){
                AccessionSummaryRecord accessionSummaryRec2 = new AccessionSummaryRecord();
                Map.Entry<String, Integer> bibEntries = bibFailureReasonCountMap.entrySet().iterator().next();
                accessionSummaryRec2.setReasonForFailureBib(bibEntries.getKey());
                accessionSummaryRec2.setFailedBibCount(bibEntries.getValue().toString());
                bibFailureReasonCountMap.remove(bibEntries.getKey());
                Map.Entry<String, Integer> ItemEntries = itemFailureReasonCountMap.entrySet().iterator().next();
                accessionSummaryRec2.setReasonForFailureItem(ItemEntries.getKey());
                accessionSummaryRec2.setFailedItemCount(ItemEntries.getValue().toString());
                itemFailureReasonCountMap.remove(ItemEntries.getKey());
                accessionSummaryRecordList.add(accessionSummaryRec2);
                count+=1;
            }
            if(bibFailureReasonCountMap.size() != 0){
                for(String key : bibFailureReasonCountMap.keySet()){
                    AccessionSummaryRecord accessionSummaryRec = new AccessionSummaryRecord();
                    accessionSummaryRec.setReasonForFailureBib(key);
                    accessionSummaryRec.setFailedBibCount(bibFailureReasonCountMap.get(key).toString());
                    accessionSummaryRecordList.add(accessionSummaryRec);
                }
            }
        }
        return accessionSummaryRecordList;
    }

}
