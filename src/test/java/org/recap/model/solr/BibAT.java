package org.recap.model.solr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore
public class BibAT extends BaseTestCase {

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
        List<Integer> holdingsIdList = new ArrayList<>();
        List<Integer> itemIdList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");
        holdingsIdList.add(201);
        holdingsIdList.add(202);
        itemIdList.add(301);
        itemIdList.add(302);

        Bib bib = new Bib();
        bib.setBibId(101);
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

        assertTrue(indexedBib.getIssn().get(0).equals("0394469756"));
        assertTrue(indexedBib.getIsbn().get(0).equals("0394469755"));
        assertTrue(indexedBib.getOclcNumber().get(0).equals("00133182"));
        assertTrue(indexedBib.getHoldingsIdList().equals(holdingsIdList));
        assertTrue(indexedBib.getBibItemIdList().equals(itemIdList));
        assertEquals(indexedBib.getBibId(),new Integer(101));
        assertEquals(indexedBib.getDocType(),"Bibliographic");
        assertEquals(indexedBib.getTitle(),"Test Bib 1");
        assertEquals(indexedBib.getBarcode(),"1");
        assertEquals(indexedBib.getTitle(),"Test Bib 1");
        assertEquals(indexedBib.getAuthor(),"Hoepli, Nancy L");
        assertEquals(indexedBib.getPublisher(),"McClelland & Stewart, limited");
        assertEquals(indexedBib.getImprint(),"Toronto, McClelland & Stewart, limited [c1926]");
        assertEquals(indexedBib.getPublicationDate(),"1960");
        assertEquals(indexedBib.getMaterialType(),"Material Type 1");
        assertEquals(indexedBib.getNotes(),"Bibliographical footnotes 1");
        assertEquals(indexedBib.getOwningInstitution(),"PUL");
        assertEquals(indexedBib.getSubject(),"Arab countries Politics and government.");
        assertEquals(indexedBib.getPublicationPlace(),"Paris");
        assertEquals(indexedBib.getLccn(),"71448228");
    }


}
