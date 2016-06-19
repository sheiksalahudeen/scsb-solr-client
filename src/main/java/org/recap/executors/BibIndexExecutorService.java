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
    public Callable getCallable(String coreName, int startingPage, int numRecordsPerPage) {
        return new BibIndexCallable(solrUrl, coreName, startingPage, numRecordsPerPage, bibliographicDetailsRepository);
    }

    @Override
    protected Integer getTotalDocCount() {
        Long count = bibliographicDetailsRepository.count();
        return count.intValue();
    }

    @Override
    protected String getResourceURL() {
        return bibResourceURL;
    }

    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}
