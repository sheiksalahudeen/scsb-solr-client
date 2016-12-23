package org.recap.matchingAlgorithm;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 27/10/16.
 */
public class MatchingAlgorithmUT extends BaseTestCase {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUT.class);

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Test
    public void populateTempMatchingPointsEntity() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        long count = matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities();

        stopWatch.stop();
        logger.info("Total Time taken : " + stopWatch.getTotalTimeSeconds());

        Thread.sleep(10000);
        long savedCount = matchingMatchPointsDetailsRepository.count();
        assertEquals(count, savedCount);
    }

    @Test
    public void populateTempMatchingBibsEntity() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        long count = matchingAlgorithmHelperService.populateMatchingBibEntities();

        stopWatch.stop();
        logger.info("Total Time taken : " + stopWatch.getTotalTimeSeconds());
        long savedBibsCount = matchingBibDetailsRepository.count();
        assertEquals(count, savedBibsCount);
    }

}
