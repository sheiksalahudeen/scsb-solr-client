package org.recap.executors;

import org.recap.repository.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/15/16.
 */

@Service
public class BibIndexExecutorService extends IndexExecutorService {


    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Override
    public Callable getCallable(String coreName, String bibResourceUrl, int from, int to) {
        return new BibIndexCallable(solrUrl, bibResourceUrl, coreName, from, to, bibliographicDetailsRepository);
    }

    @Override
    protected Integer getTotalDocCount() {
        RestTemplate restTemplate = new RestTemplate();
        Long count = bibliographicDetailsRepository.count();
        return count.intValue();
    }

    @Override
    protected String getResourceURL() {
        return bibResourceURL;
    }
}
