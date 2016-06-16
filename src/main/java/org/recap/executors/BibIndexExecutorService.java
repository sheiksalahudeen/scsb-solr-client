package org.recap.executors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/15/16.
 */

@Service
public class BibIndexExecutorService extends IndexExecutorService {

    @Override
    public Callable getCallable(String coreName, String bibResourceUrl, int from, int to) {
        return new BibIndexCallable(solrUrl, bibResourceUrl, coreName, from, to);
    }

    @Override
    protected Integer getTotalDocCount() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(bibResourceURL + "/count", String.class);
        Integer bibliographicCount = Integer.valueOf(response.getBody());
        return bibliographicCount;

    }

    @Override
    protected String getResourceURL() {
        return bibResourceURL;
    }
}
