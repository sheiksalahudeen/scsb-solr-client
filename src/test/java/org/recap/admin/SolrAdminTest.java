package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/12/16.
 */
public class SolrAdminTest extends BaseTestCase {
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
}
