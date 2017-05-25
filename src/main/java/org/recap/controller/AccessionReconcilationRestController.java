package org.recap.controller;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by akulak on 16/5/17.
 */

@RestController
@RequestMapping("/accessionReconcilationService")
public class AccessionReconcilationRestController {

    @Autowired
    SolrTemplate solrTemplate;

    @RequestMapping(method = RequestMethod.POST,value = "/startAccessionReconcilation")
    public Set<String> startAccessionReconcilation(@RequestBody String barcodes) throws IOException, SolrServerException {
        Set<String> missingBarcodes = new HashSet<>();
        SolrClient solrClient = solrTemplate.getSolrClient();
        String[] splittedBarcodes = barcodes.trim().split(",");
        Set<String> lasBarcodes = new HashSet<>(Arrays.asList(splittedBarcodes));
        SolrQuery solrQuery = getSolrQuery(barcodes, splittedBarcodes.length);
        QueryResponse queryResponse = solrClient.query(solrQuery, SolrRequest.METHOD.POST);
        if (lasBarcodes.size() != queryResponse.getFieldStatsInfo().get(RecapConstants.BARCODE).getCountDistinct()){
            missingBarcodes = getDifference(lasBarcodes, queryResponse);
        }
        return missingBarcodes;
    }

    private Set<String> getDifference(Set<String> lasBarcodes, QueryResponse queryResponse) {
        Set<String> temp = new HashSet<>(lasBarcodes);
        temp.removeAll(queryResponse.getFieldStatsInfo().get(RecapConstants.BARCODE).getDistinctValues());
        return temp;
    }

    private SolrQuery getSolrQuery(String barcode,int rows) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.DOC_TYPE_ITEM);
        solrQuery.setRows(rows);
        solrQuery.addFilterQuery(RecapConstants.BARCODE+":"+ StringEscapeUtils.escapeJava(barcode).replace(",","\" \""));
        solrQuery.setFields(RecapConstants.BARCODE);
        solrQuery.setGetFieldStatistics(true);
        solrQuery.setGetFieldStatistics(RecapConstants.BARCODE);
        solrQuery.addStatsFieldCalcDistinct(RecapConstants.BARCODE, true);
        return solrQuery;
    }

}

