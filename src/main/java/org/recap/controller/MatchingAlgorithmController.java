package org.recap.controller;

import org.recap.MatchingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by angelind on 12/7/16.
 */
@Controller
public class MatchingAlgorithmController {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @Autowired
    MatchingAlgorithm matchingAlgorithm;

    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/full", method = RequestMethod.POST)
    public String matchingAlgorithmFull() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            matchingAlgorithm.generateMatchingAlgorithmReport();
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
            matchingAlgorithm.generateMatchingAlgorithmReportForOclc();
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
            matchingAlgorithm.generateMatchingAlgorithmReportForIsbn();
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
            matchingAlgorithm.generateMatchingAlgorithmReportForIssn();
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
            matchingAlgorithm.generateMatchingAlgorithmReportForLccn();
            stopWatch.stop();
            logger.info("Total Time taken to process Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            stringBuilder.append("Status  : Done" ).append("\n");
            stringBuilder.append("Total Time Taken  : " + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error("Exception : " + e.getMessage());
        }
        return stringBuilder.toString();
    }
}
