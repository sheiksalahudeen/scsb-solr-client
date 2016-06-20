package org.recap.controller;

import org.recap.executors.BibIndexExecutorService;
import org.recap.model.solr.SolrIndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Sheik on 6/18/2016.
 */
@Controller
public class SolrIndexController {

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @RequestMapping("/")
    public String solrIndexer(Model model){
        model.addAttribute("solrIndexRequest",new SolrIndexRequest());
        return "solrIndexer";
    }

    @RequestMapping(value="/solrIndexer", params={"fullIndex"})
    public String fullIndex(final SolrIndexRequest solrIndexRequest, final BindingResult bindingResult, Model model) {

        Integer numberOfThread = solrIndexRequest.getNumberOfThread();
        Integer numberOfDoc = solrIndexRequest.getNumberOfDoc();
        System.out.println("Number of Thread : " + numberOfThread + "   Number of Doc :" + numberOfDoc);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibIndexExecutorService.index(numberOfThread, numberOfDoc);
        stopWatch.stop();

        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());

        return solrIndexer(model);
    }
}
