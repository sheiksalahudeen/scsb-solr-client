package org.recap.repository.jpa;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.util.SolrQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by angelind on 31/10/16.
 */
public class MatchingMatchPointsDetailsRepositoryUT extends BaseTestCase{

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Test
    public void saveMatchingMatchPointsEntity() throws Exception {
        MatchingMatchPointsEntity savedMatchingMatchPointsEntity = saveMatchingMatchPointEntity(RecapConstants.MATCH_POINT_FIELD_OCLC, "129282");
        assertNotNull(savedMatchingMatchPointsEntity.getId());
        MatchingMatchPointsEntity matchPointsEntity = matchingMatchPointsDetailsRepository.findOne(savedMatchingMatchPointsEntity.getId());
        assertNotNull(matchPointsEntity);
    }

    private MatchingMatchPointsEntity saveMatchingMatchPointEntity(String criteria, String value) {
        MatchingMatchPointsEntity matchingMatchPointsEntity = new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setMatchCriteria(criteria);
        matchingMatchPointsEntity.setCriteriaValue(value);
        matchingMatchPointsEntity.setCriteriaValueCount(2);
        return matchingMatchPointsDetailsRepository.save(matchingMatchPointsEntity);
    }

    @Test
    public void countBasedOnCriteria() throws Exception {
        saveMatchingMatchPointEntity(RecapConstants.MATCH_POINT_FIELD_OCLC, "3093949");
        long countBasedOnCriteria = matchingMatchPointsDetailsRepository.countBasedOnCriteria(RecapConstants.MATCH_POINT_FIELD_OCLC);
        assertTrue(countBasedOnCriteria > 0);
    }

    @Test
    public void getMatchPointEntityBasedOnCriterias() throws Exception {
        saveMatchingMatchPointEntity(RecapConstants.MATCH_POINT_FIELD_OCLC, "3093949");
        List<MatchingMatchPointsEntity> byFirstMatchCriteriaAndSecondMatchCriteria = matchingMatchPointsDetailsRepository.getMatchPointEntityByCriteria(RecapConstants.MATCH_POINT_FIELD_OCLC, 0, 1000);
        assertNotNull(byFirstMatchCriteriaAndSecondMatchCriteria);
        assertTrue(byFirstMatchCriteriaAndSecondMatchCriteria.size() > 0);
    }

    @Test
    public void testMatchingMatchPointsEntity(){
        MatchingMatchPointsEntity matchingMatchPointsEntity = new MatchingMatchPointsEntity();
        matchingMatchPointsEntity.setId(1);
        matchingMatchPointsEntity.setCriteriaValue("3093949");
        matchingMatchPointsEntity.setMatchCriteria(RecapConstants.MATCH_POINT_FIELD_OCLC);
        matchingMatchPointsEntity.setCriteriaValueCount(1);
        assertNotNull(matchingMatchPointsEntity.getId());
        assertNotNull(matchingMatchPointsEntity.getCriteriaValue());
        assertNotNull(matchingMatchPointsEntity.getCriteriaValueCount());
        assertNotNull(matchingMatchPointsEntity.getMatchCriteria());
    }

    @Test
    public void verifyMatchingCriteriaValue() throws Exception {
        MatchingMatchPointsEntity matchingMatchPointsEntity = saveMatchingMatchPointEntity(RecapConstants.MATCH_POINT_FIELD_OCLC, "25001781 /\\");
        assertNotNull(matchingMatchPointsEntity);
        assertNotNull(matchingMatchPointsEntity.getId());
        MatchingMatchPointsEntity matchPointsEntity = matchingMatchPointsDetailsRepository.findOne(matchingMatchPointsEntity.getId());
        assertNotNull(matchPointsEntity);
        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        List<String> matchingCriterias = new ArrayList<>();
        SolrQuery solrQuery = solrQueryBuilder.solrQueryToFetchBibDetails(Arrays.asList(matchPointsEntity), matchingCriterias, RecapConstants.MATCH_POINT_FIELD_LCCN);
        assertNotNull(solrQuery);
    }

}