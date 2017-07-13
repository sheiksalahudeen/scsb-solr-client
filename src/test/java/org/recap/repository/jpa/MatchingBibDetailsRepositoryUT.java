package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingBibEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by angelind on 1/11/16.
 */
public class MatchingBibDetailsRepositoryUT extends BaseTestCase{

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Test
    public void saveMatchingBibEntity() throws Exception {
        MatchingBibEntity savedMatchingBibEntity = saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        assertNotNull(savedMatchingBibEntity.getId());
        MatchingBibEntity bibEntity = matchingBibDetailsRepository.findOne(savedMatchingBibEntity.getId());
        assertNotNull(bibEntity);
    }

    @Test
    public void testMatchingBibEntity(){
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setId(1);
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setOwningInstitution("NYPL");
        matchingBibEntity.setOwningInstBibId("N1029");
        matchingBibEntity.setTitle("Middleware for ReCAP");
        matchingBibEntity.setOclc("129393");
        matchingBibEntity.setIsbn("93930");
        matchingBibEntity.setIssn("12283");
        matchingBibEntity.setLccn("039329");
        matchingBibEntity.setMaterialType("monograph");
        matchingBibEntity.setMatching("test");
        matchingBibEntity.setRoot("31");
        assertNotNull(matchingBibEntity.getId());
        assertNotNull(matchingBibEntity.getRoot());
        assertNotNull(matchingBibEntity.getId());
        assertNotNull(matchingBibEntity.getBibId());
        assertNotNull(matchingBibEntity.getOwningInstitution());
        assertNotNull(matchingBibEntity.getOwningInstBibId());
        assertNotNull(matchingBibEntity.getTitle());
        assertNotNull(matchingBibEntity.getOclc());
        assertNotNull(matchingBibEntity.getIsbn());
        assertNotNull(matchingBibEntity.getIssn());
        assertNotNull(matchingBibEntity.getLccn());
        assertNotNull(matchingBibEntity.getMaterialType());
        assertNotNull(matchingBibEntity.getMatching());
    }

    private MatchingBibEntity saveMatchingBibEntity(String matchingCriteria) {
        MatchingBibEntity matchingBibEntity = new MatchingBibEntity();
        matchingBibEntity.setBibId(1);
        matchingBibEntity.setOwningInstitution("NYPL");
        matchingBibEntity.setOwningInstBibId("N1029");
        matchingBibEntity.setTitle("Middleware for ReCAP");
        matchingBibEntity.setOclc("129393");
        matchingBibEntity.setIsbn("93930");
        matchingBibEntity.setIssn("12283");
        matchingBibEntity.setLccn("039329");
        matchingBibEntity.setMaterialType("monograph");
        matchingBibEntity.setMatching(matchingCriteria);
        matchingBibEntity.setRoot("31");
        matchingBibEntity.setStatus(RecapConstants.PENDING);
        return matchingBibDetailsRepository.save(matchingBibEntity);
    }

    @Test
    public void getMultipleMatchPointBibEntity() throws Exception {
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        List<MatchingBibEntity> multipleMatchPointBibEntity = matchingBibDetailsRepository.getBibEntityBasedOnBibIds(Arrays.asList(1));
        assertNotNull(multipleMatchPointBibEntity);
        assertTrue(multipleMatchPointBibEntity.size() > 1);
    }

    @Test
    public void getMultipleMatchedBibIdsBasedOnLimit() throws Exception {
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        List<Integer> multipleMatchedBibIdsBasedOnLimit = matchingBibDetailsRepository.getMultipleMatchedBibIdsBasedOnLimit(0, 1);
        assertNotNull(multipleMatchedBibIdsBasedOnLimit);
        assertTrue(multipleMatchedBibIdsBasedOnLimit.size() == 1);
    }

    @Test
    public void getMultipleMatchUniqueBibCount() throws Exception {
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_ISBN);
        long multipleMatchUniqueBibCount = matchingBibDetailsRepository.getMultipleMatchUniqueBibCount();
        assertNotNull(multipleMatchUniqueBibCount);
        assertTrue(multipleMatchUniqueBibCount > 0);
    }

    @Test
    public void getSingleMatchBibCountBasedOnMatching() throws Exception {
        saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        long multipleMatchUniqueBibCount = matchingBibDetailsRepository.getSingleMatchBibCountBasedOnMatching(RecapConstants.MATCH_POINT_FIELD_OCLC);
        assertNotNull(multipleMatchUniqueBibCount);
        assertTrue(multipleMatchUniqueBibCount > 0);
    }

    @Test
    public void updateAndFetchMatchingBibByStatus() throws Exception {
        MatchingBibEntity matchingBibEntity = saveMatchingBibEntity(RecapConstants.MATCH_POINT_FIELD_OCLC);
        assertNotNull(matchingBibEntity);
        assertNotNull(matchingBibEntity.getId());
        Page<MatchingBibEntity> byStatus = matchingBibDetailsRepository.findByStatus(new PageRequest(0, 10), RecapConstants.PENDING);
        assertNotNull(byStatus);
        MatchingBibEntity matchingBibEntity1 = byStatus.getContent().get(0);
        assertNotNull(matchingBibEntity1);
        assertEquals(matchingBibEntity.getId(), matchingBibEntity1.getId());
        int updateStatus = matchingBibDetailsRepository.updateStatusBasedOnBibs(RecapConstants.COMPLETE_STATUS, Arrays.asList(matchingBibEntity.getBibId()));
        assertTrue(updateStatus > 0);
        Page<MatchingBibEntity> matchingBibEntities = matchingBibDetailsRepository.findByStatus(new PageRequest(0, 10), RecapConstants.COMPLETE_STATUS);
        assertNotNull(matchingBibEntities);
        List<MatchingBibEntity> matchingBibEntityList = matchingBibEntities.getContent();
        assertTrue(matchingBibEntityList.size() > 0);
    }

}