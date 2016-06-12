package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/12/16.
 */
public class SolrAdminTest extends BaseTestCase {

    @Autowired
    SolrAdmin solrAdmin;

    @Autowired
    SolrClient solrAdminClient;

    @Before
    public void setUp() {
        assertNotNull(solrAdmin);
        assertNotNull(solrAdminClient);
    }

    private void unloadCore(String coreName) throws IOException, SolrServerException {
        solrAdmin.getCoreAdminRequest().unloadCore(coreName, solrAdminClient);
    }

    @Test
    public void createSolrCoresTest() throws Exception {
        String tempCoreName = "temp1";
        CoreAdminResponse coreAdminResponse = solrAdmin.createSolrCore(tempCoreName);
        assertNotNull(coreAdminResponse);
        assertTrue(coreAdminResponse.getStatus() == 0);
        System.out.println("Core created");
        unloadCore(tempCoreName);
    }

}
