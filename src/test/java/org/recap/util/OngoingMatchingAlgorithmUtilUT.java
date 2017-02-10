package org.recap.util;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by angelind on 6/2/17.
 */
public class OngoingMatchingAlgorithmUtilUT extends BaseTestCase{

    @Autowired
    SolrIndexService solrIndexService;

    @Autowired
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Test
    public void processMatchingForBibTest() {
        SolrInputDocument solrInputDocument = solrIndexService.indexByBibliographicId(1);
        assertNotNull(solrInputDocument);
        ongoingMatchingAlgorithmUtil.processMatchingForBib(solrInputDocument);
    }

}