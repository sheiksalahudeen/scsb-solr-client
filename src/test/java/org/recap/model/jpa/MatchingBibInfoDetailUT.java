package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.MatchingBibInfoDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/2/17.
 */
public class MatchingBibInfoDetailUT extends BaseTestCase{

    @Autowired
    MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;


    @Test
    public void testMatchingBibInformation(){
        MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
        matchingBibInfoDetail.setBibId("12");
        matchingBibInfoDetail.setOwningInstitution("PUL");
        matchingBibInfoDetail.setOwningInstitutionBibId("1223");
        matchingBibInfoDetail.setRecordNum(4);
        MatchingBibInfoDetail savedMatchingBibInfoDetail = matchingBibInfoDetailRepository.save(matchingBibInfoDetail);
        assertNotNull(savedMatchingBibInfoDetail);
        assertNotNull(savedMatchingBibInfoDetail.getMatchingBibInfoDetailId());
        assertNotNull(savedMatchingBibInfoDetail.getBibId());
        assertNotNull(savedMatchingBibInfoDetail.getRecordNum());
        assertNotNull(savedMatchingBibInfoDetail.getOwningInstitution());
        assertNotNull(savedMatchingBibInfoDetail.getOwningInstitutionBibId());
    }

}