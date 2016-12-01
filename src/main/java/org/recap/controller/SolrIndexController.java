package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.plexus.util.StringUtils;
import org.recap.RecapConstants;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibIndexExecutorService;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.executors.HoldingsIndexExecutorService;
import org.recap.executors.ItemIndexExecutorService;
import org.recap.report.ReportGenerator;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sheik on 6/18/2016.
 */
@Controller
public class SolrIndexController {

    Logger logger = LoggerFactory.getLogger(SolrIndexController.class);

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    HoldingsIndexExecutorService holdingsIndexExecutorService;

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

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${solr.parent.core}")
    String solrCore;

    @RequestMapping("/scsbadmin")
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

        Integer totalProcessedRecords = bibItemIndexExecutorService.index(solrIndexRequest);
        String status = "Total number of records processed : " + totalProcessedRecords;

        return report(status);
    }

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/report", method = RequestMethod.GET)
    public String report(String status) {
        return StringUtils.isBlank(status) ? "Index process initiated!" : status;
    }
}
