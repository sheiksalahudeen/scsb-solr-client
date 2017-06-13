package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by angelind on 30/1/17.
 */
public class MatchingBibItemIndexCallable implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(MatchingBibItemIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private String coreName;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private HoldingsDetailsRepository holdingsDetailsRepository;
    private ProducerTemplate producerTemplate;
    private SolrTemplate solrTemplate;
    private String operationType;
    private Date from;
    private Date to;

    /**
     * This method instantiates a new matching bib item index callable.
     *
     * @param coreName                       the core name
     * @param pageNum                        the page num
     * @param docsPerPage                    the docs per page
     * @param bibliographicDetailsRepository the bibliographic details repository
     * @param holdingsDetailsRepository      the holdings details repository
     * @param producerTemplate               the producer template
     * @param solrTemplate                   the solr template
     * @param operationType                  the operation type
     * @param from                           the from date
     * @param to                             the to date
     */
    public MatchingBibItemIndexCallable(String coreName, int pageNum, int docsPerPage, BibliographicDetailsRepository bibliographicDetailsRepository,
                                        HoldingsDetailsRepository holdingsDetailsRepository, ProducerTemplate producerTemplate, SolrTemplate solrTemplate, String operationType,
                                        Date from, Date to) {
        this.coreName = coreName;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
        this.producerTemplate = producerTemplate;
        this.solrTemplate = solrTemplate;
        this.operationType = operationType;
        this.from = from;
        this.to = to;
    }

    /**
     * This method is processed by thread to generate solr input documents and index to solr.
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {

        Page<BibliographicEntity> bibliographicEntities;

        bibliographicEntities = bibliographicDetailsRepository.getBibliographicEntitiesForChangedItems(new PageRequest(pageNum, docsPerPage), operationType, from, to);

        logger.info("Num Bibs Fetched : " + bibliographicEntities.getNumberOfElements());
        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();


        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            BibliographicEntity bibliographicEntity = iterator.next();
            Future submit = executorService.submit(new BibItemRecordSetupCallable(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository, producerTemplate));
            futures.add(submit);
        }

        logger.info("Num futures to prepare Bib and Associated data : {} ",futures.size());

        List<SolrInputDocument> solrInputDocumentsToIndex = new ArrayList<>();
        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            try {
                Future future = futureIterator.next();
                SolrInputDocument solrInputDocument = (SolrInputDocument) future.get();
                if(solrInputDocument != null)
                    solrInputDocumentsToIndex.add(solrInputDocument);
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }

        executorService.shutdown();

        if (!CollectionUtils.isEmpty(solrInputDocumentsToIndex)) {
            producerTemplate.sendBodyAndHeader(RecapConstants.SOLR_QUEUE, solrInputDocumentsToIndex, RecapConstants.SOLR_CORE, coreName);
        }
        return solrInputDocumentsToIndex.size();
    }
}
