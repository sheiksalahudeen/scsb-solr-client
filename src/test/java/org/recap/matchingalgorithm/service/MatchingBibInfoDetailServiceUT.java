package org.recap.matchingalgorithm.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 29/1/17.
 */
public class MatchingBibInfoDetailServiceUT extends BaseTestCase {

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Test
    public void populateMatchingBibInfo(){
        String respone  = matchingBibInfoDetailService.populateMatchingBibInfo();
        assertNotNull(respone);

        String response = matchingBibInfoDetailService.populateMatchingBibInfo(new Date(), new Date());
        assertNotNull(response);
    }
}
