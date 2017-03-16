package org.recap.admin;

import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/12/16.
 */
@Ignore
public class SolrAdminAT extends BaseTestCase {

    @Value("${solr.parent.core}")
    String solrCore;

    String tempCoreName1 = "temp0";
    String tempCoreName2 = "temp1";
    String tempCoreName3 = "temp2";

    @Test
    public void createSolrCoresTest() throws Exception {
        List<String> tempCores = asList(tempCoreName1, tempCoreName2, tempCoreName3);

        CoreAdminResponse coreAdminResponse = solrAdmin.createSolrCores(tempCores);
        assertNotNull(coreAdminResponse);
        assertTrue(coreAdminResponse.getStatus() == 0);
        System.out.println("Core created");

    }

    @Test
    public void unloadTempCores() throws Exception {
        solrAdmin.unloadTempCores();

        CoreAdminRequest coreAdminRequest = solrAdmin.getCoreAdminRequest();

        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        CoreAdminResponse cores = coreAdminRequest.process(solrAdminClient);

        System.out.println(cores.getStatus());

    }

    @Test
    public void testIfCoreExist() throws Exception {
        boolean isCoreExists = solrAdmin.isCoreExist(solrCore);
        assertTrue(isCoreExists);
    }
}
