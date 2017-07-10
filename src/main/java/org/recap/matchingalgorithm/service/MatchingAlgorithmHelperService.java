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
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmHelperService.class);

    @Autowired
    private MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    private SolrQueryBuilder solrQueryBuilder;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private JmxHelper jmxHelper;

    @Autowired
    private ProducerTemplate producerTemplate;

    private ExecutorService executorService;

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets matching bib details repository.
     *
     * @return the matching bib details repository
     */
    public MatchingBibDetailsRepository getMatchingBibDetailsRepository() {
        return matchingBibDetailsRepository;
    }

    /**
     * Gets matching match points details repository.
     *
     * @return the matching match points details repository
     */
    public MatchingMatchPointsDetailsRepository getMatchingMatchPointsDetailsRepository() {
        return matchingMatchPointsDetailsRepository;
    }

    /**
     * Gets matching algorithm util.
     *
     * @return the matching algorithm util
     */
    public MatchingAlgorithmUtil getMatchingAlgorithmUtil() {
        return matchingAlgorithmUtil;
    }

    /**
     * Gets solr query builder.
     *
     * @return the solr query builder
     */
    public SolrQueryBuilder getSolrQueryBuilder() {
        return solrQueryBuilder;
    }

    /**
     * Gets solr template.
     *
     * @return the solr template
     */
    public SolrTemplate getSolrTemplate() {
        return solrTemplate;
    }

    /**
     * Gets jmx helper.
     *
     * @return the jmx helper
     */
    public JmxHelper getJmxHelper() {
        return jmxHelper;
    }

    /**
     * Gets producer template.
     *
     * @return the producer template
     */
    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    /**
     * This method finds the matching records based on the match point field(OCLC,ISBN,ISSN,LCCN).
     *
     * @return the long
     * @throws Exception the exception
     */
    public long findMatchingAndPopulateMatchPointsEntities() throws Exception {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities;
        long count = 0;

        matchingMatchPointsEntities = getMatchingAlgorithmUtil().getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        count = count + matchingMatchPointsEntities.size();
        getMatchingAlgorithmUtil().saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = getMatchingAlgorithmUtil().getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        count = count + matchingMatchPointsEntities.size();
        getMatchingAlgorithmUtil().saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = getMatchingAlgorithmUtil().getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISSN);
        count = count + matchingMatchPointsEntities.size();
        getMatchingAlgorithmUtil().saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        matchingMatchPointsEntities = getMatchingAlgorithmUtil().getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_LCCN);
        count = count + matchingMatchPointsEntities.size();
        getMatchingAlgorithmUtil().saveMatchingMatchPointEntities(matchingMatchPointsEntities);

        getLogger().info("Total count : {} " , count);

        DestinationViewMBean saveMatchingMatchPointsQ = getJmxHelper().getBeanForQueueName("saveMatchingMatchPointsQ");

        if(saveMatchingMatchPointsQ != null) {
            while (saveMatchingMatchPointsQ.getQueueSize() != 0) {
                //Do Nothing
            }
        }
        return count;
    }

    /**
     * This method is used to populate matching bib records in the database.
     *
     * @return the long
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public long populateMatchingBibEntities() throws IOException, SolrServerException {
        Integer count = 0;
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_OCLC);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISBN);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISSN);
        count = count + fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_LCCN);
        DestinationViewMBean saveMatchingBibsQ = getJmxHelper().getBeanForQueueName("saveMatchingBibsQ");
        if(saveMatchingBibsQ != null) {
            while (saveMatchingBibsQ.getQueueSize() != 0) {
                //Do nothing
            }
        }
        return count;
    }

    /**
     * This method is used to populate reports for oclc and isbn matching combination based on the given batch size.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCandISBN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndISBN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndIsbn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISBN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        getLogger().info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISBN_CRITERIA, oclcNumbers.toString(), isbns.toString());
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

    /**
     * This method is used to populate reports for oclc and issn combination.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCAndISSN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndISSN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndISSN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.ISSN_CRITERIA, oclcNumbers.toString(), issns.toString());
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

    /**
     * This method is used to populate reports for oclc and lccn combination.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForOCLCAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForOCLCAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForOclcAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForOCLCAndLCCN, batchSize);
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(oclcAndBibIdMap, oclcNumberSet, oclc, RecapConstants.MATCH_POINT_FIELD_OCLC, oclcList, bibEntityMap, oclcNumbers));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.LCCN_CRITERIA, oclcNumbers.toString(), lccns.toString());
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

    /**
     * This method is used to populate reports for isbn and issn combination.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForISBNAndISSN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISBNAndISSN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIsbnAndIssn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndISSN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.ISSN_CRITERIA, isbns.toString(), issns.toString());
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

    /**
     * This method is used to populate reports for isbn and lccn combination.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForISBNAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISBNAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIsbnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISBNAndLCCN, batchSize);
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(isbnAndBibIdMap, isbnSet, isbn, RecapConstants.MATCH_POINT_FIELD_ISBN, isbnList, bibEntityMap, isbns));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISBN_CRITERIA, RecapConstants.LCCN_CRITERIA, isbns.toString(), lccns.toString());
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

    /**
     * This method is used to populate reports for issn and lccn combination.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForISSNAndLCCN(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        List<Integer> multiMatchBibIdsForISSNAndLCCN = getMatchingBibDetailsRepository().getMultiMatchBibIdsForIssnAndLccn();
        List<List<Integer>> multipleMatchBibIds = Lists.partition(multiMatchBibIdsForISSNAndLCCN, batchSize);
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();

        logger.info(RecapConstants.TOTAL_BIB_ID_PARTITION_LIST , multipleMatchBibIds.size());
        for(List<Integer> bibIds : multipleMatchBibIds) {
            List<MatchingBibEntity> bibEntitiesBasedOnBibIds = getMatchingBibDetailsRepository().getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN);
            if(CollectionUtils.isNotEmpty(bibEntitiesBasedOnBibIds)) {
                getMatchingAlgorithmUtil().populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, bibEntitiesBasedOnBibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
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
                    tempBibIds.addAll(getMatchingAlgorithmUtil().getBibIdsForCriteriaValue(issnAndBibIdMap, issnSet, issn, RecapConstants.MATCH_POINT_FIELD_ISSN, issnList, bibEntityMap, issns));
                }
                Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().populateAndSaveReportEntity(tempBibIds, bibEntityMap, RecapConstants.ISSN_CRITERIA, RecapConstants.LCCN_CRITERIA, issns.toString(), lccns.toString());
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

    /**
     * This method is used to populate reports for single match.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForSingleMatch(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        Map<String, Integer> matchingCountsMap = getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_OCLC);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISBN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_ISSN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        matchingCountsMap = getMatchingAlgorithmUtil().getSingleMatchBibsAndSaveReport(batchSize, RecapConstants.MATCH_POINT_FIELD_LCCN);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);

        DestinationViewMBean saveMatchingBibsQ = getJmxHelper().getBeanForQueueName("updateMatchingBibEntityQ");
        if(saveMatchingBibsQ != null) {
            while (saveMatchingBibsQ.getQueueSize() != 0) {
                //Do nothing
            }
        }

        matchingCountsMap = populateReportsForPendingMatches(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);

        Map countsMap = new HashMap();
        countsMap.put(RecapConstants.PUL_MATCHING_COUNT, pulMatchingCount);
        countsMap.put(RecapConstants.CUL_MATCHING_COUNT, culMatchingCount);
        countsMap.put(RecapConstants.NYPL_MATCHING_COUNT, nyplMatchingCount);
        return countsMap;
    }

    /**
     * Populate reports for pending matches map.
     *
     * @param batchSize the batch size
     * @return the map
     */
    public Map<String,Integer> populateReportsForPendingMatches(Integer batchSize) {

        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;

        Page<MatchingBibEntity> matchingBibEntities = getMatchingBibDetailsRepository().findByStatus(new PageRequest(0, batchSize), RecapConstants.PENDING);
        int totalPages = matchingBibEntities.getTotalPages();
        List<MatchingBibEntity> matchingBibEntityList = matchingBibEntities.getContent();
        Map<String,Integer> countsMap = getMatchingAlgorithmUtil().processPendingMatchingBibs(matchingBibEntityList);
        pulMatchingCount = pulMatchingCount + countsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + countsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + countsMap.get("nyplMatchingCount");

        for(int pageNum=1; pageNum < totalPages; pageNum++) {
            matchingBibEntities = getMatchingBibDetailsRepository().findByStatus(new PageRequest(0, batchSize), RecapConstants.PENDING);
            matchingBibEntityList = matchingBibEntities.getContent();
            countsMap = getMatchingAlgorithmUtil().processPendingMatchingBibs(matchingBibEntityList);
            pulMatchingCount = pulMatchingCount + countsMap.get("pulMatchingCount");
            culMatchingCount = culMatchingCount + countsMap.get("culMatchingCount");
            nyplMatchingCount = nyplMatchingCount + countsMap.get("nyplMatchingCount");
        }

        countsMap = new HashMap();
        countsMap.put("pulMatchingCount", pulMatchingCount);
        countsMap.put("culMatchingCount", culMatchingCount);
        countsMap.put("nyplMatchingCount", nyplMatchingCount);
        return countsMap;
    }

    /**
     * This method is used to save matching summary count.
     *
     * @param pulMatchingCount  the pul matching count
     * @param culMatchingCount  the cul matching count
     * @param nyplMatchingCount the nypl matching count
     */
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
        getProducerTemplate().sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
    }

    /**
     * This method is used to fetch and save matching bibs.
     *
     * @param matchCriteria the match criteria
     * @return the integer
     * @throws SolrServerException the solr server exception
     * @throws IOException         the io exception
     */
    public Integer fetchAndSaveMatchingBibs(String matchCriteria) throws SolrServerException, IOException {
        long batchSize = 300;
        Integer size = 0;
        long countBasedOnCriteria = getMatchingMatchPointsDetailsRepository().countBasedOnCriteria(matchCriteria);
        SaveMatchingBibsCallable saveMatchingBibsCallable = new SaveMatchingBibsCallable();
        saveMatchingBibsCallable.setBibIdList(new HashSet<>());
        int totalPagesCount = (int) (countBasedOnCriteria / batchSize);
        ExecutorService executor = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        for (int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new SaveMatchingBibsCallable(getMatchingMatchPointsDetailsRepository(), matchCriteria, getSolrTemplate(),
                    getProducerTemplate(), getSolrQueryBuilder(), batchSize, pageNum, getMatchingAlgorithmUtil());
            callables.add(callable);
        }

        size = executeCallables(size, executor, callables);
        return size;
    }

    private Integer executeCallables(Integer size, ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Future<Integer>> futures = null;
        try {
            futures = getFutures(executorService, callables, futures);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }

        if(futures != null) {
            for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
                Future future = iterator.next();
                try {
                    size += (Integer) future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(RecapConstants.LOG_ERROR,e);
                }
            }
        }
        return size;
    }

    private List<Future<Integer>> getFutures(ExecutorService executorService, List<Callable<Integer>> callables, List<Future<Integer>> futures) throws InterruptedException {
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
        return futures;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }
}
