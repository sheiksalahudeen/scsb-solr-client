package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class BibItemIndexCallable implements Callable {

    private static final Logger logger = LoggerFactory.getLogger(BibItemIndexCallable.class);

    private final int pageNum;
    private final int docsPerPage;
    private String coreName;
    private String solrURL;
    private Integer owningInstitutionId;
    private Date fromDate;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private HoldingsDetailsRepository holdingsDetailsRepository;
    private ProducerTemplate producerTemplate;
    private SolrTemplate solrTemplate;

    public BibItemIndexCallable(String solrURL, String coreName, int pageNum, int docsPerPage, BibliographicDetailsRepository bibliographicDetailsRepository, HoldingsDetailsRepository holdingsDetailsRepository, Integer owningInstitutionId, Date fromDate, ProducerTemplate producerTemplate, SolrTemplate solrTemplate) {
        this.coreName = coreName;
        this.solrURL = solrURL;
        this.pageNum = pageNum;
        this.docsPerPage = docsPerPage;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
        this.owningInstitutionId = owningInstitutionId;
        this.fromDate = fromDate;
        this.producerTemplate = producerTemplate;
        this.solrTemplate = solrTemplate;
    }

    @Override
    public Object call() throws Exception {

        Page<BibliographicEntity> bibliographicEntities = null;
        if (null == owningInstitutionId && null == fromDate) {
            bibliographicEntities = bibliographicDetailsRepository.findAll(new PageRequest(pageNum, docsPerPage));
        } else if (null != owningInstitutionId && null == fromDate) {
            bibliographicEntities = bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(pageNum, docsPerPage), owningInstitutionId);
        } else if (null == owningInstitutionId && null != fromDate) {
            bibliographicEntities = bibliographicDetailsRepository.findByLastUpdatedDateAfter(new PageRequest(pageNum, docsPerPage), fromDate);
        } else if (null != owningInstitutionId && null != fromDate) {
            bibliographicEntities = bibliographicDetailsRepository.findByOwningInstitutionIdAndLastUpdatedDateAfter(new PageRequest(pageNum, docsPerPage), owningInstitutionId, fromDate);
        }

        logger.info("Num Bibs Fetched : " + bibliographicEntities.getNumberOfElements());
        Iterator<BibliographicEntity> iterator = bibliographicEntities.iterator();


        ExecutorService executorService = Executors.newFixedThreadPool(50);
        List<Future> futures = new ArrayList<>();
        while (iterator.hasNext()) {
            BibliographicEntity bibliographicEntity = iterator.next();
            Future submit = executorService.submit(new BibItemRecordSetupCallable(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository, producerTemplate));
            futures.add(submit);
        }

        logger.info("Num futures to prepare Bib and Associated data : ",futures.size());

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
            SolrTemplate solrTemplate = new SolrTemplate(new HttpSolrClient(solrURL + File.separator + coreName));
            solrTemplate.setSolrCore(coreName);
            solrTemplate.saveDocuments(solrInputDocumentsToIndex);
            solrTemplate.commit();
        }
        return solrInputDocumentsToIndex.size();
    }
}
