package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.util.ReportsServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private Logger logger = LoggerFactory.getLogger(ReportsRestController.class);

    @Autowired
    ReportsServiceUtil reportsServiceUtil;

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
}
