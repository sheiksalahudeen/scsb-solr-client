package org.recap.rest.service;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.model.solr.MatchingRecordReport;
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
        Map<String, Integer> oclcList = getBibListUsingFacet(facetFieldObject, "OCLCNumber");
        csvUtil.createFile();
        getMatchingList(oclcList);
        csvUtil.closeFile();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    public void getMatchingList(Map<String, Integer> matchingFieldList) {
        for (String fieldValue : matchingFieldList.keySet()) {
            try {
                List<Bib> bibs = bibCrudRepository.findByOclcNumber(fieldValue);
                Map<String, List<MatchingRecordReport>> itemMap = new HashMap<>();
                for (Bib bib : bibs) {
                    List<MatchingRecordReport> matchingRecordReports = new ArrayList<>();
                    for (Integer itemId : bib.getBibItemIdList()) {
                        Item item = itemCrudRepository.findByItemId(itemId);
                        if (item.getCollectionGroupDesignation().equalsIgnoreCase("Shared")) {
                            matchingRecordReports.add(populateMatchingRecordReport(fieldValue, bib, item));
                        }
                    }
                    if (!CollectionUtils.isEmpty(matchingRecordReports)) {
                        itemMap.put(bib.getOwningInstitution(), matchingRecordReports);
                    }
                }
                if (itemMap.size() > 1) {
                    for (String owningInstitution : itemMap.keySet()) {
                        csvUtil.writeMatchingAlgorithmReportToCsv(itemMap.get(owningInstitution));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Integer> getBibListUsingFacet(JSONObject facetFieldObject, String fieldName) {
        Map<String, Integer> matchingFieldValues = new HashMap<>();
        JSONArray fieldObjecArray;
        try {
            fieldObjecArray = facetFieldObject.getJSONArray(fieldName);
            for (int i = 0; i < fieldObjecArray.length(); i++) {
                String fieldValue = "";
                Integer count = 0;
                if (i % 2 == 0) {
                    fieldValue = fieldObjecArray.getString(i);
                    count = fieldObjecArray.getInt(i + 1);
                }
                if (StringUtils.isNotBlank(fieldValue)) {
                    matchingFieldValues.put(fieldValue, count);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return matchingFieldValues;
    }

    public MatchingRecordReport populateMatchingRecordReport(String fieldValue, Bib bib, Item item) {
        MatchingRecordReport matchingRecordReport = new MatchingRecordReport();
        matchingRecordReport.setBibId(bib.getOwningInstitutionBibId());
        matchingRecordReport.setTitle(bib.getTitle());
        matchingRecordReport.setMatchPointContent(fieldValue);
        matchingRecordReport.setInstitutionId(bib.getOwningInstitution());
        matchingRecordReport.setBarcode(item.getBarcode());
        matchingRecordReport.setUseRestrictions(item.getUseRestriction());
        matchingRecordReport.setMatchPointTag("035");
        return matchingRecordReport;
    }

}
