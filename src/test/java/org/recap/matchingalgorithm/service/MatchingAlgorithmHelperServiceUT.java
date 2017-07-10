package org.recap.matchingalgorithm.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by premkb on 3/8/16.
 */
public class MatchingAlgorithmHelperServiceUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmHelperServiceUT.class);

    @InjectMocks
    MatchingAlgorithmHelperService matchingAlgorithmHelperService = new MatchingAlgorithmHelperService();

    @Mock
    MatchingAlgorithmHelperService matchingAlgoHelperService;

    @Mock
    private MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Mock
    private MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Mock
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    private SolrQueryBuilder solrQueryBuilder;

    @Mock
    private SolrTemplate solrTemplate;

    @Mock
    private JmxHelper jmxHelper;

    @Mock
    private ProducerTemplate producerTemplate;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(matchingAlgoHelperService.getLogger()).thenCallRealMethod();
    }

    private MatchingMatchPointsEntity getMatchingMatchPointEntity() {
        MatchingMatchPointsEntity matchingMatchPointsEntity = new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setMatchCriteria(RecapConstants.OCLC_CRITERIA);
        matchingMatchPointsEntity.setCriteriaValue("193843");
        matchingMatchPointsEntity.setCriteriaValueCount(4);
        matchingMatchPointsEntity.setId(1);
        return matchingMatchPointsEntity;
    }

    private MatchingBibEntity getMatchingBibEntity(String matching) {
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setId(1);
        matchingBibEntity.setRoot("123");
        matchingBibEntity.setStatus("Pending");
        matchingBibEntity.setLccn("19383");
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setIsbn("883939");
        matchingBibEntity.setOwningInstitution("PUL");
        matchingBibEntity.setMatching(matching);
        matchingBibEntity.setMaterialType("Monograph");
        matchingBibEntity.setOclc("2939384");
        matchingBibEntity.setIssn("29384");
        matchingBibEntity.setOwningInstBibId("1938");
        matchingBibEntity.setTitle("Sample Matching Title");
        return matchingBibEntity;
    }

    @Test
    public void findMatchingAndPopulateMatchPointsEntities() throws Exception {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities = new ArrayList<>();
        matchingMatchPointsEntities.add(getMatchingMatchPointEntity());
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgoHelperService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_OCLC)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingMatchPointsEntities);
        Mockito.when(matchingAlgorithmUtil.getMatchingMatchPointsEntity(RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingMatchPointsEntities);
        Mockito.doNothing().when(matchingAlgorithmUtil).saveMatchingMatchPointEntities(matchingMatchPointsEntities);
        Mockito.when(matchingAlgoHelperService.findMatchingAndPopulateMatchPointsEntities()).thenCallRealMethod();
        long count = matchingAlgoHelperService.findMatchingAndPopulateMatchPointsEntities();
        assertNotNull(count);
        assertEquals(count, matchingMatchPointsEntities.size() * 4);
    }

    @Test
    public void populateMatchingBibEntities() throws Exception {
        Mockito.when(matchingAlgoHelperService.populateMatchingBibEntities()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgoHelperService.getMatchingMatchPointsDetailsRepository()).thenReturn(matchingMatchPointsDetailsRepository);
        Mockito.when(matchingAlgoHelperService.fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_OCLC)).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISBN)).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_ISSN)).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.fetchAndSaveMatchingBibs(RecapConstants.MATCH_POINT_FIELD_LCCN)).thenCallRealMethod();
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(RecapConstants.MATCH_POINT_FIELD_OCLC)).thenReturn(new Long(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(RecapConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(new Long(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(RecapConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(new Long(0));
        Mockito.when(matchingMatchPointsDetailsRepository.countBasedOnCriteria(RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(new Long(0));
        long count = matchingAlgoHelperService.populateMatchingBibEntities();
        assertNotNull(count);
        assertEquals(count, 0);
    }

    @Test
    public void populateReportsForOCLCAndISBN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIsbn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.MATCH_POINT_FIELD_ISBN,
                matchingBibEntity.getOclc(), matchingBibEntity.getIsbn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForOCLCandISBN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForOCLCandISBN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForOCLCAndISSN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndIssn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.MATCH_POINT_FIELD_ISSN,
                matchingBibEntity.getOclc(), matchingBibEntity.getIssn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForOCLCAndISSN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForOCLCAndISSN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForOCLCAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> oclcAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_OCLC, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(oclcAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getOclc());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForOclcAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(oclcAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_OCLC, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.OCLC_CRITERIA, RecapConstants.MATCH_POINT_FIELD_LCCN,
                matchingBibEntity.getOclc(), matchingBibEntity.getLccn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForOCLCAndLCCN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForOCLCAndLCCN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISBNAndISSN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_ISBN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(isbnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIsbn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndIssn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_ISSN,
                matchingBibEntity.getIsbn(), matchingBibEntity.getIssn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForISBNAndISSN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForISBNAndISSN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISBNAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> isbnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_ISBN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(isbnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIsbn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIsbnAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(isbnAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_ISBN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCH_POINT_FIELD_LCCN,
                matchingBibEntity.getIsbn(), matchingBibEntity.getLccn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForISBNAndLCCN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForISBNAndLCCN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForISSNAndLCCN() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISSN);
        matchingBibEntities.add(matchingBibEntity);
        List<Integer> bibIds = Arrays.asList(matchingBibEntity.getBibId());
        Set<Integer> bibIdSet = new HashSet<>();
        Map<String, Set<Integer>> issnAndBibIdMap = new HashMap<>();
        bibIdSet.addAll(bibIds);
        Map<Integer, MatchingBibEntity> matchingBibEntityMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUtil.getMatchCriteriaValue(RecapConstants.MATCH_POINT_FIELD_ISSN, matchingBibEntity)).thenCallRealMethod();
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateCriteriaMap(issnAndBibIdMap, matchingBibEntity.getBibId(), matchingBibEntity.getIssn());
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibIdsForIssnAndLccn()).thenReturn(bibIds);
        Mockito.when(matchingBibDetailsRepository.getMultiMatchBibEntitiesBasedOnBibIds(bibIds, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(matchingBibEntities);
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).populateBibIdWithMatchingCriteriaValue(issnAndBibIdMap, matchingBibEntities, RecapConstants.MATCH_POINT_FIELD_ISSN, bibEntityMap);
        matchingBibEntityMap.put(matchingBibEntity.getBibId(), matchingBibEntity);
        Mockito.when(matchingAlgorithmUtil.populateAndSaveReportEntity(bibIdSet, matchingBibEntityMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCH_POINT_FIELD_LCCN,
                matchingBibEntity.getIssn(), matchingBibEntity.getLccn())).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForISSNAndLCCN(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForISSNAndLCCN(1000);
        assertNotNull(countsMap);
        assertEquals(countMap, countsMap);
    }

    @Test
    public void populateReportsForSingleMatch() throws Exception {
        List<MatchingBibEntity> matchingBibEntities = new ArrayList<>();
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        matchingBibEntities.add(matchingBibEntity);
        Map<String,Integer> countMap = new HashMap<>();
        countMap.put(RecapConstants.PUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.CUL_MATCHING_COUNT, 1);
        countMap.put(RecapConstants.NYPL_MATCHING_COUNT, 1);
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgoHelperService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenReturn(matchingBibDetailsRepository);
        Mockito.when(matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1000, RecapConstants.MATCH_POINT_FIELD_OCLC)).thenReturn(countMap);
        Mockito.when(matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1000, RecapConstants.MATCH_POINT_FIELD_ISBN)).thenReturn(countMap);
        Mockito.when(matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1000, RecapConstants.MATCH_POINT_FIELD_ISSN)).thenReturn(countMap);
        Mockito.when(matchingAlgorithmUtil.getSingleMatchBibsAndSaveReport(1000, RecapConstants.MATCH_POINT_FIELD_LCCN)).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForPendingMatches(1000)).thenCallRealMethod();
        Mockito.when(matchingBibDetailsRepository.findByStatus(new PageRequest(0,1000), RecapConstants.PENDING)).thenReturn(getMatchingBibEntity(matchingBibEntities));
        Mockito.when(matchingBibDetailsRepository.findByStatus(new PageRequest(1,1000), RecapConstants.PENDING)).thenReturn(getMatchingBibEntity(matchingBibEntities));
        Mockito.when(matchingAlgorithmUtil.processPendingMatchingBibs(matchingBibEntities)).thenReturn(countMap);
        Mockito.when(matchingAlgoHelperService.populateReportsForSingleMatch(1000)).thenCallRealMethod();
        Map<String, Integer> countsMap = matchingAlgoHelperService.populateReportsForSingleMatch(1000);
        assertEquals(Math.toIntExact(countsMap.get(RecapConstants.PUL_MATCHING_COUNT)), 6);
        assertEquals(Math.toIntExact(countsMap.get(RecapConstants.CUL_MATCHING_COUNT)), 6);
        assertEquals(Math.toIntExact(countsMap.get(RecapConstants.NYPL_MATCHING_COUNT)), 6);
    }

    @Test
    public void checkGetterServices() throws Exception {
        Mockito.when(matchingAlgoHelperService.getJmxHelper()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingBibDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingAlgorithmUtil()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getMatchingMatchPointsDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getProducerTemplate()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getSolrQueryBuilder()).thenCallRealMethod();
        Mockito.when(matchingAlgoHelperService.getSolrTemplate()).thenCallRealMethod();
        assertNotEquals(jmxHelper, matchingAlgoHelperService.getJmxHelper());
        assertNotEquals(matchingBibDetailsRepository, matchingAlgoHelperService.getMatchingBibDetailsRepository());
        assertNotEquals(matchingAlgorithmUtil, matchingAlgoHelperService.getMatchingAlgorithmUtil());
        assertNotEquals(solrQueryBuilder, matchingAlgoHelperService.getSolrQueryBuilder());
        assertNotEquals(solrTemplate, matchingAlgoHelperService.getSolrTemplate());
        assertNotEquals(producerTemplate, matchingAlgoHelperService.getProducerTemplate());
        assertNotEquals(matchingMatchPointsDetailsRepository, matchingAlgoHelperService.getMatchingMatchPointsDetailsRepository());
    }

    public Page<MatchingBibEntity> getMatchingBibEntity(List<MatchingBibEntity> matchingBibEntities){
        Page<MatchingBibEntity> matchingBibEntityPage = new Page<MatchingBibEntity>() {
            @Override
            public int getTotalPages() {
                return 2;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <S> Page<S> map(Converter<? super MatchingBibEntity, ? extends S> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<MatchingBibEntity> getContent() {
                return matchingBibEntities;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<MatchingBibEntity> iterator() {
                return matchingBibEntities.iterator();
            }
        };

        return matchingBibEntityPage;
    }
}
