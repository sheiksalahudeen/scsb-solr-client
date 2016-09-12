package org.recap.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.plexus.util.StringUtils;
import org.recap.RecapConstants;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibIndexExecutorService;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.executors.ItemIndexExecutorService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * Created by Sheik on 6/18/2016.
 */
@Controller
public class SolrIndexController {

    Logger logger = LoggerFactory.getLogger(SolrIndexController.class);

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    ItemIndexExecutorService itemIndexExecutorService;

    @Autowired
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    @Autowired
    SolrAdmin solrAdmin;

    @Value("${commit.indexes.interval}")
    public Integer commitIndexesInterval;

    @RequestMapping("/")
    public String solrIndexer(Model model){
        model.addAttribute("solrIndexRequest",new SolrIndexRequest());
        return "solrIndexer";
    }

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/fullIndex", method = RequestMethod.POST)
    public String fullIndex(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) throws Exception {
        String docType = solrIndexRequest.getDocType();
        Integer numberOfThread = solrIndexRequest.getNumberOfThreads();
        Integer numberOfDoc = solrIndexRequest.getNumberOfDocs();
        if (solrIndexRequest.getCommitInterval() == null) {
            solrIndexRequest.setCommitInterval(commitIndexesInterval);
        }
        Integer commitInterval = solrIndexRequest.getCommitInterval();

        logger.info("Document Type : " + docType
                + "   Number of Threads : " + numberOfThread
                + "   Number of Docs :" + numberOfDoc
                + "   Commit Interval :" + commitInterval
                + "   From Date : " + solrIndexRequest.getDateFrom());

        Date fromDate = null;
        if (StringUtils.isNotBlank(solrIndexRequest.getDateFrom())) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(RecapConstants.INCREMENTAL_DATE_FORMAT);
            fromDate = dateFormatter.parse(solrIndexRequest.getDateFrom());
        }

        if (solrIndexRequest.isDoClean()) {
            bibSolrCrudRepository.deleteAll();
            itemCrudRepository.deleteAll();
            try {
                solrAdmin.unloadTempCores();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SolrServerException e) {
                e.printStackTrace();
            }
        }

        String totalTimeTaken = null;
        if (solrIndexRequest.getDocType().equalsIgnoreCase("Bibs")) {
            bibIndexExecutorService.index(solrIndexRequest);
            totalTimeTaken = bibIndexExecutorService.getStopWatch().getTotalTimeSeconds() + " secs";
        } else if (solrIndexRequest.getDocType().equalsIgnoreCase("Items")) {
            itemIndexExecutorService.index(solrIndexRequest);
            totalTimeTaken = itemIndexExecutorService.getStopWatch().getTotalTimeSeconds() + " secs";
        } else {
            bibItemIndexExecutorService.index(solrIndexRequest);
            totalTimeTaken = bibItemIndexExecutorService.getStopWatch().getTotalTimeSeconds() + " secs";
        }

        logger.info("Total time taken:" + totalTimeTaken);

        return solrIndexer(model);
    }

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/report", method = RequestMethod.GET)
    public String report() {
        return "Index process initiated!";
    }
}
