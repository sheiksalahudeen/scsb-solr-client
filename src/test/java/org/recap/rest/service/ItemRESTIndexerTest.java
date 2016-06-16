package org.recap.rest.service;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.Item;
import org.recap.util.ItemJSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 16/6/16.
 */
public class ItemRESTIndexerTest extends BaseTestCase {

    @Test
    public void countFromRESTCall() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(itemResourceURL + "/count", String.class);
        Long itemCount = Long.valueOf(response.getBody());
        System.out.println("Item count " + itemCount);
    }

    @Test
    public void indexRESTCallResponseFindByRangeOfIdsRESTCall() throws Exception {

        itemCrudRepository.deleteAll();

        Integer fromId = 2;
        Integer toId = 3;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(itemResourceURL + "/findByRangeOfIds?fromId=" + fromId + "&toId=" + toId, String.class);


        HttpStatus statusCode = response.getStatusCode();
        assertTrue(statusCode.is2xxSuccessful());

        JSONArray jsonArray = new JSONArray(response.getBody());

        List<Item> itemsToIndex = new ArrayList<Item>();

        for (int i =0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String itemId = jsonObject.getString("itemId");
            String barcode = jsonObject.getString("barcode");

            assertNotNull(itemId);
            assertNotNull(barcode);

            itemsToIndex.add(ItemJSONUtil.getInstance().generateItemForIndex(jsonObject));
        }

        itemCrudRepository.save(itemsToIndex);
        long count = itemCrudRepository.count();
        assertEquals(2, count);

    }
}
