package org.recap.executors;

import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 30/1/17.
 */
@Service
public class MatchingBibItemIndexExecutorService extends MatchingIndexExecutorService {

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * Gets the callable class for the thread to process.
     * @param coreName      the core name
     * @param pageNum       the page num
     * @param docsPerPage
     * @param operationType the operation type
     * @return
     */
    @Override
    public Callable getCallable(String coreName, int pageNum, int docsPerPage, String operationType, Date from, Date to) {
        return new MatchingBibItemIndexCallable(coreName, pageNum, docsPerPage, bibliographicDetailsRepository, holdingsDetailsRepository, producerTemplate, solrTemplate, operationType, from, to);
    }

    /**
     * Gets the total documents count from database by operation type.
     * @param operationType the operation type
     * @return
     */
    @Override
    protected Integer getTotalDocCount(String operationType, Date from, Date to) {
        Long bibCountForChangedItems;
        bibCountForChangedItems = bibliographicDetailsRepository.getCountOfBibliographicEntitiesForChangedItems(operationType, from, to);
        return bibCountForChangedItems.intValue();
    }

    /**
     * This method sets bibliographic details repository.
     *
     * @param bibliographicDetailsRepository the bibliographic details repository
     */
    public void setBibliographicDetailsRepository(BibliographicDetailsRepository bibliographicDetailsRepository) {
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
    }
}