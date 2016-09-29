package org.recap.camel.route;

import org.apache.camel.ProducerTemplate;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.*;

/**
 * Created by rajeshbabuk on 29/9/16.
 */
public class SolrRouteBuilderAT extends BaseTestCase {

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${solr.parent.core}")
    String solrCore;

    @Test
    public void testSolrRouteBuilder() throws Exception {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.setField("id", "123");
        solrInputDocument.setField("Title_search", "Title1");
        solrInputDocument.setField("Author_search", "Author1");

        producerTemplate.sendBodyAndHeader(RecapConstants.SOLR_QUEUE, solrInputDocument, RecapConstants.SOLR_CORE, solrCore);
        Thread.sleep(2000);
    }

}