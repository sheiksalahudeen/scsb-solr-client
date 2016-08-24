package org.recap.matchingAlgorithm;

import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.solr.Bib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by angelind on 12/7/16.
 */
@Component
public class MatchingAlgorithmSaveReport {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmSaveReport.class);

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrParentCore;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    public void saveMatchingAlgorithmReports() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.field=ISBN&facet.field=ISSN&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_OCLC);
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_ISBN);
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_ISSN);
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_LCCN);
            getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME);
            getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME);
            getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME);
            getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME);
        }
    }

    public void saveMatchingAlgorithmReportForOclc() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "OCLCNumber");
            getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME);
        }
    }

    public void saveMatchingAlgorithmReportForIsbn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISBN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISBN");
            getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME);
        }
    }

    public void saveMatchingAlgorithmReportForIssn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISSN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISSN");
            getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME);
        }
    }

    public void saveMatchingAlgorithmReportForLccn() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "LCCN");
            getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME);
        }
    }

    public void getMatchingList(Map<String, Integer> matchingFieldList, String fieldName, String matchingFileName, String exceptionReportFileName) {
        for (String fieldValue : matchingFieldList.keySet()) {
            Set<Bib> matchingExceptionSet = new HashSet<>();
            try {
                List<Bib> bibs = matchingAlgorithmHelperService.getBibs(fieldName, fieldValue);
                Map<String, Set<Bib>> owningInstitutionMap = matchingAlgorithmHelperService.getMatchingReports(fieldName, fieldValue, bibs, matchingExceptionSet);
                if (owningInstitutionMap.size() > 1) {
                    for (String owningInstitution : owningInstitutionMap.keySet()) {
                        Set<Bib> bibSet = owningInstitutionMap.get(owningInstitution);
                        for(Bib bib : bibSet) {
                            matchingAlgorithmHelperService.populateAndSaveReportEntity(fieldName, fieldValue, bib, matchingFileName, RecapConstants.MATCHING_TYPE);
                        }
                    }
                }
                generateExceptionReport(fieldName, fieldValue, matchingExceptionSet, exceptionReportFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void generateExceptionReport(String fieldName, String fieldValue, Set<Bib> matchingExceptionSet, String exceptionReportFileName) {
        if(!CollectionUtils.isEmpty(matchingExceptionSet)) {
            Map<String, Set<Bib>> owningInstitutionMap = matchingAlgorithmHelperService.getBibsForOwningInstitution(matchingExceptionSet);
            if(owningInstitutionMap.size() > 1) {
                for(String owningInstitution : owningInstitutionMap.keySet()) {
                    Set<Bib> bibSet = owningInstitutionMap.get(owningInstitution);
                    for(Bib bib : bibSet) {
                        matchingAlgorithmHelperService.populateAndSaveReportEntity(fieldName, fieldValue, bib, exceptionReportFileName, RecapConstants.EXCEPTION_TYPE);
                    }
                }
            }
        }
    }
}
