package org.recap.model.solr;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 8/6/17.
 */
public class BibItemUT extends BaseTestCase{

    @Test
    public void testBibItem(){
        BibItem bibItem = new BibItem();
        bibItem.setId("1");
        bibItem.setBibId(1);
        bibItem.setTitle("Title1");
        bibItem.setTitleDisplay("test");
        bibItem.setTitleSubFieldA("Title");
        bibItem.setAuthorDisplay("Author1");
        bibItem.setAuthorSearch(Arrays.asList("test"));
        bibItem.setBarcode("BC234");
        bibItem.setDocType("Bib");
        bibItem.setImprint("sample imprint");
        List<String> isbnList = new ArrayList<>();
        isbnList.add("978-3-16-148410-0");
        bibItem.setIsbn(isbnList);
        bibItem.setLccn("sample lccn");
        bibItem.setPublicationPlace("Texas");
        bibItem.setPublisher("McGraw Hill");
        bibItem.setPublicationDate("1998");
        bibItem.setSubject("Physics");
        bibItem.setNotes("Notes");
        bibItem.setOwningInstitution("PUL");
        bibItem.setOwningInstitutionBibId("1");

        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setBarcode("BC234");
        item.setCallNumberSearch("123");
        item.setVolumePartYear("V1");
        item.setCustomerCode("NA");
        item.setAvailability("Available");
        items.add(item);
        bibItem.setItems(items);
    }

}