package org.recap.util;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by angelind on 6/2/17.
 */
public class OngoingMatchingAlgorithmUtilUT extends BaseTestCase{

    @Mock
    SolrIndexService solrIndexService;

    @Autowired
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Test
    public void processMatchingForBibTest() {
        Mockito.when(solrIndexService.indexByBibliographicId(1)).thenReturn(new SolrInputDocument());
        SolrInputDocument solrInputDocument = solrIndexService.indexByBibliographicId(1);
        assertNotNull(solrInputDocument);
        ongoingMatchingAlgorithmUtil.processMatchingForBib(solrInputDocument);
    }

}