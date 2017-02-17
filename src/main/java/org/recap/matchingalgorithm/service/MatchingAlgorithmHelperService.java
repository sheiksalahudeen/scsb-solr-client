package org.recap.matchingalgorithm.service;

import com.google.common.collect.Lists;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.executors.SaveMatchingBibsCallable;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by angelind on 11/7/16.
 */
@Service
public class MatchingAlgorithmHelperService {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmHelperService.class);

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    JmxHelper jmxHelper;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    private ExecutorService executorService;

    public void clearExistingMatchingData() throws Exception {
        matchingMatchPointsDetailsRepository.deleteAll();
        matchingBibDetailsRepository.deleteAll();
        clearMatchingReports();
    }

    public void clearMatchingReports() {
        reportDetailRepository.deleteReportDataEntitiesByTypeAndFileName(Arrays.asList("SingleMatch", "MultiMatch"));
        reportDetailRepository.deleteReportEntitiesByTypeAndFileName(Arrays.asList("SingleMatch", "MultiMatch"));
    }

    public long findMatchingAndPopulateMatchPointsEntities() throws Exception {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities;
        long count = 0;

        matchingMatchPointsEntities = matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        count = count + matchingMatchPointsEntities.size();
        matchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        count = count + matchingMatchPointsEntities.size();
        matchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISSN);
        count = count + matchingMatchPointsEntities.size();
        matchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_LCCN);
        count = count + matchingMatchPointsEntities.size();
        matchingAlgorithmUtil.saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        logger.info("Total count : " , count);

        DestinationViewMBean saveMatchingMatchPointsQ = jmxHelper.getBeanForQueueName("saveMatchingMatchPointsQ");

        while (saveMatchingMatchPointsQ.getQueueSize() != 0) {
            //Do Nothing
        }
        return count;
    }

    public long populateMatchingBibEntities() throws IOException, SolrServerException {
        Integer count = 0;
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_OCLC);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISBN);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISSN);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_LCCN);
        DestinationViewMBean saveMatchingBibsQ = jmxHelper.getBeanForQueueName("saveMatchingBibsQ");
        while (saveMatchingBibsQ.getQueueSize() != 0) {
            //Do nothing
        }
        return count;
    }

    public Map<String,Integer> populateReportsForOCLCandISBN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndISBN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIsbn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISBN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISBN_CRITERIA, oclcNumbers.toString(), isbns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }

        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForOCLCAndISSN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISSN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISSN_CRITERIA, oclcNumbers.toString(), issns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }
        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForOCLCAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndLCCN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.LCCN_CRITERIA, oclcNumbers.toString(), lccns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }
        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForISBNAndISSN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISBNAndISSN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndISSN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.ISSN_CRITERIA, isbns.toString(), issns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }
        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForISBNAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISBNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndLCCN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.LCCN_CRITERIA, isbns.toString(), lccns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }
        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForISSNAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISSNAndLCCN = matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISSNAndLCCN, batchSize);
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST + multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                matchingAlgorithmUtil.populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
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
                    tempBibIds.addAll(matchingAlgorithmUtil.getBibIdsForCriteriaValue(issnAndBibIdMap, issnSet, issn, RecapConstants.MATCH_POINT_FIELD_ISSN, issnList, bibEntityMap, issns));
                }
                Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISSN_CRITERIA, RecapConstants.LCCN_CRITERIA, issns.toString(), lccns.toString());
                pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
                culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
                nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
            }
        }
        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public Map<String,Integer> populateReportsForSingleMatch(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        Map<String, Integer> matchingCountsMap = matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_OCLC);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISBN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISSN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_LCCN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);

        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    public void saveMatchingSummaryCount(Integer pulMatchingCount, Integer culMatchingCount, Integer nyplMatchingCount) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setType("MatchingCount");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setFileName("MatchingSummaryCount");
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity pulCountReportDataEntity = new ReportDataEntity();
        pulCountReportDataEntity.setHeaderName(RecapConstants.PUL_MATCHING_COUNT);
        pulCountReportDataEntity.setHeaderValue(String.valueOf(pulMatchingCount));
        reportDataEntities.add(pulCountReportDataEntity);

        ReportDataEntity culCountReportDataEntity = new ReportDataEntity();
        culCountReportDataEntity.setHeaderName(RecapConstants.CUL_MATCHING_COUNT);
        culCountReportDataEntity.setHeaderValue(String.valueOf(culMatchingCount));
        reportDataEntities.add(culCountReportDataEntity);

        ReportDataEntity nyplCountReportDataEntity = new ReportDataEntity();
        nyplCountReportDataEntity.setHeaderName(RecapConstants.NYPL_MATCHING_COUNT);
        nyplCountReportDataEntity.setHeaderValue(String.valueOf(nyplMatchingCount));
        reportDataEntities.add(nyplCountReportDataEntity);

        reportEntity.addAll(reportDataEntities);
        producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
    }

    public Integer fetchAndSaveMatchingBibs(String matchCriteria) throws SolrServerException, IOException {
        long batchSize = 300;
        Integer size = 0;
        long countBasedOnCriteria = matchingMatchPointsDetailsRepository.countBasedOnCriteria(matchCriteria);
        SaveMatchingBibsCallable saveMatchingBibsCallable = new SaveMatchingBibsCallable();
        saveMatchingBibsCallable.setBibIdList(new HashSet<>());
        int totalPagesCount = (int) Math.ceil(countBasedOnCriteria / batchSize);
        ExecutorService executorService = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        for (int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new SaveMatchingBibsCallable(matchingMatchPointsDetailsRepository, matchCriteria, solrTemplate,
                    producerTemplate, solrQueryBuilder, batchSize, pageNum, matchingAlgorithmUtil);
            callables.add(callable);
        }

        size = executeCallables(size, executorService, callables);
        return size;
    }

    private Integer executeCallables(Integer size, ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Future<Integer>> futures = null;
        try {
            futures = executorService.invokeAll(callables);
            futures
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }

        for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            try {
                size += (Integer) future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }
        return size;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }
}
