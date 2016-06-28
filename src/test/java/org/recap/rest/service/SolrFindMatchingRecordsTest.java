package org.recap.rest.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.Bib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 22/6/16.
 */
public class SolrFindMatchingRecordsTest extends BaseTestCase {

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrParentCore;

    @Autowired
    SolrClient solrClient;

    @Test
    public void fetchUsingFacetQuery() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + File.separator + solrParentCore + File.separator + "query?q=DocType:Bib&facet=true&facet.field=OCLCNumber&facet.field=ISBN&facet.field=ISSN&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(solrQueryUrl, String.class);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject facetObject = responseJsonObject.getJSONObject("facet_counts");
        JSONObject facetFieldObject = facetObject.getJSONObject("facet_fields");
        Map<String, Integer> oclcList = getBibListUsingFacet(facetFieldObject, "OCLCNumber");
        Map<String, Integer> isbnList = getBibListUsingFacet(facetFieldObject, "ISBN");
        Map<String, Integer> issnList = getBibListUsingFacet(facetFieldObject, "ISSN");
        Map<String, Integer> lccnList = getBibListUsingFacet(facetFieldObject, "LCCN");
        FileWriter fileWriter = new FileWriter("matchingReportsForFacet.csv");
        writeContentsForFacet(oclcList, fileWriter, "OclcNumber");
        writeContentsForFacet(isbnList, fileWriter, "ISBN");
        writeContentsForFacet(issnList, fileWriter, "ISSN");
        writeContentsForFacet(lccnList, fileWriter, "LCCN");
        fileWriter.flush();
        fileWriter.close();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    private Map<String, Integer> getBibListUsingFacet(JSONObject facetFieldObject, String fieldName) {
        Map<String, Integer> matchingFieldValues = new HashMap<>();
        JSONArray fieldObjecArray = null;
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

    private void writeContentsForFacet(Map<String, Integer> matchingList, FileWriter fileWriter, String fieldName) throws IOException {
        fileWriter.append(fieldName + " , Matching Count");
        fileWriter.append("\n");
        for (String fieldValue : matchingList.keySet()) {
            fileWriter.append(fieldValue + "," + matchingList.get(fieldValue));
            fileWriter.append("\n");
        }
        fileWriter.append("\n");
    }

    @Test
    public void fetchUsingGroupByQuery() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Bib> matchedBibList = new HashMap<>();
        Map<String, List<Bib>> oclcMatchingBibList = getMatchingBibByGroupQuery(matchedBibList, "OCLCNumber");
        Map<String, List<Bib>> isbnMatchingBibList = getMatchingBibByGroupQuery(matchedBibList, "ISBN");
        Map<String, List<Bib>> issnMatchingBibList = getMatchingBibByGroupQuery(matchedBibList, "ISSN");
        Map<String, List<Bib>> lccnMatchingBibList = getMatchingBibByGroupQuery(matchedBibList, "LCCN");
        FileWriter fileWriter = new FileWriter("matchingReports.csv");
        writeContentsForGrouping(oclcMatchingBibList, fileWriter, "OclcNumber");
        writeContentsForGrouping(isbnMatchingBibList, fileWriter, "ISBN");
        writeContentsForGrouping(issnMatchingBibList, fileWriter, "ISSN");
        writeContentsForGrouping(lccnMatchingBibList, fileWriter, "LCCN");
        fileWriter.flush();
        fileWriter.close();
        stopWatch.stop();
        System.out.println("Total Time Taken : " + stopWatch.getTotalTimeSeconds());
    }

