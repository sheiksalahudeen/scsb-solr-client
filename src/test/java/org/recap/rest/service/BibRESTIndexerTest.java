package org.recap.rest.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by chenchulakshmig on 6/15/16.
 */
public class BibRESTIndexerTest extends BaseTestCase {



    @Test
    public void count() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/count", String.class);
        Long bibliographicCount = Long.valueOf(response.getBody());
        System.out.println("Bibliographic count " + bibliographicCount);
    }
}
