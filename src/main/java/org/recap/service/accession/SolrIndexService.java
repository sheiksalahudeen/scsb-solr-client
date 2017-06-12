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
    private ProducerTemplate producerTemplate;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    private SolrClient solrClient;

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets ProducerTemplate object.
     *
     * @return the ProducerTemplate object.
     */
    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    /**
     * Gets SolrTemplate object.
     *
     * @return the SolrTemplate object.
     */
    public SolrTemplate getSolrTemplate() {
        return solrTemplate;
    }

    /**
     * Gets BibliographicDetailsRepository object.
     *
     * @return the BibliographicDetailsRepository object.
     */
    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return bibliographicDetailsRepository;
    }

    /**
     * Gets HoldingsDetailsRepository object.
     *
     * @return the HoldingsDetailsRepository object.
     */
    public HoldingsDetailsRepository getHoldingsDetailsRepository() {
        return holdingsDetailsRepository;
    }

    /**
     * Gets BibJSONUtil object.
     *
     * @return the BibJSONUtil object.
     */
    public BibJSONUtil getBibJSONUtil(){
        return new BibJSONUtil();
    }

    /**
     * This method is used to index by bibliographic id in solr.
     *
     * @param bibliographicId the bibliographic id
     * @return the solr input document
     */
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

    /**
     * This method is used to delete by doc id in solr.
     *
     * @param docIdParam the doc id param
     * @param docIdValue the doc id value
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }
}
