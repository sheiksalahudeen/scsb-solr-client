package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.search.DataDumpSearchResult;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/2/17.
 */
public class SearchRecordsUtilUT extends BaseTestCase{

    @Autowired
    SearchRecordsUtil searchRecordsUtil;

    @Test
    public void testBuildResultsForDataDump(){
        List<DataDumpSearchResult> dataDumpSearchResults = searchRecordsUtil.buildResultsForDataDump(getBibItemList());
        assertNotNull(dataDumpSearchResults);
        assertNotNull(dataDumpSearchResults.get(0).getBibId());
        assertNotNull(dataDumpSearchResults.get(0).getItemIds());
    }

    public List<BibItem> getBibItemList(){
        List<BibItem > bibItems = new ArrayList<>();
        BibItem bibItem = new BibItem();
        bibItem.setBibId(1);
        bibItem.setTitle("Title1");
        bibItem.setAuthorDisplay("Author1");
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
        bibItems.add(bibItem);
        return bibItems;
    }

}