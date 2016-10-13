package org.recap.matchingAlgorithm;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 4/7/16.
 */
public class MatchingAlgorithmSaveReportAT extends BaseTestCase {

    @Value("${matching.report.directory}")
    String reportDirectoryPath;

    @Autowired
    MatchingAlgorithmSaveReport matchingAlgorithmSaveReport;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Test
    public void findMatchingAndSaveReportForAll() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmSaveReport.saveMatchingAlgorithmReports();
        Thread.sleep(1000);
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileNameAndType(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.MATCHING_TYPE);
        assertNotNull(reportEntityList);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingAndSaveReportForOclc() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForOclc();
        Thread.sleep(1000);
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileNameAndType(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.MATCHING_TYPE);
        assertNotNull(reportEntityList);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingAndSaveReportForISBN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForIsbn();
        Thread.sleep(1000);
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileNameAndType(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.MATCHING_TYPE);
        assertNotNull(reportEntityList);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingReportForISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForIssn();
        Thread.sleep(1000);
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileNameAndType(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.MATCHING_TYPE);
        assertNotNull(reportEntityList);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingReportForLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForLccn();
        Thread.sleep(1000);
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileNameAndType(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.MATCHING_TYPE);
        assertNotNull(reportEntityList);
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void getMatchesByFacetQuery() throws JSONException {
        String fieldName = RecapConstants.MATCH_POINT_FIELD_OCLC;
        Map<String, Integer> duplicateOCLCMap = matchingAlgorithmSaveReport.getMatchesByFacetQuery(fieldName);
        assertNotNull(duplicateOCLCMap);

        fieldName = RecapConstants.MATCH_POINT_FIELD_ISBN;
        Map<String, Integer> duplicateISBNMap = matchingAlgorithmSaveReport.getMatchesByFacetQuery(fieldName);
        assertNotNull(duplicateISBNMap);

        fieldName = RecapConstants.MATCH_POINT_FIELD_ISSN;
        Map<String, Integer> duplicateISSNMap = matchingAlgorithmSaveReport.getMatchesByFacetQuery(fieldName);
        assertNotNull(duplicateISSNMap);

        fieldName = RecapConstants.MATCH_POINT_FIELD_LCCN;
        Map<String, Integer> duplicateLCCNMap = matchingAlgorithmSaveReport.getMatchesByFacetQuery(fieldName);
        assertNotNull(duplicateLCCNMap);
    }
}
