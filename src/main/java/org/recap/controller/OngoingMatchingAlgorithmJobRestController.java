package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
    private SolrQueryBuilder solrQueryBuilder;

    @Autowired
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @RequestMapping(value = "/ongoingMatchingAlgorithmJob", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private String startMatchingAlgorithmJob(@RequestBody SolrIndexRequest solrIndexRequest) {
        Date date = solrIndexRequest.getCreatedDate();
        String status = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date fromDate = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date toDate = calendar.getTime();
        String formattedDate = ongoingMatchingAlgorithmUtil.getFormattedDateString(fromDate);
        SolrDocumentList solrDocumentList = ongoingMatchingAlgorithmUtil.fetchDataForOngoingMatchingBasedOnDate(formattedDate);
        status = processOngoingMatchingAlgorithm(solrDocumentList);
        if(RecapConstants.SUCCESS.equalsIgnoreCase(status)) {
            status = matchingBibInfoDetailService.populateMatchingBibInfo(fromDate, toDate);
        }
        return status;
    }

    /**
     * This method is used to process ongoing matching algorithm based on the given bibs in solrDocumentList and updates the CGD and generates report in solr and database.
     * This method is called for batch job.
     * @param solrDocumentList the solr document list
     * @return the string
     */
    public String processOngoingMatchingAlgorithm(SolrDocumentList solrDocumentList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String status = RecapConstants.SUCCESS;
        if(CollectionUtils.isNotEmpty(solrDocumentList)) {
            for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                status = ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument);
            }
        }
        stopWatch.stop();
        logger.info("Total Time taken to execute matching algorithm only : " + stopWatch.getTotalTimeSeconds());
        return status;
    }
}
