package org.recap.rest.service;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.model.solr.MatchingRecordReport;
import org.recap.service.MatchingAlgorithmHelperService;
import org.recap.util.CsvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 4/7/16.
 */
public class MatchingAlgorithmTest extends BaseTestCase {

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrParentCore;

    @Autowired
    CsvUtil csvUtil;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Test
    public void findMatchingUsingOclcNumber() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(solrQueryUrl, String.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject facetObject = responseJsonObject.getJSONObject("facet_counts");
        JSONObject facetFieldObject = facetObject.getJSONObject("facet_fields");
        Map<String, Integer> oclcList = matchingAlgorithmHelperService.getBibListUsingFacet(facetFieldObject, "OCLCNumber");
        csvUtil.createFile("Matching_Algo_OCLC");
        getMatchingList(oclcList, "OCLCNumber");
        csvUtil.closeFile();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingISBN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISBN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(solrQueryUrl, String.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject facetObject = responseJsonObject.getJSONObject("facet_counts");
        JSONObject facetFieldObject = facetObject.getJSONObject("facet_fields");
        Map<String, Integer> oclcList = matchingAlgorithmHelperService.getBibListUsingFacet(facetFieldObject, "ISBN");
        csvUtil.createFile("Matching_Algo_ISBN");
        getMatchingList(oclcList, "ISBN");
        csvUtil.closeFile();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingISSN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISSN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(solrQueryUrl, String.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject facetObject = responseJsonObject.getJSONObject("facet_counts");
        JSONObject facetFieldObject = facetObject.getJSONObject("facet_fields");
        Map<String, Integer> oclcList = matchingAlgorithmHelperService.getBibListUsingFacet(facetFieldObject, "ISSN");
        csvUtil.createFile("Matching_Algo_ISSN");
        getMatchingList(oclcList, "ISSN");
        csvUtil.closeFile();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void findMatchingUsingLCCN() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(solrQueryUrl, String.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject facetObject = responseJsonObject.getJSONObject("facet_counts");
        JSONObject facetFieldObject = facetObject.getJSONObject("facet_fields");
        Map<String, Integer> oclcList = matchingAlgorithmHelperService.getBibListUsingFacet(facetFieldObject, "LCCN");
        csvUtil.createFile("Matching_Algo_LCCN");
        getMatchingList(oclcList, "LCCN");
        csvUtil.closeFile();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    public void getMatchingList(Map<String, Integer> matchingFieldList, String fieldName) {
        Integer matchingBibsCount = 0;
        for (String fieldValue : matchingFieldList.keySet()) {
            try {
                List<Bib> bibs = matchingAlgorithmHelperService.getBibs(fieldName, fieldValue);
                Map<String, List<MatchingRecordReport>> itemMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(bibs)) {
                    for (Bib bib : bibs) {
                        List<MatchingRecordReport> matchingRecordReports = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(bib.getBibItemIdList())) {
                            for (Integer itemId : bib.getBibItemIdList()) {
                                Item item = itemCrudRepository.findByItemId(itemId);
                                if (item.getCollectionGroupDesignation().equalsIgnoreCase("Shared")) {
                                    matchingRecordReports.add(matchingAlgorithmHelperService.populateMatchingRecordReport(fieldValue, bib, item, fieldName));
                                }
                            }
                        }
                        if (!CollectionUtils.isEmpty(matchingRecordReports)) {
                            itemMap.put(bib.getOwningInstitution(), matchingRecordReports);
                        }
                    }
                }
                if (itemMap.size() > 1) {
                    matchingBibsCount = matchingBibsCount + bibs.size();
                    for (String owningInstitution : itemMap.keySet()) {
                        csvUtil.writeMatchingAlgorithmReportToCsv(itemMap.get(owningInstitution));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Total Matching Bibs Count : " + matchingBibsCount);
    }

}
