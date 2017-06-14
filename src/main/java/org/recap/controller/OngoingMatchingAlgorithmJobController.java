package org.recap.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.util.DateUtil;
import org.recap.util.OngoingMatchingAlgorithmUtil;
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
import java.util.Date;
import java.util.Iterator;

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
    DateUtil dateUtil;

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

    @RequestMapping("/ongoingMatchingJob")
    private String matchingJob(Model model) {
        model.addAttribute("matchingJobFromDate", new Date());
        return "ongoingMatchingJob";
    }

    @RequestMapping(value = "/ongoingMatchingJob", method = RequestMethod.POST)
    @ResponseBody
    public String startMatchingAlgorithmJob(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest) {
        Date date = solrIndexRequest.getCreatedDate();
        String jobType = solrIndexRequest.getProcessType();
        String status = "";
        if(jobType.equalsIgnoreCase(RecapConstants.ONGOING_MATCHING_ALGORITHM_JOB)) {
            String formattedDate = getOngoingMatchingAlgorithmUtil().getFormattedDateString(getDateUtil().getFromDate(date));
            SolrDocumentList solrDocumentList = getOngoingMatchingAlgorithmUtil().fetchDataForOngoingMatchingBasedOnDate(formattedDate);
            status = processOngoingMatchingAlgorithm(solrDocumentList);
        } else if(jobType.equalsIgnoreCase(RecapConstants.POPULATE_DATA_FOR_DATA_DUMP_JOB)) {
            status = getMatchingBibInfoDetailService().populateMatchingBibInfo(getDateUtil().getFromDate(date), getDateUtil().getToDate(date));
        }
        return status;
    }

    /**
     * This method is called from the solr admin ui which is used to process ongoing matching algorithm for the given bibs from the solrDocumentList.
     *
     * @param solrDocumentList the solr document list
     * @return the string
     */
    public String processOngoingMatchingAlgorithm(SolrDocumentList solrDocumentList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String status = "Success";
        if(CollectionUtils.isNotEmpty(solrDocumentList)) {
            for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
                SolrDocument solrDocument = iterator.next();
                status = getOngoingMatchingAlgorithmUtil().processMatchingForBib(solrDocument);
            }
        }
        stopWatch.stop();
        getLogger().info("Total Time taken to execute matching algorithm only : " + stopWatch.getTotalTimeSeconds());
        return status;
    }
}
