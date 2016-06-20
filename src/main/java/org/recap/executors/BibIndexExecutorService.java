package org.recap.executors;

import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/15/16.
 */

@Service
public class BibIndexExecutorService extends IndexExecutorService {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage) {
        return new BibIndexCallable(solrUrl, coreName, pageNum, docsPerPage, bibliographicDetailsRepository);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId) {
        Long count = owningInstitutionId == null ? bibliographicDetailsRepository.count() : bibliographicDetailsRepository.countByOwningInstitutionId(owningInstitutionId);
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
