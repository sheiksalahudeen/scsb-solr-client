package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(BibIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private String coreName;
    private Integer owningInstitutionId;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private HoldingsDetailsRepository holdingsDetailsRepository;
    private ProducerTemplate producerTemplate;

    public BibIndexCallable(String coreName, int pageNum, int docsPerPage, BibliographicDetailsRepository bibliographicDetailsRepository, HoldingsDetailsRepository holdingsDetailsRepository, Integer owningInstitutionId, ProducerTemplate producerTemplate) {
        this.coreName = coreName;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {

        Page<BibliographicEntity> bibliographicEntities = owningInstitutionId == null ?
                bibliographicDetailsRepository.findAllByIsDeletedFalse(new PageRequest(pageNum, docsPerPage)) :
                bibliographicDetailsRepository.findByOwningInstitutionIdAndIsDeletedFalse(new PageRequest(pageNum, docsPerPage), owningInstitutionId);

        logger.info("Num Bibs Fetched : " + bibliographicEntities.getNumberOfElements());
        List<Bib> bibsToIndex = new ArrayList<>();

        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while(iterator.hasNext()){
            BibliographicEntity bibliographicEntity = iterator.next();
            Future submit = executorService.submit(new BibRecordSetupCallable(bibliographicEntity, bibliographicDetailsRepository, holdingsDetailsRepository, producerTemplate));
            futures.add(submit);
        }

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            try {
                Future future = futureIterator.next();
                Bib bib = (Bib) future.get();
                if(bib != null)
                    bibsToIndex.add(bib);
            } catch (Exception e) {
                logger.error(RecapConstants.LOG_ERROR,e);
            }
        }

        executorService.shutdown();

        logger.info("No of Bibs to index : {}",bibsToIndex.size());

        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            producerTemplate.sendBodyAndHeader(RecapConstants.SOLR_QUEUE, bibsToIndex, RecapConstants.SOLR_CORE, coreName);
        }
        return bibsToIndex.size();
    }
}
