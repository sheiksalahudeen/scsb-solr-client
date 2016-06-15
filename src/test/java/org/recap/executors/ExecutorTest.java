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

        List<String> issnList = new ArrayList<>();
        List<String>isbnList = new ArrayList<>();
        List<String> oclcNumberList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");

        Bib bib1 = new Bib();
        bib1.setId("101");
        bib1.setBarcode("1");
        bib1.setTitle("Test Bib 1");
        bib1.setAuthor("Hoepli, Nancy L");
        bib1.setPublisher("McClelland & Stewart, limited");
        bib1.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib1.setIssn(issnList);
        bib1.setIsbn(isbnList);
        bib1.setOclcNumber(oclcNumberList);
        bib1.setPublicationDate("1960");
        bib1.setMaterialType("Material Type 1");
        bib1.setNotes("Bibliographical footnotes 1");
        bib1.setOwningInstitution("PUL");
        bib1.setSubject("Arab countries Politics and government.");
        bib1.setPublicationPlace("Paris");
        bib1.setLccn("71448228");

        Bib bib2 = new Bib();
        bib2.setId("102");
        bib2.setBarcode("2");
        bib2.setTitle("Test Bib 2");
        bib2.setAuthor("Riddell, William Renwick");
        bib2.setPublisher("Citadel Press");
        bib2.setImprint("New York, Citadel Press [1968]");
        bib2.setIssn(issnList);
        bib2.setIsbn(isbnList);
        bib2.setOclcNumber(oclcNumberList);
        bib2.setPublicationDate("1968");
        bib2.setMaterialType("Material Type 2");
        bib2.setNotes("Bibliographical footnotes 2");
        bib2.setOwningInstitution("PUL");
        bib2.setSubject("Liberia Social life and customs.");
        bib2.setPublicationPlace("New York");
        bib2.setLccn("73596670");

        Bib bib3 = new Bib();
        bib3.setId("103");
        bib3.setBarcode("3");
        bib3.setTitle("Test Bib 3");
        bib3.setAuthor("Gale, John M");
        bib3.setPublisher("Random House");
        bib3.setImprint("New York, Random House [1971, c1970]");
        bib3.setIssn(issnList);
        bib3.setIsbn(isbnList);
        bib3.setOclcNumber(oclcNumberList);
        bib3.setPublicationDate("1971");
        bib3.setMaterialType("Material Type 3");
        bib3.setNotes("Bibliographical footnotes 3");
        bib3.setOwningInstitution("PUL");
        bib3.setSubject("Caucasus, South Fiction. Love stories. gsafd");
        bib3.setPublicationPlace("Paris");
        bib3.setLccn("68019124");

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
