package org.recap.rest.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.util.BibJSONUtil;
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
@Ignore
public class BibRESTIndexerAT extends BaseTestCase {

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
        itemCrudRepository.deleteAll();

        Integer fromId = 2;
        Integer toId = 3;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/findByRangeOfIds?fromId=" + fromId + "&toId=" + toId, String.class);


        HttpStatus statusCode = response.getStatusCode();
        assertTrue(statusCode.is2xxSuccessful());

        JSONArray jsonArray = new JSONArray(response.getBody());

        List<Bib> bibsToIndex = new ArrayList<Bib>();
        List<Item> itemsToIndex = new ArrayList<>();

        for (int i =0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String bibliographicId = jsonObject.getString("bibliographicId");
            String content = jsonObject.getString("content");

            assertNotNull(bibliographicId);
            assertNotNull(content);

            Map<String, List> map = new BibJSONUtil().generateBibAndItemsForIndex(jsonObject);
            Bib bib = (Bib) map.get("Bib");
            bibsToIndex.add(bib);

            List<Item> items = map.get("Item");
            itemsToIndex.addAll(items);
        }

        bibCrudRepository.save(bibsToIndex);
        long count = bibCrudRepository.count();
        assertEquals(2, count);

        itemCrudRepository.save(itemsToIndex);
        assertEquals(4, itemCrudRepository.count());

    }
}
