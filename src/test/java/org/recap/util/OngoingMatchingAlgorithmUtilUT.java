package org.recap.util;

import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by angelind on 6/2/17.
 */
public class OngoingMatchingAlgorithmUtilUT extends BaseTestCase{

    @Mock
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Test
    public void processMatchingForBibTest() {
        SolrDocument solrDocument = new SolrDocument();
        Mockito.when(ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument, new ArrayList<>())).thenReturn("Success");
        String status = ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument, new ArrayList<>());
        assertEquals("Success", status);
    }

}