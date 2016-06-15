package org.recap.rest.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Created by chenchulakshmig on 6/15/16.
 */
public class Bibliographic_Test extends BaseTestCase {

    String resourceUrl = "http://localhost:8080/bibliographic";

    @Test
    public void count() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(resourceUrl + "/count", String.class);
        Long bibliographicCount = Long.valueOf(response.getBody());
        System.out.println("Bibliographic count " + bibliographicCount);
    }
}
