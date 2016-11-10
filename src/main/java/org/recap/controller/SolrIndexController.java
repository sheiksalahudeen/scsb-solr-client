package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.plexus.util.StringUtils;
import org.recap.RecapConstants;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibIndexExecutorService;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.executors.HoldingsIndexExecutorService;
import org.recap.executors.ItemIndexExecutorService;
import org.recap.matchingAlgorithm.report.ReportGenerator;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.util.BibJSONUtil;
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
import java.text.SimpleDateFormat;
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

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/generateReports", method = RequestMethod.POST)
    public String generateReports(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                                         BindingResult result,
                                         Model model) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Date createdDate = solrIndexRequest.getCreatedDate();
        if(createdDate == null) {
            createdDate = new Date();
        }
        String reportType = solrIndexRequest.getReportType();
        String generatedReportFileName = null;
        String owningInstitutionCode = solrIndexRequest.getOwningInstitutionCode();
        String status = "";
        generatedReportFileName = reportGenerator.generateReport(RecapConstants.SOLR_INDEX_FAILURE_REPORT, owningInstitutionCode, reportType, solrIndexRequest.getTransmissionType(), getFromDate(createdDate), getToDate(createdDate));
        if(StringUtils.isEmpty(generatedReportFileName)) {
            status = "Report wasn't generated! Please contact help desk!";
        } else {
            status = "The Generated Report File Name : " + generatedReportFileName;
        }
        stopWatch.stop();
        logger.info("Total time taken to generate File : " + stopWatch.getTotalTimeSeconds());
        return status;
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
