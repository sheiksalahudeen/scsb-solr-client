package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.MatchingBibInfoDetail;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by angelind on 5/4/17.
 */
public class MatchingBibInfoDetailRepositoryUT extends BaseTestCase {

    @Autowired
    MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;

    @Test
    public void findByBibId() throws Exception {
        MatchingBibInfoDetail matchingBibInfoDetail = saveMatchingBibInfoDetail(1, "1");
        assertNotNull(matchingBibInfoDetail);
        assertNotNull(matchingBibInfoDetail.getMatchingBibInfoDetailId());
        List<MatchingBibInfoDetail> byBibId = matchingBibInfoDetailRepository.findByBibId(matchingBibInfoDetail.getBibId());
        assertNotNull(byBibId);
        assertTrue(byBibId.size() == 1);
        assertNotNull(byBibId.get(0));
    }

    @Test
    public void findByRecordNumIn() throws Exception {
        MatchingBibInfoDetail matchingBibInfoDetail1 = saveMatchingBibInfoDetail(1, "1");
        MatchingBibInfoDetail matchingBibInfoDetail2 = saveMatchingBibInfoDetail(2, "2");
        List<MatchingBibInfoDetail> byRecordNumIn = matchingBibInfoDetailRepository.findByRecordNumIn(Arrays.asList(matchingBibInfoDetail1.getRecordNum(), matchingBibInfoDetail2.getRecordNum()));
        assertNotNull(byRecordNumIn);
        assertTrue(byRecordNumIn.size() == 2);
    }

    @Test
    public void findRecordNumByBibIds() throws Exception {
        MatchingBibInfoDetail matchingBibInfoDetail1 = saveMatchingBibInfoDetail(1, "1");
        MatchingBibInfoDetail matchingBibInfoDetail2 = saveMatchingBibInfoDetail(2, "2");
        List<Integer> recordNumByBibIds = matchingBibInfoDetailRepository.findRecordNumByBibIds(Arrays.asList(matchingBibInfoDetail1.getBibId(), matchingBibInfoDetail2.getBibId()));
        assertNotNull(recordNumByBibIds);
        assertTrue(recordNumByBibIds.size() == 2);
    }

    public MatchingBibInfoDetail saveMatchingBibInfoDetail(Integer recordNum, String bibId) {
        MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
        matchingBibInfoDetail.setBibId(bibId);
        matchingBibInfoDetail.setOwningInstitution("PUL");
        matchingBibInfoDetail.setOwningInstitutionBibId("PA1001");
        matchingBibInfoDetail.setRecordNum(recordNum);
        return matchingBibInfoDetailRepository.save(matchingBibInfoDetail);
    }

}