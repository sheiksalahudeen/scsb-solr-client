package org.recap.executors;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 30/1/17.
 */
@Ignore
public class MatchingIndexExecutorServiceAT extends BaseTestCase{

    @Autowired
    MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService;

    @Test
    public void indexingForMatchingAlgorithmTest() throws InterruptedException {
        Integer count = matchingBibItemIndexExecutorService.indexingForMatchingAlgorithm(RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, new Date());
        assertTrue(count > 0);
    }


}