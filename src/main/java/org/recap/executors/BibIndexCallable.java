package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by pvsubrah on 6/13/16.
 */


public class BibIndexCallable implements Callable {

    Logger logger = LoggerFactory.getLogger(BibIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private String coreName;
    private String solrURL;
    private Integer owningInstitutionId;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private ProducerTemplate producerTemplate;

    public BibIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage, BibliographicDetailsRepository bibliographicDetailsRepository, Integer owningInstitutionId, ProducerTemplate producerTemplate) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {

        Page<BibliographicEntity> bibliographicEntities = owningInstitutionId == null ?
                bibliographicDetailsRepository.findAll(new PageRequest(pageNum, docsPerPage)) :
                bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        logger.info("Num Bibs Fetched : " + bibliographicEntities.getNumberOfElements());
        List<Bib> bibsToIndex = new ArrayList<>();

        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while(iterator.hasNext()){
            BibliographicEntity bibliographicEntity = iterator.next();
            Future submit = executorService.submit(new BibRecordSetupCallable(bibliographicEntity));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            try {
                Future future = futureIterator.next();
                Bib bib = (Bib) future.get();
                bibsToIndex.add(bib);
            } catch (Exception e) {
                logger.error("Exception : " + e.getMessage());
            }
        }

        executorService.shutdown();

        logger.info("No of Bibs to index : " + bibsToIndex.size());

        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            producerTemplate.sendBodyAndHeader(RecapConstants.SOLR_QUEUE, bibsToIndex, RecapConstants.SOLR_CORE, coreName);
        }
        return bibsToIndex.size();
    }
}
