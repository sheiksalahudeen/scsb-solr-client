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
import org.recap.model.Item;
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
        List<String> holdingsIdList = new ArrayList<>();
        List<String> itemIdList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");
        holdingsIdList.add("201");
        holdingsIdList.add("202");
        itemIdList.add("203");
        itemIdList.add("204");

        Bib bib1 = new Bib();
        bib1.setId("1");
        bib1.setBibId("101");
        bib1.setDocType("Bibliographic");
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
        bib1.setHoldingsIdList(holdingsIdList);
        bib1.setBibItemIdList(itemIdList);

        Bib bib2 = new Bib();
        bib2.setId("2");
        bib2.setBibId("102");
        bib2.setDocType("Bibliographic");
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
        bib2.setHoldingsIdList(holdingsIdList);
        bib2.setBibItemIdList(itemIdList);

        Bib bib3 = new Bib();
        bib3.setId("1");
        bib3.setBibId("103");
        bib3.setDocType("Bibliographic");
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
        bib3.setHoldingsIdList(holdingsIdList);
        bib3.setBibItemIdList(itemIdList);

        List<Bib> bibList = asList(bib1, bib2, bib3);

        coreAdminExecutorService.indexBibs(numThreads, 1, bibList);

        Bib searchBib1 = bibCrudRepository.findByBarcode(bib1.getBarcode());
        assertNotNull(searchBib1);


        Bib searchBib2 = bibCrudRepository.findByBarcode(bib2.getBarcode());
        assertNotNull(searchBib2);


        Bib searchBib3 = bibCrudRepository.findByBarcode(bib3.getBarcode());
        assertNotNull(searchBib3);
    }

    @Test
    public void indexMultipleItemsWithThreads() throws Exception {

        try {
            itemCrudRepository.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Integer> itemBibIdList = new ArrayList<>();
        List<Integer> holdingsIdList = new ArrayList<>();
        itemBibIdList.add(101);
        itemBibIdList.add(102);
        holdingsIdList.add(201);
        holdingsIdList.add(202);

        Item item1 = new Item();
        item1.setId("1");
        item1.setBarcode("1");
        item1.setItemId(301);
        item1.setDocType("Item");
        item1.setAvailability("Available");
        item1.setCallNumber("F864");
        item1.setCustomerCode("PA");
        item1.setCollectionGroupDesignation("Shared");
        item1.setUseRestriction("Use Restriction");
        item1.setVolumePartYear("1970");
        item1.setSummaryHoldings("This item has 2 Holdings");
        item1.setHoldingsIdList(holdingsIdList);
        item1.setItemBibIdList(itemBibIdList);

        Item item2 = new Item();
        item2.setId("2");
        item2.setBarcode("2");
        item2.setItemId(302);
        item2.setDocType("Item");
        item2.setAvailability("Available");
        item2.setCallNumber("F865");
        item2.setCustomerCode("PA");
        item2.setCollectionGroupDesignation("Private");
        item2.setUseRestriction("Use Restriction");
        item2.setVolumePartYear("1980");
        item2.setSummaryHoldings("This item has 2 Holdings");
        item2.setHoldingsIdList(holdingsIdList);
        item2.setItemBibIdList(itemBibIdList);

        Item item3 = new Item();
        item3.setId("3");
        item3.setBarcode("3");
        item3.setItemId(303);
        item3.setDocType("Item");
        item3.setAvailability("Available");
        item3.setCallNumber("F866");
        item3.setCustomerCode("PA");
        item3.setCollectionGroupDesignation("Shared");
        item3.setUseRestriction("Use Restriction");
        item3.setVolumePartYear("1980");
        item3.setSummaryHoldings("This item has 2 Holdings");
        item3.setHoldingsIdList(holdingsIdList);
        item3.setItemBibIdList(itemBibIdList);

        List<Item> itemList = asList(item1, item2, item3);

        coreAdminExecutorService.indexItems(numThreads, 1, itemList);

        Item searchItem1 = itemCrudRepository.findByBarcode(item1.getBarcode());
        assertNotNull(searchItem1);


        Item searchItem2 = itemCrudRepository.findByBarcode(item2.getBarcode());
        assertNotNull(searchItem2);


        Item searchItem3 = itemCrudRepository.findByBarcode(item3.getBarcode());
        assertNotNull(searchItem3);

    }

}
