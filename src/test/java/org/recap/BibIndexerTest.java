package org.recap;

import org.junit.Before;
import org.junit.Test;
import org.recap.model.solr.Bib;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class BibIndexerTest extends BaseTestCase {

    @Before
    public void setUp() throws Exception {
        assertNotNull(bibCrudRepository);
        bibCrudRepository.deleteAll();
    }

    @Test
    public void indexBib() throws Exception {

        List<String> issnList = new ArrayList<>();
        List<String>isbnList = new ArrayList<>();
        List<String> oclcNumberList = new ArrayList<>();
        List<String> holdingsIdList = new ArrayList<>();
        List<String> itemIdList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");
        holdingsIdList.add("201");
        holdingsIdList.add("202");
        itemIdList.add("301");
        itemIdList.add("302");

        Bib bib = new Bib();
        bib.setId("1");
        bib.setBibId("101");
        bib.setDocType("Bibliographic");
        bib.setTitle("Middleware for ReCAP");
        bib.setBarcode("1");
        bib.setTitle("Test Bib 1");
        bib.setAuthor("Hoepli, Nancy L");
        bib.setPublisher("McClelland & Stewart, limited");
        bib.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib.setIssn(issnList);
        bib.setIsbn(isbnList);
        bib.setOclcNumber(oclcNumberList);
        bib.setPublicationDate("1960");
        bib.setMaterialType("Material Type 1");
        bib.setNotes("Bibliographical footnotes 1");
        bib.setOwningInstitution("PUL");
        bib.setSubject("Arab countries Politics and government.");
        bib.setPublicationPlace("Paris");
        bib.setLccn("71448228");
        bib.setHoldingsIdList(holdingsIdList);
        bib.setBibItemIdList(itemIdList);
        Bib indexedBib = bibCrudRepository.save(bib);

        assertNotNull(indexedBib);

        Bib searchBib = bibCrudRepository.findByBarcode(indexedBib.getBarcode());
        assertNotNull(searchBib);

    }



}
