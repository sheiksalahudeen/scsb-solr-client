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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class ExecutorTest extends BaseTestCase {

    @Autowired
    CoreAdminExecutorService coreAdminExecutorService;
    private int numThreads = 3;

    @Test
    public void indexMultipleBibsWithThreads() throws Exception {

        try {
            bibCrudRepository.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bib bib1 = new Bib();
        bib1.setBarcode("1");
        Bib bib2 = new Bib();
        bib2.setBarcode("2");
        Bib bib3 = new Bib();
        bib3.setBarcode("3");

        List<Bib> bibList = asList(bib1, bib2, bib3);

        coreAdminExecutorService.indexBibs(numThreads, 1, bibList);

        Bib searchBib1 = bibCrudRepository.findByBarcode(bib1.getBarcode());
        assertNotNull(searchBib1);


        Bib searchBib2 = bibCrudRepository.findByBarcode(bib2.getBarcode());
        assertNotNull(searchBib2);


        Bib searchBib3 = bibCrudRepository.findByBarcode(bib3.getBarcode());
        assertNotNull(searchBib3);
    }

}
