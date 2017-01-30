package org.recap.matchingAlgorithm.service;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String populateMatchingBibInfo(){
        List<String> typeList = new ArrayList<>();
        typeList.add("SingleMatch");
        typeList.add("MultiMatch");
        List<String> headerNameList = new ArrayList<>();
        headerNameList.add("BibId");
        headerNameList.add("OwningInstitution");
        headerNameList.add("OwningInstitutionBibId");
        Integer matchingCount = reportDetailRepository.getCountByType(typeList);
        logger.info("matchingCount------>"+matchingCount);
        Integer pageCount = getPageCount(matchingCount,batchSize);
        logger.info("pageCount--->"+pageCount);
        StopWatch stopWatchFull = new StopWatch();
        stopWatchFull.start();
        for(int count=0;count<pageCount;count++){
            logger.info("Current page--->"+count);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Page<Integer> recordNumbers = reportDetailRepository.getRecordNumByType(new PageRequest(count, batchSize),typeList);
            List<Integer> recordNumberList = recordNumbers.getContent();
            logger.info("recordNumberList size----->"+recordNumberList.size());
            List<ReportDataEntity> reportDataEntityList = reportDataDetailsRepository.getRecordsForMatchingBibInfo(getStringList(recordNumberList),headerNameList);
            Map<String,List<ReportDataEntity>> reportDataEntityMap = getRecordNumReportDataEntityMap(reportDataEntityList);
            List<MatchingBibInfoDetail> matchingBibInfoDetailList = populateMatchingBibInfoDetail(reportDataEntityMap);
            matchingBibInfoDetailRepository.save(matchingBibInfoDetailList);
            matchingBibInfoDetailRepository.flush();
            stopWatch.stop();
            logger.info("Time taken to save-->"+stopWatch.getTotalTimeSeconds());
            logger.info("Page "+count+"saved to db");
        }
        stopWatchFull.stop();
        logger.info("Loade matching bib info in "+stopWatchFull.getTotalTimeSeconds()+" seconds");
        return "Success";
    }

    public int getPageCount(int totalRecordCount,int batchSize){
        int quotient = totalRecordCount / batchSize;
        int remainder = Integer.valueOf(Long.toString(totalRecordCount)) % (batchSize);
        int loopCount = remainder == 0 ? quotient : quotient + 1;
        return loopCount;
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
            String[] bibidArray = null;
            String[] institutionArray = null;
            String[] owningInstitutionBibIdArray = null;
            for(ReportDataEntity reportDataEntity : entry.getValue()) {
                if (reportDataEntity.getHeaderName().equals("BibId")) {
                    bibidArray = reportDataEntity.getHeaderValue().split(",");
                } else if (reportDataEntity.getHeaderName().equals("OwningInstitution")) {
                    institutionArray = reportDataEntity.getHeaderValue().split(",");
                } else if (reportDataEntity.getHeaderName().equals("OwningInstitutionBibId")) {
                    owningInstitutionBibIdArray = reportDataEntity.getHeaderValue().split(",");
                }
            }
            for(int count=0;count<bibidArray.length;count++){
                MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
                matchingBibInfoDetail.setBibId(bibidArray[count]);
                matchingBibInfoDetail.setOwningInstitution(institutionArray[count]);
                matchingBibInfoDetail.setOwningInstitutionBibId(owningInstitutionBibIdArray[count]);
                matchingBibInfoDetail.setRecordNum(Integer.valueOf(entry.getKey()));
                matchingBibInfoDetailList.add(matchingBibInfoDetail);
            }
        }
        return matchingBibInfoDetailList;
    }

}
