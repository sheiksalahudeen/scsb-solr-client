package org.recap.matchingAlgorithm;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.camel.activemq.JmxHelper;
import org.recap.controller.MatchingAlgorithmController;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.util.MatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.text.Normalizer;

import static org.junit.Assert.*;

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

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    MatchingAlgorithmController matchingAlgorithmController;

    @Autowired
    JmxHelper jmxHelper;

    @Autowired
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    private Integer batchSize = 1000;

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

    @Test
    public void testDiacriticTitles() {
        String title = "--A bude hůř : román o třech-dílech /-";
        System.out.println("Actual Title : " + title);
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        System.out.println("Step 1 Normalized Title : " + normalizedTitle);
        normalizedTitle = normalizedTitle.replaceAll("[^\\p{ASCII}]", "");
        System.out.println("Step 2 Normalized Title : " + normalizedTitle);
        normalizedTitle = normalizedTitle.replaceAll("\\p{M}", "");
        System.out.println("Step 3 Normalized Title : " + normalizedTitle);
    }

    @Test
    public void testGetTitleToMatch() {
        String title = "--A bude hůř : román o třech-dílech /-";
        System.out.println("Actual Title : " + title);
        String titleToMatch = matchingAlgorithmUtil.getTitleToMatch(title);
        System.out.println("Title To Match : " + titleToMatch);
    }

    @Test
    public void runWholeMatchingAlgorithm() throws Exception {
        String status = matchingAlgorithmController.matchingAlgorithmFull();
        assertNotNull(status);
        assertTrue(status.contains("Done"));
    }

    @Test
    public void updateCGDForMatchingAlgorithm() throws Exception {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        matchingAlgorithmUpdateCGDService.updateCGDProcessForMonographs(batchSize);

        stopWatch.stop();
        logger.info("Total Time taken to update CGD is : " + stopWatch.getTotalTimeSeconds());
    }

}
