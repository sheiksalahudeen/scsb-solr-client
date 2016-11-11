package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.MatchingAlgorithmSaveReport;
import org.recap.report.ReportGenerator;
import org.recap.model.solr.SolrIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by angelind on 12/7/16.
 */
@Controller
public class MatchingAlgorithmController {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @Autowired
    MatchingAlgorithmSaveReport matchingAlgorithmSaveReport;

    @Autowired
    ReportGenerator reportGenerator;

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/full", method = RequestMethod.POST)
    public String matchingAlgorithmFull() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmSaveReport.saveMatchingAlgorithmReports();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/oclc", method = RequestMethod.POST)
    public String matchingAlgorithmBasedOnOCLC() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForOclc();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/isbn", method = RequestMethod.POST)
    public String matchingAlgorithmBasedOnISBN() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForIsbn();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/issn", method = RequestMethod.POST)
    public String matchingAlgorithmBasedOnISSN() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForIssn();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/lccn", method = RequestMethod.POST)
    public String matchingAlgorithmBasedOnLCCN() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmSaveReport.saveMatchingAlgorithmReportForLccn();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/generateReports/full", method = RequestMethod.POST)
    public String generateReportsForAll(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else if(RecapConstants.EXCEPTION_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/generateReports/oclc", method = RequestMethod.POST)
    public String generateReportsForOclc(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else if(RecapConstants.EXCEPTION_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_OCLC_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/generateReports/isbn", method = RequestMethod.POST)
    public String generateReportsForIsbn(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else if(RecapConstants.EXCEPTION_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_ISBN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/generateReports/issn", method = RequestMethod.POST)
    public String generateReportsForIssn(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else if(RecapConstants.EXCEPTION_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_ISSN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/generateReports/lccn", method = RequestMethod.POST)
    public String generateReportsForLccn(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else if(RecapConstants.EXCEPTION_TYPE.equalsIgnoreCase(reportType)) {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        } else {
            generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_LCCN_FILE_NAME, RecapConstants.ALL_INST, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
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
