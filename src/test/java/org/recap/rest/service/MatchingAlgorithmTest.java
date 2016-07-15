package org.recap.rest.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.MatchingAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 4/7/16.
 */
public class MatchingAlgorithmTest extends BaseTestCase {

    @Value("${solr.report.directory}")
    String reportDirectoryPath;

    @Autowired
    MatchingAlgorithm matchingAlgorithm;

    @Test
    public void findMatchingUsingOclcNumber() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithm.generateMatchingAlgorithmReportForOclc();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + "Matching_Algo_OCLC_" + df.format(new Date()) + ".csv");
        assertTrue(file.exists());
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingISBN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithm.generateMatchingAlgorithmReportForIsbn();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + "Matching_Algo_ISBN_" + df.format(new Date()) + ".csv");
        assertTrue(file.exists());
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithm.generateMatchingAlgorithmReportForIssn();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + "Matching_Algo_ISSN_" + df.format(new Date()) + ".csv");
        assertTrue(file.exists());
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithm.generateMatchingAlgorithmReportForLccn();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + "Matching_Algo_LCCN_" + df.format(new Date()) + ".csv");
        assertTrue(file.exists());
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }
}
