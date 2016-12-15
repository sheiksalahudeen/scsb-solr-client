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
        List<Integer> multiMatchBibIdsForOCLCAndISBN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIsbn(RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISBN, batchSize);
        Map<String, List<Integer>> oclcAndIsbnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
            }
        }
        populateMatchingCriteriaMap(oclcAndIsbnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + oclcAndIsbnMap.size());
        populateAndSaveReportEntity(oclcAndIsbnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_OCLC, RecapConstants.MATCHING_ISBN, "OCLC,ISBN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForOCLCAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIssn(RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISSN, batchSize);
        Map<String, List<Integer>> oclcAndIssnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
            }
        }
        populateMatchingCriteriaMap(oclcAndIssnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + oclcAndIssnMap.size());
        populateAndSaveReportEntity(oclcAndIssnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_OCLC, RecapConstants.MATCHING_ISSN, "OCLC,ISSN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForOCLCAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndLccn(RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndLCCN, batchSize);
        Map<String, List<Integer>> oclcAndLccnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
            }
        }
        populateMatchingCriteriaMap(oclcAndLccnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + oclcAndLccnMap.size());
        populateAndSaveReportEntity(oclcAndLccnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_OCLC, RecapConstants.MATCHING_LCCN, "OCLC,LCCN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISBNAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndIssn(RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndISSN, batchSize);
        Map<String, List<Integer>> isbnAndIssnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
            }
        }
        populateMatchingCriteriaMap(isbnAndIssnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + isbnAndIssnMap.size());
        populateAndSaveReportEntity(isbnAndIssnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_ISBN, RecapConstants.MATCHING_ISSN, "ISBN,ISSN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISBNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndLccn(RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndLCCN, batchSize);
        Map<String, List<Integer>> isbnAndLccnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            }
        }
        populateMatchingCriteriaMap(isbnAndLccnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + isbnAndLccnMap.size());
        populateAndSaveReportEntity(isbnAndLccnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_ISBN, RecapConstants.MATCHING_LCCN, "ISBN,LCCN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISSNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer batchSize = 10000;
        List<Integer> multiMatchBibIdsForISSNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndLccn(RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
        stopWatch.stop();
        logger.info("Time Taken to fetch results from db : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISSNAndLCCN, batchSize);
        Map<String, List<Integer>> issnAndLccnMap = new HashMap<>();
        Map<Integer, String> bibIdAndCriteriaMap = new HashMap<>();
        Map<Integer, String> bibIdAndOwningInstitutionMap = new HashMap<>();
        logger.info("Total Bib Id partition List : " + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                populateBibIdWithMatchingCriteriaValue(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            }
        }
        populateMatchingCriteriaMap(issnAndLccnMap, bibIdAndCriteriaMap);
        logger.info("Total Elements to report : " + issnAndLccnMap.size());
        populateAndSaveReportEntity(issnAndLccnMap, bibIdAndOwningInstitutionMap, RecapConstants.MATCHING_ISSN, RecapConstants.MATCHING_LCCN, "ISSN,LCCN");
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    private void populateMatchingCriteriaMap(Map<String, List<Integer>> criteriaMap, Map<Integer, String> bibIdAndCriteriaMap) {
        for (Iterator<Integer> iterator = bibIdAndCriteriaMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            String criteriaValue = bibIdAndCriteriaMap.get(bibId);
            if(criteriaValue.split(",").length > 1) {
                if(criteriaMap.containsKey(criteriaValue)) {
                    List<Integer> bibIds = criteriaMap.get(criteriaValue);
                    List<Integer> bibIdList = new ArrayList<>();
                    bibIdList.addAll(bibIds);
                    bibIdList.add(bibId);
                    criteriaMap.put(criteriaValue, bibIdList);
                } else {
                    criteriaMap.put(criteriaValue, Arrays.asList(bibId));
                }
            }
        }
    }

    private void populateBibIdWithMatchingCriteriaValue(Map<Integer, String> bibIdAndCriteriaMap, Map<Integer, String> bibIdAndOwningInstitutionMap, List<MatchingBibEntity> matchingBibEntities, String matchCriteria1, String matchCriteria2) {
        for (Iterator<MatchingBibEntity> iterator = matchingBibEntities.iterator(); iterator.hasNext(); ) {
            MatchingBibEntity matchingBibEntity = iterator.next();
            Integer bibId = matchingBibEntity.getBibId();
            String matching = matchingBibEntity.getMatching();
            if(bibIdAndCriteriaMap.containsKey(bibId)) {
                StringBuilder value = new StringBuilder();
                String criteriaValue = bibIdAndCriteriaMap.get(bibId);
                if(matching.equalsIgnoreCase(matchCriteria1)) {
                    String criteriaValue1 = getMatchCriteriaValue(matchCriteria1, matchingBibEntity);
                    value.append(criteriaValue1).append(",").append(criteriaValue);
                    bibIdAndCriteriaMap.put(bibId, value.toString());
                } else if(matching.equalsIgnoreCase(matchCriteria2)) {
                    String criteriaValue2 = getMatchCriteriaValue(matchCriteria2, matchingBibEntity);
                    value.append(criteriaValue2).append(",").append(criteriaValue);
                    bibIdAndCriteriaMap.put(bibId, value.toString());
                }
            } else {
                bibIdAndOwningInstitutionMap.put(bibId, matchingBibEntity.getOwningInstitution());
                if(matching.equalsIgnoreCase(matchCriteria1)) {
                    bibIdAndCriteriaMap.put(bibId, getMatchCriteriaValue(matchCriteria1, matchingBibEntity));
                } else if(matching.equalsIgnoreCase(matchCriteria2)) {
                    bibIdAndCriteriaMap.put(bibId, getMatchCriteriaValue(matchCriteria2, matchingBibEntity));
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

    private void populateAndSaveReportEntity(Map<String, List<Integer>> criteriaMap, Map<Integer, String> bibIdAndOwningInstitutionMap, String header1, String header2, String fileName) {
        for (Iterator<String> iterator = criteriaMap.keySet().iterator(); iterator.hasNext(); ) {
            ReportEntity reportEntity = new ReportEntity();
            List<ReportDataEntity> reportDataEntities = new ArrayList<>();
            reportEntity.setFileName(fileName);
            reportEntity.setType("MultiMatch");
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            String criteriaValues = iterator.next();
            StringBuilder bibIdString = new StringBuilder();
            StringBuilder owningInstString = new StringBuilder();
            List<Integer> bibIds = criteriaMap.get(criteriaValues);
            for (Iterator<Integer> integerIterator = bibIds.iterator(); integerIterator.hasNext(); ) {
                Integer bibId = integerIterator.next();
                bibIdString.append(bibId);
                owningInstString.append(bibIdAndOwningInstitutionMap.get(bibId));
                if(integerIterator.hasNext()) {
                    bibIdString.append(",");
                    owningInstString.append(",");
                }
            }
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

            String[] criteriavalue = criteriaValues.split(",");
            if(StringUtils.isNotBlank(criteriavalue[0])) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName(header1);
                criteriaReportDataEntity.setHeaderValue(criteriavalue[0]);
                reportDataEntities.add(criteriaReportDataEntity);
            }

            if(StringUtils.isNotBlank(criteriavalue[1])) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName(header2);
                criteriaReportDataEntity.setHeaderValue(criteriavalue[1]);
                reportDataEntities.add(criteriaReportDataEntity);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
    }
}
