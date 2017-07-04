package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.util.DateUtil;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;

/**
 * Created by angelind on 16/3/17.
 */
@Controller
public class OngoingMatchingAlgorithmJobController {

    private static final Logger logger = LoggerFactory.getLogger(OngoingMatchingAlgorithmJobController.class);

    @Autowired
    private OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Autowired
    private DateUtil dateUtil;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    private String batchSize;

    public Logger getLogger() {
        return logger;
    }

    public OngoingMatchingAlgorithmUtil getOngoingMatchingAlgorithmUtil() {
        return ongoingMatchingAlgorithmUtil;
    }

    public MatchingBibInfoDetailService getMatchingBibInfoDetailService() {
        return matchingBibInfoDetailService;
    }

    public DateUtil getDateUtil() {
        return dateUtil;
    }

    public String getBatchSize() {
        return batchSize;
    }

    @RequestMapping("/ongoingMatchingJob")
    private String matchingJob(Model model) {
        model.addAttribute("matchingJobFromDate", new Date());
        return "ongoingMatchingJob";
    }

    @RequestMapping(value = "/ongoingMatchingJob", method = RequestMethod.POST)
    @ResponseBody
    public String startMatchingAlgorithmJob(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date date = solrIndexRequest.getCreatedDate();
        String jobType = solrIndexRequest.getProcessType();
        String status = "";
        Integer rows = Integer.valueOf(getBatchSize());
        try {
            if (jobType.equalsIgnoreCase(RecapConstants.ONGOING_MATCHING_ALGORITHM_JOB)) {
                status = getOngoingMatchingAlgorithmUtil().fetchUpdatedRecordsAndStartProcess(getDateUtil().getFromDate(date), rows);
            } else if (jobType.equalsIgnoreCase(RecapConstants.POPULATE_DATA_FOR_DATA_DUMP_JOB)) {
                status = getMatchingBibInfoDetailService().populateMatchingBibInfo(getDateUtil().getFromDate(date), getDateUtil().getToDate(date));
            }
        } catch (Exception e) {
            logger.error("Exception : {}", e);
        }
        stopWatch.stop();
        getLogger().info("Total Time taken to complete Ongoing Matching Algorithm : {}", stopWatch.getTotalTimeSeconds());
        return status;
    }
}
