package org.recap.matchingalgorithm.service;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingBibInfoDetail;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.MatchingBibInfoDetailRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by premkb on 28/1/17.
 */
@Service
public class MatchingBibInfoDetailService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingBibInfoDetailService.class);

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Autowired
    private MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;

    @Autowired
    private ReportDataDetailsRepository reportDataDetailsRepository;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    Integer batchSize;

    public String populateMatchingBibInfo(Date fromDate, Date toDate) {
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        List<String> headerNameList = new ArrayList<>();
        headerNameList.add(RecapConstants.BIB_ID);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION_BIB_ID);
        Integer matchingCount = reportDetailRepository.getCountByTypeAndFileNameAndDateRange(typeList, RecapConstants.ONGOING_MATCHING_ALGORITHM, fromDate, toDate);
        logger.info("matchingReports Count ------> {} ",matchingCount);
        Integer pageCount = getPageCount(matchingCount,batchSize);
        logger.info("Total pages ---> {}",pageCount);
        StopWatch stopWatchFull = new StopWatch();
        stopWatchFull.start();
        for(int pageNum=0; pageNum<pageCount; pageNum++) {
            logger.info("Current page ---> {}", pageNum);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Page<Integer> recordNumbers = reportDetailRepository.getRecordNumByTypeAndFileNameAndDateRange(new PageRequest(pageNum, batchSize), typeList,
                    RecapConstants.ONGOING_MATCHING_ALGORITHM, fromDate, toDate);
            List<Integer> recordNumberList = recordNumbers.getContent();
            logger.info("recordNumberList size -----> {}", recordNumberList.size());
            List<ReportDataEntity> reportDataEntityList = reportDataDetailsRepository.getRecordsForMatchingBibInfo(getStringList(recordNumberList),headerNameList);
            Map<String,List<ReportDataEntity>> reportDataEntityMap = getRecordNumReportDataEntityMap(reportDataEntityList);
            List<MatchingBibInfoDetail> matchingBibInfoDetailList = findAndPopulateMatchingBibInfoDetail(reportDataEntityMap);
            matchingBibInfoDetailRepository.save(matchingBibInfoDetailList);
            matchingBibInfoDetailRepository.flush();
            stopWatch.stop();
            logger.info("Time taken to save ---> {}", stopWatch.getTotalTimeSeconds());
            logger.info("Page {} saved to db ", pageCount);
        }
        stopWatchFull.stop();
        logger.info("Loaded matching bib info in {} seconds", stopWatchFull.getTotalTimeSeconds());
        return "Success";
    }

    public String populateMatchingBibInfo(){
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        List<String> headerNameList = new ArrayList<>();
        headerNameList.add(RecapConstants.BIB_ID);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION_BIB_ID);
        Integer matchingCount = reportDetailRepository.getCountByType(typeList);
        logger.info("matchingCount------> {}", matchingCount);
        Integer pageCount = getPageCount(matchingCount,batchSize);
        logger.info("pageCount---> {} ", pageCount);
        StopWatch stopWatchFull = new StopWatch();
        stopWatchFull.start();
        for(int count=0;count<pageCount;count++){
            logger.info("Current page---> {}", count);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Page<Integer> recordNumbers = reportDetailRepository.getRecordNumByType(new PageRequest(count, batchSize),typeList);
            List<Integer> recordNumberList = recordNumbers.getContent();
            logger.info("recordNumberList size-----> {}", recordNumberList.size());
            List<ReportDataEntity> reportDataEntityList = reportDataDetailsRepository.getRecordsForMatchingBibInfo(getStringList(recordNumberList),headerNameList);
            Map<String,List<ReportDataEntity>> reportDataEntityMap = getRecordNumReportDataEntityMap(reportDataEntityList);
            List<MatchingBibInfoDetail> matchingBibInfoDetailList = populateMatchingBibInfoDetail(reportDataEntityMap);
            matchingBibInfoDetailRepository.save(matchingBibInfoDetailList);
            matchingBibInfoDetailRepository.flush();
            stopWatch.stop();
            logger.info("Time taken to save--> {}", stopWatch.getTotalTimeSeconds());
            logger.info("Page {} saved to db", count);
        }
        stopWatchFull.stop();
        logger.info("Loaded matching bib info in {} seconds", stopWatchFull.getTotalTimeSeconds());
        return "Success";
    }

    public int getPageCount(int totalRecordCount,int batchSize){
        int quotient = totalRecordCount / batchSize;
        int remainder = Integer.valueOf(Long.toString(totalRecordCount)) % (batchSize);
        return remainder == 0 ? quotient : quotient + 1;
    }

    private List<String> getStringList(List<Integer> integerList){
        List<String> stringList = new ArrayList<>();
        for(Integer integer : integerList){
            stringList.add(String.valueOf(integer));
        }
        return stringList;
    }

    private  Map<String,List<ReportDataEntity>> getRecordNumReportDataEntityMap(List<ReportDataEntity> reportDataEntityList){
        Map<String,List<ReportDataEntity>> reportDataEntityMap = new HashMap<>();
        for(ReportDataEntity reportDataEntity:reportDataEntityList){
            if(reportDataEntityMap.containsKey(reportDataEntity.getRecordNum())){
                reportDataEntityMap.get(reportDataEntity.getRecordNum()).add(reportDataEntity);
            }else{
                List<ReportDataEntity> reportDataEntityListForRowNum = new ArrayList<>();
                reportDataEntityListForRowNum.add(reportDataEntity);
                reportDataEntityMap.put(reportDataEntity.getRecordNum(),reportDataEntityListForRowNum);
            }
        }
        return reportDataEntityMap;
    }

    private List<MatchingBibInfoDetail> populateMatchingBibInfoDetail(Map<String,List<ReportDataEntity>> reportDataEntityMap){
        List<MatchingBibInfoDetail> matchingBibInfoDetailList = new ArrayList<>();
        for(Map.Entry<String,List<ReportDataEntity>> entry:reportDataEntityMap.entrySet()){
            Map<String, String[]> dataArrayMap = populateDataArrays(entry.getValue());
            String[] bibIdArray = dataArrayMap.get(RecapConstants.BIB_ID);
            String[] institutionArray = dataArrayMap.get(RecapConstants.OWNING_INSTITUTION);
            String[] owningInstitutionBibIdArray = dataArrayMap.get(RecapConstants.OWNING_INSTITUTION_BIB_ID);
            for(int count=0;count<bibIdArray.length;count++){
                MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
                matchingBibInfoDetail.setBibId(bibIdArray[count]);
                matchingBibInfoDetail.setOwningInstitution(institutionArray[count]);
                matchingBibInfoDetail.setOwningInstitutionBibId(owningInstitutionBibIdArray[count]);
                matchingBibInfoDetail.setRecordNum(Integer.valueOf(entry.getKey()));
                matchingBibInfoDetailList.add(matchingBibInfoDetail);
            }
        }
        return matchingBibInfoDetailList;
    }

    private List<MatchingBibInfoDetail> findAndPopulateMatchingBibInfoDetail(Map<String,List<ReportDataEntity>> reportDataEntityMap){
        List<MatchingBibInfoDetail> matchingBibInfoDetailList = new ArrayList<>();
        for(Map.Entry<String,List<ReportDataEntity>> entry:reportDataEntityMap.entrySet()){
            Integer recordNum = Integer.valueOf(entry.getKey());
            Map<String, String[]> dataArrayMap = populateDataArrays(entry.getValue());
            String[] bibIdArray = dataArrayMap.get(RecapConstants.BIB_ID);
            String[] institutionArray = dataArrayMap.get(RecapConstants.OWNING_INSTITUTION);
            String[] owningInstitutionBibIdArray = dataArrayMap.get(RecapConstants.OWNING_INSTITUTION_BIB_ID);

            matchingBibInfoDetailList.addAll(getMatchingBibInfoDetailsToBeSaved(recordNum, bibIdArray, institutionArray, owningInstitutionBibIdArray));
        }
        return matchingBibInfoDetailList;
    }

    private List<MatchingBibInfoDetail> getMatchingBibInfoDetailsToBeSaved(Integer recordNum, String[] bibIdArray, String[] institutionArray, String[] owningInstitutionBibIdArray) {
        List<MatchingBibInfoDetail> matchingBibInfoDetailList = new ArrayList<>();
        List<String> addedBibIds = new ArrayList<>();
        List<Integer> recordNums = matchingBibInfoDetailRepository.findRecordNumByBibIds(Arrays.asList(bibIdArray));
        List<MatchingBibInfoDetail> matchingBibInfoDetails = matchingBibInfoDetailRepository.findByRecordNumIn(recordNums);
        if(CollectionUtils.isNotEmpty(matchingBibInfoDetails)) {
            for(MatchingBibInfoDetail matchingBibInfoDetail : matchingBibInfoDetails) {
                addedBibIds.add(matchingBibInfoDetail.getBibId());
                matchingBibInfoDetail.setRecordNum(recordNum);
                matchingBibInfoDetailList.add(matchingBibInfoDetail);
            }
        }
        for(int count=0;count<bibIdArray.length;count++){
            String bibId = bibIdArray[count];
            if(!addedBibIds.contains(bibId)) {
                MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
                matchingBibInfoDetail.setBibId(bibId);
                matchingBibInfoDetail.setOwningInstitution(institutionArray[count]);
                matchingBibInfoDetail.setOwningInstitutionBibId(owningInstitutionBibIdArray[count]);
                matchingBibInfoDetail.setRecordNum(recordNum);
                matchingBibInfoDetailList.add(matchingBibInfoDetail);
            }
        }
        return matchingBibInfoDetailList;
    }

    private Map<String, String[]> populateDataArrays(List<ReportDataEntity> reportDataEntities) {
        Map<String, String[]> dataArrayMap = new HashMap<>();
        for(ReportDataEntity reportDataEntity : reportDataEntities) {
            if (RecapConstants.BIB_ID.equals(reportDataEntity.getHeaderName())) {
                dataArrayMap.put(RecapConstants.BIB_ID, reportDataEntity.getHeaderValue().split(","));
            } else if (RecapConstants.OWNING_INSTITUTION.equals(reportDataEntity.getHeaderName())) {
                dataArrayMap.put(RecapConstants.OWNING_INSTITUTION, reportDataEntity.getHeaderValue().split(","));
            } else if (RecapConstants.OWNING_INSTITUTION_BIB_ID.equals(reportDataEntity.getHeaderName())) {
                dataArrayMap.put(RecapConstants.OWNING_INSTITUTION_BIB_ID, reportDataEntity.getHeaderValue().split(","));
            }
        }
        return dataArrayMap;
    }

}
