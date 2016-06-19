package org.recap.executors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

/**
 * Created by angelind on 16/6/16.
 */
@Service
public class ItemIndexExecutorService extends IndexExecutorService {

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage) {
        return new ItemIndexCallable(solrUrl, coreName, pageNum, docsPerPage);
    }

    @Override
    protected Integer getTotalDocCount() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response =
                restTemplate.getForEntity(itemResourceURL + "/count", String.class);
        Integer bibliographicCount = Integer.valueOf(response.getBody());
        return bibliographicCount;
    }

    @Override
    protected String getResourceURL() {
        return itemResourceURL;
    }
}
