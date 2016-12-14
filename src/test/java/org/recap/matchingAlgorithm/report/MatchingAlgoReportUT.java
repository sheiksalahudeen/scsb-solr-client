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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                populateOclcAndIsbnMap(bibIdAndCriteriaMap, bibIdAndOwningInstitutionMap, bibEntitiesBasedOnBibIds);
            }
        }
        for (Iterator<Integer> iterator = bibIdAndCriteriaMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            String criteriaValue = bibIdAndCriteriaMap.get(bibId);
            if(criteriaValue.split(",").length > 1) {
                if(oclcAndIsbnMap.containsKey(criteriaValue)) {
                    List<Integer> bibIds = oclcAndIsbnMap.get(criteriaValue);
                    List<Integer> bibIdList = new ArrayList<>();
                    bibIdList.addAll(bibIds);
                    bibIdList.add(bibId);
                    oclcAndIsbnMap.put(criteriaValue, bibIdList);
                } else {
                    oclcAndIsbnMap.put(criteriaValue, Arrays.asList(bibId));
                }
            }
        }
        logger.info("Total Elements to report : " + oclcAndIsbnMap.size());
        populateAndSaveReportEntity(oclcAndIsbnMap, bibIdAndOwningInstitutionMap);
        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    private void populateOclcAndIsbnMap(Map<Integer, String> bibIdAndCriteriaMap, Map<Integer, String> bibIdAndOwningInstitutionMap, List<MatchingBibEntity> matchingBibEntities) {
        for (Iterator<MatchingBibEntity> iterator = matchingBibEntities.iterator(); iterator.hasNext(); ) {
            MatchingBibEntity matchingBibEntity = iterator.next();
            Integer bibId = matchingBibEntity.getBibId();
            String matching = matchingBibEntity.getMatching();
            if(bibIdAndCriteriaMap.containsKey(bibId)) {
                StringBuilder value = new StringBuilder();
                String criteriaValue = bibIdAndCriteriaMap.get(bibId);
                if(matching.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
                    String oclc = matchingBibEntity.getOclc();
                    value.append(oclc).append(",").append(criteriaValue);
                    bibIdAndCriteriaMap.put(bibId, value.toString());
                } else if(matching.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
                    String isbn = matchingBibEntity.getIsbn();
                    value.append(criteriaValue).append(",").append(isbn);
                    bibIdAndCriteriaMap.put(bibId, value.toString());
                }
            } else {
                bibIdAndOwningInstitutionMap.put(bibId, matchingBibEntity.getOwningInstitution());
                if(matching.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
                    bibIdAndCriteriaMap.put(bibId, matchingBibEntity.getOclc());
                } else if(matching.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
                    bibIdAndCriteriaMap.put(bibId, matchingBibEntity.getIsbn());
                }
            }
        }
    }

    private void populateAndSaveReportEntity(Map<String, List<Integer>> oclcAndIsbnMap, Map<Integer, String> bibIdAndOwningInstitutionMap) {
        for (Iterator<String> iterator = oclcAndIsbnMap.keySet().iterator(); iterator.hasNext(); ) {
            ReportEntity reportEntity = new ReportEntity();
            List<ReportDataEntity> reportDataEntities = new ArrayList<>();
            reportEntity.setFileName("OCLC,ISBN");
            reportEntity.setType("MultiMatch");
            reportEntity.setCreatedDate(new Date());
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            String oclcAndIsbnValues = iterator.next();
            StringBuilder bibIdString = new StringBuilder();
            StringBuilder owningInstString = new StringBuilder();
            List<Integer> bibIds = oclcAndIsbnMap.get(oclcAndIsbnValues);
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

            String[] oclcAndIsbn = oclcAndIsbnValues.split(",");
            if(StringUtils.isNotBlank(oclcAndIsbn[0])) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName("OCLC");
                criteriaReportDataEntity.setHeaderValue(oclcAndIsbn[0]);
                reportDataEntities.add(criteriaReportDataEntity);
            }

            if(StringUtils.isNotBlank(oclcAndIsbn[1])) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName("ISBN");
                criteriaReportDataEntity.setHeaderValue(oclcAndIsbn[1]);
                reportDataEntities.add(criteriaReportDataEntity);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
    }
}
