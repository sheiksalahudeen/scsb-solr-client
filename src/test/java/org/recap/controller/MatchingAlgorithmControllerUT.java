package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.report.ReportGenerator;
import org.recap.util.MatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController= new MatchingAlgorithmController();

    @Mock
    MatchingAlgorithmController matchingAlgoController;

    @Mock
    ReportGenerator reportGenerator;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Mock
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    private Integer batchSize = 10000;



    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception{
        Map<String,Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount",1);
        matchingAlgoMap.put("culMatchingCount",2);
        matchingAlgoMap.put("nyplMatchingCount",3);
        Mockito.when(matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateMatchingBibEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmFull()).thenReturn("Status  : Done");
        String response = matchingAlgoController.matchingAlgorithmFull();
        assertTrue(response.contains("Status  : Done"));
    }

    @Test
    public void testFullMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setToDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ALL_INST);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        String reportType = RecapConstants.MATCHING_TYPE;
        Date createdDate = solrIndexRequest.getCreatedDate();
        Date toDate = solrIndexRequest.getToDate();
        Mockito.when(matchingAlgoController.getLogger()).thenReturn(logger);
        Mockito.when(matchingAlgoController.getReportGenerator()).thenReturn(reportGenerator);
        Mockito.when(matchingAlgoController.getFromDate(createdDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getToDate(toDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME);
        Mockito.when(matchingAlgoController.generateReportsForAll(solrIndexRequest, bindingResult, model)).thenCallRealMethod();
        String response = matchingAlgoController.generateReportsForAll(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME));
        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        reportType = RecapConstants.EXCEPTION_TYPE;
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.EXCEPTION_REPORT_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.EXCEPTION_REPORT_FILE_NAME);
        response = matchingAlgoController.generateReportsForAll(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_FILE_NAME));
    }

    @Test
    public void testOCLCMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setToDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.OCLC_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        String reportType = RecapConstants.MATCHING_TYPE;
        Date createdDate = solrIndexRequest.getCreatedDate();
        Date toDate = solrIndexRequest.getToDate();
        Mockito.when(matchingAlgoController.getLogger()).thenReturn(logger);
        Mockito.when(matchingAlgoController.getReportGenerator()).thenReturn(reportGenerator);
        Mockito.when(matchingAlgoController.getFromDate(createdDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getToDate(toDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME);
        Mockito.when(matchingAlgoController.generateReportsForOclc(solrIndexRequest, bindingResult, model)).thenCallRealMethod();
        String response = matchingAlgoController.generateReportsForOclc(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME));
        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        reportType = RecapConstants.EXCEPTION_TYPE;
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME);
        response = matchingAlgorithmController.generateReportsForOclc(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME));
    }

    @Test
    public void testISBNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setToDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ISBN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        String reportType = RecapConstants.MATCHING_TYPE;
        Date createdDate = solrIndexRequest.getCreatedDate();
        Date toDate = solrIndexRequest.getToDate();
        Mockito.when(matchingAlgoController.getLogger()).thenReturn(logger);
        Mockito.when(matchingAlgoController.getReportGenerator()).thenReturn(reportGenerator);
        Mockito.when(matchingAlgoController.getFromDate(createdDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getToDate(toDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME);
        Mockito.when(matchingAlgoController.generateReportsForIsbn(solrIndexRequest, bindingResult, model)).thenCallRealMethod();
        String response = matchingAlgoController.generateReportsForIsbn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME));
        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        reportType = RecapConstants.EXCEPTION_TYPE;
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME);
        response = matchingAlgoController.generateReportsForIsbn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME));
    }

    @Test
    public void testISSNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setToDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.ISSN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        String reportType = RecapConstants.MATCHING_TYPE;
        Date createdDate = solrIndexRequest.getCreatedDate();
        Date toDate = solrIndexRequest.getToDate();
        Mockito.when(matchingAlgoController.getLogger()).thenReturn(logger);
        Mockito.when(matchingAlgoController.getReportGenerator()).thenReturn(reportGenerator);
        Mockito.when(matchingAlgoController.getFromDate(createdDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getToDate(toDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME);
        Mockito.when(matchingAlgoController.generateReportsForIssn(solrIndexRequest, bindingResult, model)).thenCallRealMethod();
        Thread.sleep(1000);
        String response = matchingAlgoController.generateReportsForIssn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME));
        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        reportType = RecapConstants.EXCEPTION_TYPE;
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME, RecapConstants.ALL_INST,reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME);
        response = matchingAlgoController.generateReportsForIssn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME));
    }

    @Test
    public void testLCCNMatchingReportAndExceptionReport() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(new Date());
        solrIndexRequest.setToDate(new Date());
        solrIndexRequest.setReportType(RecapConstants.MATCHING_TYPE);
        solrIndexRequest.setMatchingCriteria(RecapConstants.LCCN_CRITERIA);
        solrIndexRequest.setTransmissionType(RecapConstants.FILE_SYSTEM);
        String reportType = RecapConstants.MATCHING_TYPE;
        Date createdDate = solrIndexRequest.getCreatedDate();
        Date toDate = solrIndexRequest.getToDate();
        Mockito.when(matchingAlgoController.getLogger()).thenReturn(logger);
        Mockito.when(matchingAlgoController.getReportGenerator()).thenReturn(reportGenerator);
        Mockito.when(matchingAlgoController.getFromDate(createdDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getToDate(toDate)).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME);
        Mockito.when(matchingAlgoController.generateReportsForLccn(solrIndexRequest, bindingResult, model)).thenCallRealMethod();
        String response = matchingAlgoController.generateReportsForLccn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME));
        solrIndexRequest.setReportType(RecapConstants.EXCEPTION_TYPE);
        reportType = RecapConstants.EXCEPTION_TYPE;
        Mockito.when(matchingAlgoController.getReportGenerator().generateReport(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(toDate))).thenReturn(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME);
        response = matchingAlgoController.generateReportsForLccn(solrIndexRequest, bindingResult, model);
        assertTrue(response.contains(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME));
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}