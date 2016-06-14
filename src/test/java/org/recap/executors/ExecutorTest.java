package org.recap.executors;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.junit.After;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.admin.SolrAdmin;
import org.recap.model.Bib;
import org.recap.repository.main.BibCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class ExecutorTest extends BaseTestCase {

    @Autowired
    CoreAdminExecutorService coreAdminExecutorService;
    private int numThreads;

    @Autowired
    SolrAdmin solrAdmin;

    @Autowired
    SolrClient solrAdminClient;

    @Autowired
    BibCrudRepository bibCrudRepository;

    @Test
    public void indexMultipleBibsWithThreads() {

        try {
            bibCrudRepository.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bib bib1 = new Bib();
        bib1.setId(1L);
        Bib bib2 = new Bib();
        bib2.setId(2L);
        Bib bib3 = new Bib();
        bib3.setId(3L);

        List<Bib> bibList = asList(bib1, bib2, bib3);

        numThreads = 3;
        coreAdminExecutorService.indexBibs(numThreads, 1, bibList);

    }


    @After
    public void unloadTempCores() throws Exception {
        List<String> coreNames = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            coreNames.add("temp"+i);
        }

        unloadCores(coreNames);
    }

    private void unloadCores(List<String> tempCores) throws SolrServerException, IOException {
        CoreAdminRequest.Unload coreAdminRequest = solrAdmin.getCoreAdminUnloadRequest();
        for(String tempCoreName : tempCores){
            CoreAdminResponse adminResponse = coreAdminRequest.unloadCore(tempCoreName, true, true, solrAdminClient);
            assertTrue(adminResponse.getStatus() == 0);
        }
    }


}
