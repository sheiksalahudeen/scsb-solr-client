package org.recap.service.accession;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Created by rajeshbabuk on 10/11/16.
 */
@Service
public class SolrIndexService {

    private static final Logger logger = LoggerFactory.getLogger(SolrIndexService.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    SolrClient solrClient;

    public Logger getLogger() {
        return logger;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public SolrTemplate getSolrTemplate() {
        return solrTemplate;
    }

    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return bibliographicDetailsRepository;
    }

    public HoldingsDetailsRepository getHoldingsDetailsRepository() {
        return holdingsDetailsRepository;
    }

    public BibJSONUtil getBibJSONUtil(){
        return new BibJSONUtil();
    }

    public SolrInputDocument indexByBibliographicId(@RequestBody Integer bibliographicId) {
        getBibJSONUtil().setProducerTemplate(getProducerTemplate());
        BibliographicEntity bibliographicEntity = getBibliographicDetailsRepository().findByBibliographicId(bibliographicId);
        SolrInputDocument solrInputDocument = getBibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity, getSolrTemplate(), getBibliographicDetailsRepository(), getHoldingsDetailsRepository());
        if (solrInputDocument !=null) {
            StopWatch stopWatchIndexDocument = new StopWatch();
            stopWatchIndexDocument.start();
            getSolrTemplate().saveDocument(solrInputDocument,1);
            stopWatchIndexDocument.stop();
            logger.info("Time taken to index the doc--->{}sec",stopWatchIndexDocument.getTotalTimeSeconds());
        }
        return solrInputDocument;
    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }
}
