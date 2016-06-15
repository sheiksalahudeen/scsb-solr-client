package org.recap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.recap.admin.SolrAdmin;
import org.recap.repository.main.BibCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
public class BaseTestCase {

    @Autowired
    public SolrAdmin solrAdmin;

    @Autowired
    public SolrClient solrAdminClient;

    @Autowired
    public BibCrudRepository bibCrudRepository;

    @Value("${bib.rest.url}")
    public String bibResourceURL;


    @Before
    public void unloadCores() throws Exception {
        CoreAdminRequest coreAdminRequest = solrAdmin.getCoreAdminRequest();

        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        CoreAdminResponse cores = coreAdminRequest.process(solrAdminClient);

        List<String> coreList = new ArrayList<String>();
        for (int i = 0; i < cores.getCoreStatus().size(); i++) {
            String name = cores.getCoreStatus().getName(i);
            if (name.contains("temp")) {
                coreList.add(name);
            }
        }

        for (Iterator<String> iterator = coreList.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            CoreAdminResponse adminResponse = coreAdminRequest.unloadCore(coreName, true, true, solrAdminClient);
            assertTrue(adminResponse.getStatus() == 0);

        }
    }

    @Test
    public void loadContexts() {

    }
}
