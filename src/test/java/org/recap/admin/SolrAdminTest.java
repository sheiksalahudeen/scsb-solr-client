package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
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

    @Test
    public void createSolrCoresTest() throws Exception {
        String tempCoreName1 = "temp1";
        String tempCoreName2 = "temp2";
        String tempCoreName3 = "temp3";

        List<String> tempCores = asList(tempCoreName1, tempCoreName2, tempCoreName3);

        CoreAdminResponse coreAdminResponse = solrAdmin.createSolrCore(tempCores);
        assertNotNull(coreAdminResponse);
        assertTrue(coreAdminResponse.getStatus() == 0);
        System.out.println("Core created");

        CoreAdminRequest.Unload coreAdminRequest = solrAdmin.getCoreAdminUnloadRequest();
        for(String tempCoreName : tempCores){
            coreAdminRequest.unloadCore(tempCoreName, true, true, solrAdminClient);
        }

    }

}
