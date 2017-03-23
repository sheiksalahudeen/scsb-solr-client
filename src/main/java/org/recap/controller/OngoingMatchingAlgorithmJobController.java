package org.recap.controller;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.recap.util.SolrQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by angelind on 16/3/17.
 */
@Controller
public class OngoingMatchingAlgorithmJobController {

    private static final Logger logger = LoggerFactory.getLogger(OngoingMatchingAlgorithmJobController.class);

    @Autowired
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @RequestMapping("/ongoingMatchingJob")
    private String matchingJob(Model model) {
        model.addAttribute("matchingJobFromDate", new Date());
        return "ongoingMatchingJob";
    }

    @RequestMapping(value = "/ongoingMatchingJob", method = RequestMethod.POST)
    @ResponseBody
    private String startMatchingAlgorithmJob(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(solrIndexRequest.getCreatedDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        String formattedDate = ongoingMatchingAlgorithmUtil.getFormattedDateString(cal.getTime());
        SolrDocumentList solrDocumentList = ongoingMatchingAlgorithmUtil.fetchDataForOngoingMatchingBasedOnDate(formattedDate);
        return processOngoingMatchingAlgorithm(solrDocumentList);
    }

    public String processOngoingMatchingAlgorithm(SolrDocumentList solrDocumentList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String status = "Success";
        for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
            SolrDocument solrDocument = iterator.next();
            status = ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument);
        }
        stopWatch.stop();
        logger.info("Total Time taken to execute matching algorithm only : " + stopWatch.getTotalTimeSeconds());
        return status;
    }
}
