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
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
@RestController
@RequestMapping("/ongoingMatchingAlgorithmService")
public class OngoingMatchingAlgorithmJobRestController {

    private static final Logger logger = LoggerFactory.getLogger(OngoingMatchingAlgorithmJobRestController.class);

    @Autowired
    private OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Autowired
    DateUtil dateUtil;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    private String batchSize;

    @RequestMapping(value = "/ongoingMatchingAlgorithmJob", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String startMatchingAlgorithmJob(@RequestBody SolrIndexRequest solrIndexRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date date = solrIndexRequest.getCreatedDate();
        String status="";
        Integer rows = Integer.valueOf(batchSize);
        try {
            status = ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(dateUtil.getFromDate(date), rows);
            if(RecapConstants.SUCCESS.equalsIgnoreCase(status)) {
                status = matchingBibInfoDetailService.populateMatchingBibInfo(dateUtil.getFromDate(date), dateUtil.getToDate(date));
            }
        } catch (Exception e) {
            logger.error("Exception : {}", e);
        }
        stopWatch.stop();
        logger.info("Total Time taken to complete Ongoing Matching Algorithm : {}", stopWatch.getTotalTimeSeconds());
        return status;
    }
}
