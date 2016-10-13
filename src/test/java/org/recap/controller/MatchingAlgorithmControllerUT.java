package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.MatchingAlgorithmSaveReport;
import org.recap.matchingAlgorithm.report.ReportGenerator;
import org.recap.model.solr.SolrIndexRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController= new MatchingAlgorithmController();

    @Mock
    MatchingAlgorithmSaveReport mockMatchingAlgorithmSaveReport;

    @Mock
    ReportGenerator reportGenerator;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception{

        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReports();
        String response = matchingAlgorithmController.matchingAlgorithmFull();
        assertTrue(response.contains("Status  : Done"));
    }

   @Test
    public void testMatchingAlgorithmBasedOnOCLC() throws Exception{
       doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForOclc();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnOCLC();
        assertTrue(response.contains("Status  : Done"));assertTrue(response.contains("Status  : Done"));
    }


    @Test
    public void testMatchingAlgorithmBasedOnISSN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForIssn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnISSN();
        assertTrue(response.contains("Status  : Done"));;
    }


    @Test
    public void testMatchingAlgorithmBasedOnLCCN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForLccn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnLCCN();
        assertTrue(response.contains("Status  : Done"));
    }
    @Test
    public void testMatchingAlgorithmBasedOnISBN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForIsbn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnISBN();
        assertTrue(response.contains("Status  : Done"));
    }

    @Test
    public void testFullMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ALL_INST);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        when(reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME);
        String response = matchingAlgorithmController.generateReportsForAll(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME));

        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        when(reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.EXCEPTION_REPORT_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForAll(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_FILE_NAME));
    }

    @Test
    public void testOCLCMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.OCLC_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        when(reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME);
        String response = matchingAlgorithmController.generateReportsForOclc(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME));

        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        when(reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForOclc(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME));
    }

    @Test
    public void testISBNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ISBN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        when(reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME);
        String response = matchingAlgorithmController.generateReportsForIsbn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME));

        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        when(reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForIsbn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME));
    }

    @Test
    public void testISSNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ISSN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        when(reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME);
        String response = matchingAlgorithmController.generateReportsForIssn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME));

        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        when(reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForIssn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME));
    }

    @Test
    public void testLCCNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.LCCN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        when(reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME);
        String response = matchingAlgorithmController.generateReportsForLccn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME));

        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        when(reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, matchingAlgorithmController.getFromDate(solrIndexRequest.getCreatedDate()),
                matchingAlgorithmController.getToDate(solrIndexRequest.getCreatedDate()))).thenReturn(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForLccn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME));
    }
}