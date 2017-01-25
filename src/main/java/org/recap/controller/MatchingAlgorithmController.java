package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by angelind on 12/7/16.
 */
@Controller
public class MatchingAlgorithmController {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Value("${matching.algorithm.batchSize}")
    public String matchingAlgoBatchSize;

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/full", method = RequestMethod.POST)
    public String matchingAlgorithmFull() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            StopWatch stopWatch1 = new StopWatch();
            stopWatch1.start();
            matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities();
            stopWatch1.stop();
            logger.info("Time taken to save Match point Entity : " + stopWatch1.getTotalTimeSeconds());
            stopWatch1 = new StopWatch();
            stopWatch1.start();
            matchingAlgorithmHelperService.populateMatchingBibEntities();
            stopWatch1.stop();
            logger.info("Time taken to save Matching Bib Entity : " + stopWatch1.getTotalTimeSeconds());
            runReportsForMatchingAlgorithm(Integer.valueOf(matchingAlgoBatchSize));

            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
            stringBuilder.append("Status : Failed");
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/reports", method = RequestMethod.POST)
    public String matchingAlgorithmOnlyReports() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            runReportsForMatchingAlgorithm(Integer.valueOf(matchingAlgoBatchSize));
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm Reports : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
            stringBuilder.append("Status : Failed");
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/updateCGDInDB", method = RequestMethod.POST)
    public String updateCGDInDB() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmUpdateCGDService.updateCGDProcessForMonographs(Integer.valueOf(matchingAlgoBatchSize));
            stopWatch.stop();
            logger.info("Total Time taken to Update CGD In DB For Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
            stringBuilder.append("Status : Failed");
        }
        return stringBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/updateCGDInSolr", method = RequestMethod.POST)
    public String updateCGDInSolr(@Valid @ModelAttribute("matchingAlgoDate") String matchingAlgoDate) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        Date lastUpdatedDate = null;
        if(StringUtils.isNotBlank(matchingAlgoDate)) {
            try {
                lastUpdatedDate = sdf.parse(matchingAlgoDate);
            } catch (ParseException e) {
                logger.error("Exception while parsing Date : " + e.getMessage());
            }
        }
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithmUpdateCGDService.updateCGDForItemsInSolr(Integer.valueOf(matchingAlgoBatchSize));
            stopWatch.stop();
            logger.info("Total Time taken to Update CGD In Solr For Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
            stringBuilder.append("Status : Failed");
        }
        return stringBuilder.toString();
    }

    private void runReportsForMatchingAlgorithm(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save OCLC&ISBN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save OCLC&ISSN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save OCLC&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save ISBN&ISSN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save ISBN&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save ISSN&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get("pulMatchingCount");
        culMatchingCount = culMatchingCount + matchingCountsMap.get("culMatchingCount");
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get("nyplMatchingCount");
        stopWatch.stop();
        logger.info("Time taken to save Single Matching Reports : " + stopWatch.getTotalTimeSeconds());

        matchingAlgorithmHelperService.saveMatchingSummaryCount(pulMatchingCount, culMatchingCount, nyplMatchingCount);
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
