package org.recap.rest.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.model.Bib;
import org.recap.util.MarcUtil;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 6/15/16.
 */
public class BibRESTIndexerTest extends BaseTestCase {

    @Test
    public void countFromRESTCall() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/count", String.class);
        Long bibliographicCount = Long.valueOf(response.getBody());
        System.out.println("Bibliographic count " + bibliographicCount);
    }



    @Test
    public void indexRESTCallResponseFindByRangeOfIdsRESTCall() throws Exception {

        bibCrudRepository.deleteAll();

        Integer fromId = 2;
        Integer toId = 3;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/findByRangeOfIds?fromId=" + fromId + "&toId=" + toId, String.class);


        HttpStatus statusCode = response.getStatusCode();
        assertTrue(statusCode.is2xxSuccessful());

        JSONArray jsonArray = new JSONArray(response.getBody());

        List<Bib> bibsToIndex = new ArrayList<Bib>();

        for (int i =0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String bibliographicId = jsonObject.getString("bibliographicId");
            String content = jsonObject.getString("content");

            assertNotNull(bibliographicId);
            assertNotNull(content);

            bibsToIndex.add(generateBibForIndex(jsonObject));
        }

        bibCrudRepository.save(bibsToIndex);
        long count = bibCrudRepository.count();
        assertEquals(2, count);

    }

    private Bib generateBibForIndex(JSONObject jsonObject) {
        Bib bib = new Bib();
        MarcUtil marcUtil = new MarcUtil();
        try {
            bib.setBibId(jsonObject.getString("bibliographicId"));
            String bibContent = jsonObject.getString("content");
            List<Record> records = marcUtil.convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);
            bib.setAuthor(marcUtil.getDataFieldValue(marcRecord, "100", null, null, "a"));
            bib.setTitle(marcUtil.getDataFieldValue(marcRecord, "245", null, null, "a"));
            bib.setIssn(marcUtil.getMultiDataFieldValues(marcRecord, "010", null, null, "a"));

            JSONArray holdingsEntities = jsonObject.getJSONArray("holdingsEntities");
            List<String> holdingsIds = new ArrayList<>();
            for(int j=0;j<holdingsEntities.length();j++){
                JSONObject holdings = holdingsEntities.getJSONObject(j);
                String holdingsId = holdings.getString("holdingsId");
                holdingsIds.add(holdingsId);
            }

            bib.setHoldingsIdList(holdingsIds);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bib;
    }
}
