package org.recap.rest.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
        Integer fromId = 2;
        Integer toId = 3;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/findByRangeOfIds?fromId=" + fromId + "&toId=" + toId, String.class);


        HttpStatus statusCode = response.getStatusCode();
        assertTrue(statusCode.is2xxSuccessful());
    }
}
