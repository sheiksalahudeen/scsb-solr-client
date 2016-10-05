package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
@Service
public class BibItemIndexExecutorService extends IndexExecutorService {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, Integer owningInstitutionId) {
        return new BibItemIndexCallable(solrUrl, coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, owningInstitutionId, producerTemplate, solrTemplate);
    }

    @Override
    protected Integer getTotalDocCount(Integer owningInstitutionId) {
        Long count = owningInstitutionId == null ? bibliographicDetailsRepository.countByIsDeletedFalse() : bibliographicDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);
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
