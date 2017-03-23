package org.recap.executors;

import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

/**
 * Created by angelind on 30/1/17.
 */
@Service
public class MatchingBibItemIndexExecutorService extends MatchingIndexExecutorService {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, String operationType) {
        return new MatchingBibItemIndexCallable(solrServerProtocol + solrUrl, coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, producerTemplate, solrTemplate, operationType);
    }

    @Override
    protected Integer getTotalDocCount(String operationType) {
        Long bibCountForChangedItems;
        bibCountForChangedItems = bibliographicDetailsRepository.getCountOfBibliographicEntitiesForChangedItems(operationType);
        return bibCountForChangedItems.intValue();
    }

    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}