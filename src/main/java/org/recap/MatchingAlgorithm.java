package org.recap;

import org.recap.model.solr.Bib;
import org.recap.model.solr.MatchingRecordReport;
import org.recap.service.MatchingAlgorithmHelperService;
import org.codehaus.jettison.json.JSONObject;
import org.recap.util.CsvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by angelind on 12/7/16.
 */
@Component
public class MatchingAlgorithm {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithm.class);

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrParentCore;

    @Autowired
    CsvUtil csvUtil;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    public void generateMatchingAlgorithmReport() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.field=ISBN&facet.field=ISSN&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "OCLCNumber");
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISBN");
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISSN");
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "LCCN");
            csvUtil.createFile("Matching_Algo_Phase1");
            getMatchingList(duplicateOCLCMap, "OCLCNumber");
            getMatchingList(duplicateISBNMap, "ISBN");
            getMatchingList(duplicateISSNMap, "ISSN");
            getMatchingList(duplicateLCCNMap, "LCCN");
            csvUtil.closeFile();
        }
    }

    public void generateMatchingAlgorithmReportForOclc() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "OCLCNumber");
            csvUtil.createFile("Matching_Algo_OCLC");
            getMatchingList(duplicateISBNMap, "OCLCNumber");
            csvUtil.closeFile();
        }
    }

    public void generateMatchingAlgorithmReportForIsbn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISBN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISBN");
            csvUtil.createFile("Matching_Algo_ISBN");
            getMatchingList(duplicateISSNMap, "ISBN");
            csvUtil.closeFile();
        }
    }

    public void generateMatchingAlgorithmReportForIssn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISSN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISSN");
            csvUtil.createFile("Matching_Algo_ISSN");
            getMatchingList(duplicateLCCNMap, "ISSN");
            csvUtil.closeFile();
        }
    }

    public void generateMatchingAlgorithmReportForLccn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "LCCN");
            csvUtil.createFile("Matching_Algo_LCCN");
            getMatchingList(duplicateOCLCMap, "LCCN");
            csvUtil.closeFile();
        }
    }

    public void getMatchingList(Map<String, Integer> matchingFieldList, String fieldName) {
        Integer matchingBibsCount = 0;
        for (String fieldValue : matchingFieldList.keySet()) {
            try {
                List<Bib> bibs = matchingAlgorithmHelperService.getBibs(fieldName, fieldValue);
                Map<String, List<MatchingRecordReport>> owningInstitutionMap = matchingAlgorithmHelperService.getMatchingReports(fieldName, fieldValue, bibs);
                if (owningInstitutionMap.size() > 1) {
                    matchingBibsCount = matchingBibsCount + bibs.size();
                    for (String owningInstitution : owningInstitutionMap.keySet()) {
                        csvUtil.writeMatchingAlgorithmReportToCsv(owningInstitutionMap.get(owningInstitution));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("Total Matching Bibs Found Based on " + fieldName + " : " + matchingBibsCount);
    }
}
