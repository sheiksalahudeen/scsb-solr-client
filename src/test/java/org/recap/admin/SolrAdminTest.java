package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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
        List<String> tempCores = getCoreNames();

        CoreAdminResponse coreAdminResponse = solrAdmin.createSolrCores(tempCores);
        assertNotNull(coreAdminResponse);
        assertTrue(coreAdminResponse.getStatus() == 0);
        System.out.println("Core created");

        unloadCores(tempCores);
    }

    @Test
    public void unloadCores() throws Exception {
        String tempCoreName1 = "temp0";
        String tempCoreName2 = "temp1";
        String tempCoreName3 = "temp2";
        unloadCores(asList(tempCoreName1, tempCoreName2, tempCoreName3));
    }

    private List<String> getCoreNames() {
        String tempCoreName1 = "temp1";
        String tempCoreName2 = "temp2";
        String tempCoreName3 = "temp3";

        return asList(tempCoreName1, tempCoreName2, tempCoreName3);
    }


    private void unloadCores(List<String> tempCores) throws SolrServerException, IOException {
        CoreAdminRequest.Unload coreAdminRequest = solrAdmin.getCoreAdminUnloadRequest();
        for(String tempCoreName : tempCores){
            CoreAdminResponse adminResponse = coreAdminRequest.unloadCore(tempCoreName, true, true, solrAdminClient);
            assertTrue(adminResponse.getStatus() == 0);
        }
    }


}
