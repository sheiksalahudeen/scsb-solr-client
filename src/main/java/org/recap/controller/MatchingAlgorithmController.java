package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.executors.MatchingBibItemIndexExecutorService;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
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

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @Autowired
    private MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Autowired
    private ReportGenerator reportGenerator;

    @Autowired
    private MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Value("${matching.algorithm.batchSize}")
    private String matchingAlgoBatchSize;

    @Autowired
    private MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService;

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets matching algorithm helper service.
     *
     * @return the matching algorithm helper service
     */
    public MatchingAlgorithmHelperService getMatchingAlgorithmHelperService() {
        return matchingAlgorithmHelperService;
    }

    /**
     * Gets report generator.
     *
     * @return the report generator
     */
    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    /**
     * Sets report generator.
     *
     * @param reportGenerator the report generator
     */
    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    /**
     * Gets matching algorithm update cgd service.
     *
     * @return the matching algorithm update cgd service
     */
    public MatchingAlgorithmUpdateCGDService getMatchingAlgorithmUpdateCGDService() {
        return matchingAlgorithmUpdateCGDService;
    }

    /**
     * Gets matching bib info detail service.
     *
     * @return the matching bib info detail service
     */
    public MatchingBibInfoDetailService getMatchingBibInfoDetailService() {
        return matchingBibInfoDetailService;
    }

    /**
     * Gets matching algo batch size.
     *
     * @return the matching algo batch size
     */
    public String getMatchingAlgoBatchSize() {
        return matchingAlgoBatchSize;
    }

    /**
     * Gets matching bib item index executor service.
     *
     * @return the matching bib item index executor service
     */
    public MatchingBibItemIndexExecutorService getMatchingBibItemIndexExecutorService() {
        return matchingBibItemIndexExecutorService;
    }

    /**
     * Matching algorithm.
     * First it finds the matching records and updates them in the database (matching_matchpoints_t)
     * Second it gets the matching records details and saves them in database
     * Then it generates reports for single match and multiple-match(based on criterias) accordingly
     *
     * @return the string
     */
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
            stringBuilder.append(RecapConstants.STATUS_DONE ).append("\n");
            stringBuilder.append(RecapConstants.TOTAL_TIME_TAKEN + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            stringBuilder.append(RecapConstants.STATUS_FAILED);
        }
        return stringBuilder.toString();
    }

    /**
     * This method is used for processing reports from the matching bib details .
     *
     * @return the string
     */
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
            stringBuilder.append(RecapConstants.STATUS_DONE ).append("\n");
            stringBuilder.append(RecapConstants.TOTAL_TIME_TAKEN + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            stringBuilder.append(RecapConstants.STATUS_FAILED);
        }
        return stringBuilder.toString();
    }

    /**
     * This method is used to update cgd in database.
     *
     * @return the string
     */
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
            stringBuilder.append(RecapConstants.STATUS_DONE ).append("\n");
            stringBuilder.append(RecapConstants.TOTAL_TIME_TAKEN + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            stringBuilder.append(RecapConstants.STATUS_FAILED);
        }
        return stringBuilder.toString();
    }

    /**
     * This mehtod is used to update cgd in solr.
     *
     * @param matchingAlgoDate the matching algo date
     * @return the string
     */
    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/updateCGDInSolr", method = RequestMethod.POST)
    public String updateCGDInSolr(@Valid @ModelAttribute("matchingAlgoDate") String matchingAlgoDate) {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");

        if(StringUtils.isNotBlank(matchingAlgoDate)) {
            try {
                 sdf.parse(matchingAlgoDate);
            } catch (ParseException e) {
                logger.error("Exception while parsing Date : " + e.getMessage());
            }
        }
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Integer totalProcessedRecords = matchingBibItemIndexExecutorService.indexingForMatchingAlgorithm(RecapConstants.INITIAL_MATCHING_OPERATION_TYPE);
            stopWatch.stop();
            logger.info("Total Time taken to Update CGD In Solr For Matching Algorithm : " + stopWatch.getTotalTimeSeconds());
            String status = "Total number of records processed : " + totalProcessedRecords;
            stringBuilder.append(RecapConstants.STATUS_DONE).append("\n");
            stringBuilder.append(status).append("\n");
            stringBuilder.append(RecapConstants.TOTAL_TIME_TAKEN + stopWatch.getTotalTimeSeconds()).append("\n");
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            stringBuilder.append(RecapConstants.STATUS_FAILED);
        }
        return stringBuilder.toString();
    }

    /**
     * This method is used to populate matching institution bibid information for data dump.
     *
     * @return the string
     */
    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/populateDataForDataDump", method = RequestMethod.POST)
    public String populateDataForDataDump(){
        String respone  = null;
        try {
            respone = matchingBibInfoDetailService.populateMatchingBibInfo();
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return respone;
    }

    private void runReportsForMatchingAlgorithm(Integer batchSize) {
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save OCLC&ISBN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save OCLC&ISSN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save OCLC&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save ISBN&ISSN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save ISBN&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save ISSN&LCCN Combination Reports : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingCountsMap = matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize);
        pulMatchingCount = pulMatchingCount + matchingCountsMap.get(RecapConstants.PUL_MATCHING_COUNT);
        culMatchingCount = culMatchingCount + matchingCountsMap.get(RecapConstants.CUL_MATCHING_COUNT);
        nyplMatchingCount = nyplMatchingCount + matchingCountsMap.get(RecapConstants.NYPL_MATCHING_COUNT);
        stopWatch.stop();
        logger.info("Time taken to save Single Matching Reports : " + stopWatch.getTotalTimeSeconds());

        matchingAlgorithmHelperService.saveMatchingSummaryCount(pulMatchingCount, culMatchingCount, nyplMatchingCount);
    }

    /**
     * This method is used to count items for serials.
     *
     * @return the string
     */
// Added to produce the Summary of serial Item count which came under Matching Algorithm
    @ResponseBody
    @RequestMapping(value = "/matchingAlgorithm/itemsCountForSerials", method = RequestMethod.GET)
    public String itemCountForSerials(){
        StringBuilder response = new StringBuilder();
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        MatchingCounter.reset();
        matchingAlgorithmUpdateCGDService.getItemsCountForSerialsMatching(Integer.valueOf(matchingAlgoBatchSize));
        logger.info("Total PUL Shared Serial Items in Matching : " + MatchingCounter.getPulCGDUpdatedSharedCount());
        logger.info("Total CUL Shared Serial Items in Matching : " + MatchingCounter.getCulCGDUpdatedSharedCount());
        logger.info("Total NYPL Shared Serial Items in Matching : " + MatchingCounter.getNyplCGDUpdatedSharedCount());
        response.append("PUL Shared Serial Items Count : ").append(MatchingCounter.getPulCGDUpdatedSharedCount()).append("\n");
        response.append("CUL Shared Serial Items Count : ").append(MatchingCounter.getCulCGDUpdatedSharedCount()).append("\n");
        response.append("NYPL Shared Serial Items Count : ").append(MatchingCounter.getNyplCGDUpdatedSharedCount());
        stopwatch.stop();
        logger.info("Total Time taken to get the serial items count : " + stopwatch.getTotalTimeSeconds() + " seconds");
        return response.toString();
    }

}
