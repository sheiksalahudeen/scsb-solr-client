package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.concurrent.ExecutorService;

/**
 * Created by Sheik on 6/18/2016.
 */
@Controller
public class SolrIndexController {

    @Autowired
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    @RequestMapping("/")
    public String solrIndexer(Model model){
        model.addAttribute("solrIndexRequest",new SolrIndexRequest());
        return "solrIndexer";
    }

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/fullIndex", method = RequestMethod.POST)
    public String fullIndex(@Valid @ModelAttribute("solrIndexRequest") SolrIndexRequest solrIndexRequest,
                            BindingResult result,
                            Model model) {
        Integer numberOfThread = solrIndexRequest.getNumberOfThreads();
        Integer numberOfDoc = solrIndexRequest.getNumberOfDocs();
        System.out.println("Number of Threads : " + numberOfThread + "   Number of Docs :" + numberOfDoc);
        if (solrIndexRequest.isDoClean()) {
            bibSolrCrudRepository.deleteAll();
            itemCrudRepository.deleteAll();
        }
        bibItemIndexExecutorService.index(solrIndexRequest);
        String totalTimeTaken = bibItemIndexExecutorService.getStopWatch().getTotalTimeSeconds() + " secs";

        System.out.println("Total time taken:" + totalTimeTaken);

        return solrIndexer(model);
    }

    @ResponseBody
    @RequestMapping(value = "/solrIndexer/report", method = RequestMethod.GET)
    public String report() {
        ExecutorService executorService = bibItemIndexExecutorService.getExecutorService();
        String status = "Done";
        String processingTime = "";
        String timeString = "Total Time Taken";
        if (null != executorService) {
            boolean shutdown = executorService.isShutdown();
            if(!shutdown) {
                status = "Running";
                timeString = "Processing Time";
                long startTime = bibItemIndexExecutorService.getStartTime();
                long currentTime = System.currentTimeMillis();
                processingTime =(currentTime - startTime) / 1000 + " secs";
            } else {
                processingTime = bibItemIndexExecutorService.getStopWatch().getTotalTimeSeconds() + " secs";
            }
        }
        long numOfDocProcessed = bibSolrCrudRepository.count();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Status  : " + status).append("\n");
        stringBuilder.append(RecapConstants.PROCESSSED_RECORDS + " : " + numOfDocProcessed).append("\n");
        stringBuilder.append(timeString + " : " + processingTime).append("\n");

        return stringBuilder.toString();
    }
}
