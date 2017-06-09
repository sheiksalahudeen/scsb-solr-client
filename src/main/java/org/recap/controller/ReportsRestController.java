package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.reports.ReportDataRequest;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.report.ReportGenerator;
import org.recap.util.ReportsServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
@RestController
@RequestMapping("/reportsService")
public class ReportsRestController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsRestController.class);

    @Autowired
    private ReportsServiceUtil reportsServiceUtil;

    @Autowired
    private ReportGenerator reportGenerator;

    /**
     * This method is to get accession and deaccession counts from solr and set those values in ReportResponse Entity.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/accessionDeaccessionCounts", method = RequestMethod.POST)
    public ReportsResponse accessionDeaccessionCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            reportsResponse = reportsServiceUtil.populateAccessionDeaccessionItemCounts(reportsRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method is used to get CGD counts from solr.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/cgdItemCounts", method = RequestMethod.POST)
    public ReportsResponse cgdItemCounts(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            reportsResponse = reportsServiceUtil.populateCgdItemCounts(reportsRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method is used to get detail about deaccession from solr.
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/deaccessionResults", method = RequestMethod.POST)
    public ReportsResponse deaccessionResults(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            reportsResponse = reportsServiceUtil.populateDeaccessionResults(reportsRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method is used to get incomplete records .
     *
     * @param reportsRequest the reports request
     * @return the reports response
     */
    @RequestMapping(value="/incompleteRecords", method = RequestMethod.POST)
    public ReportsResponse incompleteRecords(@RequestBody ReportsRequest reportsRequest) {
        ReportsResponse reportsResponse = new ReportsResponse();
        try {
            reportsResponse = reportsServiceUtil.populateIncompleteRecordsReport(reportsRequest);
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            reportsResponse.setMessage(e.getMessage());
        }
        return reportsResponse;
    }

    /**
     * This method is used to generate csv reports.
     *
     * @param reportDataRequest the report data request
     * @return the string
     */
    @RequestMapping(value="/generateCsvReport", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String generateCsvReports(@RequestBody ReportDataRequest reportDataRequest){
        return reportGenerator.generateReport(reportDataRequest.getFileName(),reportDataRequest.getInstitutionCode(),reportDataRequest.getReportType(),
                reportDataRequest.getTransmissionType(),null,null);
    }

}
