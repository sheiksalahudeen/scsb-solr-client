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
import org.springframework.data.solr.core.SolrTemplate;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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

    private MatchingBibEntity getMatchingBibEntity() {
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setId(1);
        matchingBibEntity.setRoot("123");
        matchingBibEntity.setStatus("Pending");
        matchingBibEntity.setLccn("19383");
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setIsbn("883939");
        matchingBibEntity.setOwningInstitution("PUL");
        matchingBibEntity.setMatching(RecapConstants.MATCH_POINT_FIELD_OCLC);
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
        MatchingBibEntity matchingBibEntity = getMatchingBibEntity();
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
}
