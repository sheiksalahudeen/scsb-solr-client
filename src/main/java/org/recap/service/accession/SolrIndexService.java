package org.recap.service.accession;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Created by rajeshbabuk on 10/11/16.
 */
@Service
public class SolrIndexService {

    Logger logger = Logger.getLogger(SolrIndexService.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    public SolrInputDocument indexByBibliographicId(@RequestBody Integer bibliographicId) {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        bibJSONUtil.setProducerTemplate(producerTemplate);
        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibliographicId);
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
        if (solrInputDocument !=null) {
            solrTemplate.saveDocument(solrInputDocument);
            solrTemplate.commit();
        }
        return solrInputDocument;
    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }
}