    private Map<String, List<Bib>> getMatchingBibByGroupQuery(Map<String, Bib> matchedBibList, String fieldName) throws JSONException {
        ResponseEntity<String> responseEntity = restCall(fieldName);
        assertTrue(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK"));
        JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
        JSONObject groupedJsonObject = responseJsonObject.getJSONObject("grouped");
        JSONObject solrJsonObject = groupedJsonObject.getJSONObject(fieldName);
        return getMatchedBibList(matchedBibList, solrJsonObject);
    }

    private void writeContentsForGrouping(Map<String, List<Bib>> matchingBibList, FileWriter fileWriter, String fieldName) throws IOException {
        fileWriter.append(fieldName + " , BibId");
        fileWriter.append("\n");
        for (String fieldValue : matchingBibList.keySet()) {
            for (Bib bib : matchingBibList.get(fieldValue)) {
                fileWriter.append(fieldValue + ",");
                fileWriter.append(bib.getBibId());
                fileWriter.append("\n");
            }
        }
    }

    private ResponseEntity<String> restCall(String fieldName) {
        RestTemplate restTemplate = new RestTemplate();
        String solrQueryUrl = solrUrl + File.separator + solrParentCore + File.separator + "query?q=DocType:Bib&fq=" + fieldName + ":['' TO *]&wt=json&group=true&group.field=" + fieldName + "&group.limit=1000&rows=-1";
        return restTemplate.getForEntity(solrQueryUrl, String.class);
    }

    private Map<String, List<Bib>> getMatchedBibList(Map<String, Bib> matchedBibList, JSONObject solrJsonObject) throws JSONException {
        JSONArray groups = solrJsonObject.getJSONArray("groups");
        List<Bib> bibs;
        Map<String, List<Bib>> matchingBibsWithField = new HashMap<>();
        for (int i = 0; i < groups.length(); i++) {
            bibs = new ArrayList<>();
            JSONObject group = groups.getJSONObject(i);
            JSONObject docList = group.getJSONObject("doclist");
            Integer numFound = (Integer) docList.get("numFound");
            if (numFound > 1) {
                JSONArray docs = docList.getJSONArray("docs");
                for (int j = 0; j < docs.length(); j++) {
                    JSONObject jsonObject = docs.getJSONObject(j);
                    Bib bib = convertToBibObject(jsonObject);
                    matchedBibList.put(bib.getBibId(), bib);
                    bibs.add(bib);
                }
                matchingBibsWithField.put(group.getString("groupValue"), bibs);
            }
        }
        return matchingBibsWithField;
    }

    private Bib convertToBibObject(JSONObject jsonObject) {
        Bib bib = new Bib();
        try {
            bib.setId(jsonObject.getString("id"));
            bib.setBibId(jsonObject.getString("BibId"));
            bib.setTitle(!jsonObject.isNull("Title") ? jsonObject.getString("Title") : "");
            bib.setAuthor(!jsonObject.isNull("Author") ? jsonObject.getString("Author") : "");
            bib.setDocType(!jsonObject.isNull("DocType") ? jsonObject.getString("DocType") : "");
            bib.setIsbn(Arrays.asList(!jsonObject.isNull("ISBN") ? jsonObject.getJSONArray("ISBN").toString() : ""));
            bib.setIssn(Arrays.asList(!jsonObject.isNull("ISSN") ? jsonObject.getJSONArray("ISSN").toString() : ""));
            bib.setOclcNumber(Arrays.asList(!jsonObject.isNull("OCLCNumber") ? jsonObject.getJSONArray("OCLCNumber").toString() : ""));
            bib.setMaterialType(!jsonObject.isNull("MaterialType") ? jsonObject.getString("MaterialType") : "");
            bib.setNotes(!jsonObject.isNull("Notes") ? jsonObject.getString("Notes") : "");
            bib.setOwningInstitution(!jsonObject.isNull("OwningInstitution") ? jsonObject.getString("OwningInstitution") : "");
            bib.setPublicationDate(!jsonObject.isNull("PublicationDate") ? jsonObject.getString("PublicationDate") : "");
            bib.setPublicationPlace(!jsonObject.isNull("PublicationPlace") ? jsonObject.getString("PublicationPlace") : "");
            bib.setPublisher(!jsonObject.isNull("Publisher") ? jsonObject.getString("Publisher") : "");
            bib.setSubject(!jsonObject.isNull("Subject") ? jsonObject.getString("Subject") : "");
            bib.setHoldingsIdList(Arrays.asList(!jsonObject.isNull("HoldingsId") ? jsonObject.getJSONArray("HoldingsId").toString() : ""));
            bib.setBibItemIdList(Arrays.asList(!jsonObject.isNull("BibItemId") ? jsonObject.getJSONArray("BibItemId").toString() : ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bib;
    }
}
