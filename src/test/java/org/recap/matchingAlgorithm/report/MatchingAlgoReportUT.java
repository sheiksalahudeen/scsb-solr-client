package org.recap.matchingAlgorithm.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

/**
 * Created by angelind on 9/12/16.
 */
public class MatchingAlgoReportUT extends BaseTestCase {

    Logger logger = LoggerFactory.getLogger(MatchingAlgoReportUT.class);

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    private Integer batchSize=10000;

    @Test
    public void fetchMultiMatchBibsForOCLCAndISBN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForOCLCAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISBNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchMultiMatchBibsForISSNAndLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize);

        stopWatch.stop();
        Thread.sleep(1000);
        logger.info("Total Time taken in seconds : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void fetchSingleMatchBibs() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize);

        stopWatch.stop();
        logger.info("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }
}
