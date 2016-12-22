package org.recap.matchingAlgorithm.report;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by angelind on 9/12/16.
 */
public class MatchingAlgoReportUT extends BaseTestCase {

    Logger logger = LoggerFactory.getLogger(MatchingAlgoReportUT.class);

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    public void fetchMultiMatchBibsForOCLCAndISBN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForOCLCAndISBN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIsbn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISBN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
            }
        }

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder isbns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISBN_CRITERIA, oclcNumbers.toString(), isbns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForOCLCAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIssn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISSN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
            }
        }

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder issns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISSN_CRITERIA, oclcNumbers.toString(), issns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForOCLCAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndLccn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndLCCN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN, bibEntityMap);
            }
        }

        Set<String> oclcNumberSet = new HashSet<>();
        for (Iterator<String> iterator = oclcAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String oclc = iterator.next();
            if(!oclcNumberSet.contains(oclc)) {
                StringBuilder oclcNumbers  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                oclcNumberSet.add(oclc);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = oclcAndBibIdMap.get(oclc);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    oclcNumbers.append(StringUtils.isNotBlank(oclcNumbers.toString()) ? "," : "").append(matchingBibEntity.getOclc());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] oclcList = oclcNumbers.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.LCCN_CRITERIA, oclcNumbers.toString(), lccns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISBNAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndIssn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndISSN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
            }
        }

        Set<String> isbnSet = new HashSet<>();
        for (Iterator<String> iterator = isbnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String isbn = iterator.next();
            if(!isbnSet.contains(isbn)) {
                StringBuilder isbns  = new StringBuilder();
                StringBuilder issns = new StringBuilder();
                isbnSet.add(isbn);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = isbnAndBibIdMap.get(isbn);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    String[] isbnList = isbns.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.ISSN_CRITERIA, isbns.toString(), issns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISBNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndLccn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndLCCN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN, bibEntityMap);
            }
        }

        Set<String> isbnSet = new HashSet<>();
        for (Iterator<String> iterator = isbnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String isbn = iterator.next();
            if(!isbnSet.contains(isbn)) {
                StringBuilder isbns  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                isbnSet.add(isbn);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = isbnAndBibIdMap.get(isbn);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    isbns.append(StringUtils.isNotBlank(isbns.toString()) ? "," : "").append(matchingBibEntity.getIsbn());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] isbnList = isbns.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.LCCN_CRITERIA, isbns.toString(), lccns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISSNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISSNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndLccn();
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISSNAndLCCN, batchSize);
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN, bibEntityMap);
            }
        }

        Set<String> issnSet = new HashSet<>();
        for (Iterator<String> iterator = issnAndBibIdMap.keySet().iterator(); iterator.hasNext(); ) {
            String issn = iterator.next();
            if(!issnSet.contains(issn)) {
                StringBuilder issns  = new StringBuilder();
                StringBuilder lccns = new StringBuilder();
                issnSet.add(issn);
                Set<Integer> tempBibIds = new HashSet<>();
                Set<Integer> bibIds = issnAndBibIdMap.get(issn);
                tempBibIds.addAll(bibIds);
                for(Integer bibId : bibIds) {
                    MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                    issns.append(StringUtils.isNotBlank(issns.toString()) ? "," : "").append(matchingBibEntity.getIssn());
                    lccns.append(StringUtils.isNotBlank(lccns.toString()) ? "," : "").append(matchingBibEntity.getLccn());
                    String[] issnList = issns.toString().split(",");
                    tempBibIds.addAll(getBibIdsForCriteriaValue(issnAndBibIdMap, issnSet, issn, RecapConstants.MATCH_POINT_FIELD_ISSN, issnList, bibEntityMap, issns));
                }
                populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISSN_CRITERIA, RecapConstants.LCCN_CRITERIA, issns.toString(), lccns.toString());
            }
        }
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchSingleMatchBibs() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_OCLC);
        getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISBN);
        getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISSN);
        getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_LCCN);
        stopWatch.stop();
        logger.info("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    private void getSingleMatchBibsAndSaveReport(Integer batchSize, String matching) {
        Map<String, Set<Integer>> criteriaMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Integer> singleMatchBibIdsBasedOnMatching = matchingBibDetailsRepository.getSingleMatchBibIdsBasedOnMatching(matching);
        stopWatch.stop();
        logger.info("Time taken to fetch " + matching + " from db : " + stopWatch.getTotalTimeSeconds());
        logger.info("Total " + matching + " : " + singleMatchBibIdsBasedOnMatching.size());

        if(CollectionUtils.isNotEmpty(singleMatchBibIdsBasedOnMatching)) {
            List<List<Integer>> bibIdLists = Lists.partition(singleMatchBibIdsBasedOnMatching, batchSize);
            logger.info("Total " + matching + " list : " + bibIdLists.size());
            for (Iterator<List<Integer>> iterator = bibIdLists.iterator(); iterator.hasNext(); ) {
                List<Integer> bibIds = iterator.next();
                List<MatchingBibEntity> matchingBibEntities = matchingBibDetailsRepository.getBibEntityBasedOnBibIds(bibIds);
                if(CollectionUtils.isNotEmpty(matchingBibEntities)) {
                    for (Iterator<MatchingBibEntity> matchingBibEntityIterator = matchingBibEntities.iterator(); matchingBibEntityIterator.hasNext(); ) {
                        MatchingBibEntity matchingBibEntity = matchingBibEntityIterator.next();
                        Integer bibId = matchingBibEntity.getBibId();
                        String matchCriteriaValue = getMatchCriteriaValue(matching, matchingBibEntity);
                        if(!bibEntityMap.containsKey(bibId)) {
                            bibEntityMap.put(bibId, matchingBibEntity);
                        }
                        populateCriteriaMap(criteriaMap, bibId, matchCriteriaValue);
                    }
                }
            }

            Set<String> criteriaValueSet = new HashSet<>();
            for (Iterator<String> iterator = criteriaMap.keySet().iterator(); iterator.hasNext(); ) {
                String criteriaValue = iterator.next();
                if (!criteriaValueSet.contains(criteriaValue) && criteriaMap.get(criteriaValue).size() > 1) {
                    StringBuilder matchPointValue = new StringBuilder();
                    criteriaValueSet.add(criteriaValue);
                    Set<Integer> tempBibIds = new HashSet<>();
                    List<Integer> tempBibIdList = new ArrayList<>();
                    Set<Integer> bibIds = criteriaMap.get(criteriaValue);
                    tempBibIds.addAll(bibIds);
                    for (Integer bibId : bibIds) {
                        MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                        matchPointValue.append(StringUtils.isNotBlank(matchPointValue.toString()) ? "," : "").append(getMatchCriteriaValue(matching, matchingBibEntity));
                        String[] criteriaValueList = matchPointValue.toString().split(",");
                        tempBibIds.addAll(getBibIdsForCriteriaValue(criteriaMap, criteriaValueSet, criteriaValue, matching, criteriaValueList, bibEntityMap, matchPointValue));
                    }
                    tempBibIdList.addAll(tempBibIds);
                    saveReportForSingleMatch(matchPointValue.toString(), tempBibIdList, matching, bibEntityMap);
                }
            }
        }
    }

    private Set<Integer> getBibIdsForCriteriaValue(Map<String, Set<Integer>> criteriaMap, Set<String> criteriaValueSet, String criteriaValue,
                                                   String matching, String[] criteriaValueList, Map<Integer, MatchingBibEntity> bibEntityMap, StringBuilder matchPointValue) {
        Set<Integer> tempBibIdSet = new HashSet<>();
        for (String value : criteriaValueList) {
            criteriaValueSet.add(value);
            if (!value.equalsIgnoreCase(criteriaValue)) {
                Set<Integer> bibIdSet = criteriaMap.get(value);
                if (CollectionUtils.isNotEmpty(bibIdSet)) {
                    for(Integer bibId : bibIdSet) {
                        MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                        String matchCriteriaValue = getMatchCriteriaValue(matching, matchingBibEntity);
                        String[] matchCriteriaValueList = matchCriteriaValue.split(",");
                        for(String matchingValue : matchCriteriaValueList) {
                            if(!criteriaValueSet.contains(matchingValue)) {
                                matchPointValue.append(StringUtils.isNotBlank(matchPointValue.toString()) ? "," : "").append(matchingValue);
                                criteriaValueSet.add(matchingValue);
                            }
                        }
                    }
                    tempBibIdSet.addAll(bibIdSet);
                }
            }
        }
        return tempBibIdSet;
    }

    private void saveReportForSingleMatch(String criteriaValue, List<Integer> bibIdList, String criteria, Map<Integer, MatchingBibEntity> matchingBibEntityMap) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        StringBuilder bibIds = new StringBuilder();
        StringBuilder owningInsts = new StringBuilder();
        StringBuilder materialType = new StringBuilder();
        Set<String> owningInstSet = new HashSet<>();
        for(int index=0; index < bibIdList.size(); index++) {
            Integer bibId = bibIdList.get(index);
            MatchingBibEntity matchingBibEntity = matchingBibEntityMap.get(bibId);
            owningInstSet.add(matchingBibEntity.getOwningInstitution());
            if(index == 0) {
                bibIds.append(matchingBibEntity.getBibId());
                owningInsts.append(matchingBibEntity.getOwningInstitution());
                materialType.append(matchingBibEntity.getMaterialType());
            } else {
                bibIds.append(",").append(matchingBibEntity.getBibId());
                owningInsts.append(",").append(matchingBibEntity.getOwningInstitution());
                materialType.append(",").append(matchingBibEntity.getMaterialType());
            }
            if(StringUtils.isNotBlank(matchingBibEntity.getTitle())) {
                ReportDataEntity titleReportDataEntity = new ReportDataEntity();
                int i = index + 1;
                titleReportDataEntity.setHeaderName("Title" + i);
                titleReportDataEntity.setHeaderValue(matchingBibEntity.getTitle());
                reportDataEntities.add(titleReportDataEntity);
            }
        }
        if(owningInstSet.size() > 1) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC) ? RecapConstants.OCLC_CRITERIA : criteria);
            reportEntity.setType("SingleMatch");
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            reportEntity.setCreatedDate(new Date());

            if(StringUtils.isNotBlank(bibIds.toString())) {
                ReportDataEntity bibIdReportDataEntity = new ReportDataEntity();
                bibIdReportDataEntity.setHeaderName("BibId");
                bibIdReportDataEntity.setHeaderValue(bibIds.toString());
                reportDataEntities.add(bibIdReportDataEntity);
            }

            if(StringUtils.isNotBlank(owningInsts.toString())) {
                ReportDataEntity owningInstReportDataEntity = new ReportDataEntity();
                owningInstReportDataEntity.setHeaderName("OwningInstitution");
                owningInstReportDataEntity.setHeaderValue(owningInsts.toString());
                reportDataEntities.add(owningInstReportDataEntity);
            }

            if(StringUtils.isNotBlank(materialType.toString())) {
                ReportDataEntity materialTypeReportDataEntity = new ReportDataEntity();
                materialTypeReportDataEntity.setHeaderName("MaterialType");
                materialTypeReportDataEntity.setHeaderValue(materialType.toString());
                reportDataEntities.add(materialTypeReportDataEntity);
            }

            ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
            criteriaReportDataEntity.setHeaderName(criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC) ? RecapConstants.OCLC_CRITERIA : criteria);
            criteriaReportDataEntity.setHeaderValue(criteriaValue);
            reportDataEntities.add(criteriaReportDataEntity);

            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
    }

    private void populateBibIdWithMatchingCriteriaValue(Map<String, Set<Integer>> criteria1Map, List<MatchingBibEntity> matchingBibEntities, String matchCriteria1, String matchCriteria2, Map<Integer, MatchingBibEntity> bibEntityMap) {
        for (Iterator<MatchingBibEntity> iterator = matchingBibEntities.iterator(); iterator.hasNext(); ) {
            MatchingBibEntity matchingBibEntity = iterator.next();
            Integer bibId = matchingBibEntity.getBibId();
            String matching = matchingBibEntity.getMatching();
            if(!bibEntityMap.containsKey(bibId)) {
                bibEntityMap.put(bibId, matchingBibEntity);
            }
            if(matching.equalsIgnoreCase(matchCriteria1)) {
                String criteriaValue1 = getMatchCriteriaValue(matchCriteria1, matchingBibEntity);
                populateCriteriaMap(criteria1Map, bibId, criteriaValue1);
            }
        }
    }

    private void populateCriteriaMap(Map<String, Set<Integer>> criteriaMap, Integer bibId, String value) {

        String[] criteriaValues = value.split(",");
        for(String criteriaValue : criteriaValues) {
            if(StringUtils.isNotBlank(criteriaValue)) {
                if(criteriaMap.containsKey(criteriaValue)) {
                    Set<Integer> bibIdSet = new HashSet<>();
                    Set<Integer> bibIds = criteriaMap.get(criteriaValue);
                    bibIdSet.addAll(bibIds);
                    bibIdSet.add(bibId);
                    criteriaMap.put(criteriaValue, bibIdSet);
                } else {
                    Set<Integer> bibIdSet = new HashSet<>();
                    bibIdSet.add(bibId);
                    criteriaMap.put(criteriaValue,bibIdSet);
                }
            }
        }
    }

    private String getMatchCriteriaValue(String matchCriteria, MatchingBibEntity matchingBibEntity) {
        if(matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            return matchingBibEntity.getOclc();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            return matchingBibEntity.getIsbn();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            return matchingBibEntity.getIssn();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_LCCN)) {
            return matchingBibEntity.getLccn();
        }
        return "";
    }

    private void populateAndSaveReportEntity(Set<Integer> bibIds, Map<Integer, MatchingBibEntity> bibEntityMap, String header1, String header2, String oclcNumbers, String isbns) {
        ReportEntity reportEntity = new ReportEntity();
        Set<String> owningInstSet = new HashSet<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportEntity.setFileName(header1 + "," + header2);
        reportEntity.setType("MultiMatch");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        StringBuilder bibIdString = new StringBuilder();
        StringBuilder owningInstString = new StringBuilder();
        StringBuilder materialTypeString = new StringBuilder();

        for (Iterator<Integer> integerIterator = bibIds.iterator(); integerIterator.hasNext(); ) {
            Integer bibId = integerIterator.next();
            bibIdString.append(bibId);
            MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
            owningInstSet.add(matchingBibEntity.getOwningInstitution());
            owningInstString.append(matchingBibEntity.getOwningInstitution());
            materialTypeString.append(matchingBibEntity.getMaterialType());
            if(integerIterator.hasNext()) {
                bibIdString.append(",");
                owningInstString.append(",");
                materialTypeString.append(",");
            }
        }
        if(owningInstSet.size() > 1) {
            if(StringUtils.isNotBlank(bibIdString)) {
                ReportDataEntity bibIdReportDataEntity = new ReportDataEntity();
                bibIdReportDataEntity.setHeaderName("BibId");
                bibIdReportDataEntity.setHeaderValue(bibIdString.toString());
                reportDataEntities.add(bibIdReportDataEntity);
            }

            if(StringUtils.isNotBlank(owningInstString)) {
                ReportDataEntity owningInstReportDataEntity = new ReportDataEntity();
                owningInstReportDataEntity.setHeaderName("OwningInstitution");
                owningInstReportDataEntity.setHeaderValue(owningInstString.toString());
                reportDataEntities.add(owningInstReportDataEntity);
            }

            if(StringUtils.isNotBlank(materialTypeString)) {
                ReportDataEntity materialTypeReportDataEntity = new ReportDataEntity();
                materialTypeReportDataEntity.setHeaderName("MaterialType");
                materialTypeReportDataEntity.setHeaderValue(materialTypeString.toString());
                reportDataEntities.add(materialTypeReportDataEntity);
            }

            if(StringUtils.isNotBlank(oclcNumbers)) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName(header1);
                criteriaReportDataEntity.setHeaderValue(oclcNumbers);
                reportDataEntities.add(criteriaReportDataEntity);
            }

            if(StringUtils.isNotBlank(isbns)) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderValue(isbns);
                criteriaReportDataEntity.setHeaderName(header2);
                reportDataEntities.add(criteriaReportDataEntity);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
    }
}
