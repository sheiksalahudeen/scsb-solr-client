package org.recap.model.search;

import org.junit.Test;
import org.recap.RecapConstants;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 2/8/16.
 */
public class SearchResultRowUT {

    @Test
    public void testSearchResultRow()throws Exception{
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
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
        buildResults(searchRecordsRequest,bibItems);

        assertNotNull(searchRecordsRequest.getSearchResultRows());
        assertNotNull(searchRecordsRequest.getSearchResultRows().get(0));

        assertNotNull("1",searchRecordsRequest.getSearchResultRows().get(0).getBibId());
        assertNotNull("Title1",searchRecordsRequest.getSearchResultRows().get(0).getTitle());
        assertNotNull("Author1",searchRecordsRequest.getSearchResultRows().get(0).getAuthor());
        assertNotNull("BC234",searchRecordsRequest.getSearchResultRows().get(0).getBarcode());
        assertNotNull("McGraw Hill",searchRecordsRequest.getSearchResultRows().get(0).getPublisher());
        assertNotNull("1998",searchRecordsRequest.getSearchResultRows().get(0).getPublisherDate());
        assertNotNull("PUL",searchRecordsRequest.getSearchResultRows().get(0).getOwningInstitution());
        assertNotNull("1",searchRecordsRequest.getSearchResultRows().get(0).getOwningInstitution());
        assertNotNull("Available",searchRecordsRequest.getSearchResultRows().get(0).getAvailability());
    }

    private void buildResults(SearchRecordsRequest searchRecordsRequest, List<BibItem> bibItems) {
        searchRecordsRequest.setSearchResultRows(null);
        searchRecordsRequest.setShowResults(true);
        if (!CollectionUtils.isEmpty(bibItems)) {
            List<SearchResultRow> searchResultRows = new ArrayList<>();
            for (BibItem bibItem : bibItems) {
                SearchResultRow searchResultRow = new SearchResultRow();
                searchResultRow.setBibId(bibItem.getBibId());
                searchResultRow.setTitle(bibItem.getTitle());
                searchResultRow.setAuthor(bibItem.getAuthorDisplay());
                searchResultRow.setPublisher(bibItem.getPublisher());
                searchResultRow.setPublisherDate(bibItem.getPublicationDate());
                searchResultRow.setOwningInstitution(bibItem.getOwningInstitution());
                searchResultRow.setLeaderMaterialType(bibItem.getLeaderMaterialType());
                if (null != bibItem.getItems() && bibItem.getItems().size() == 1 && !RecapConstants.SERIAL.equals(bibItem.getLeaderMaterialType())) {
                    Item item = bibItem.getItems().get(0);
                    searchResultRow.setCustomerCode(item.getCustomerCode());
                    searchResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                    searchResultRow.setUseRestriction(item.getUseRestriction());
                    searchResultRow.setBarcode(item.getBarcode());
                    searchResultRow.setAvailability(item.getAvailability());
                } else {
                    if (!CollectionUtils.isEmpty(bibItem.getItems())) {
                        List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
                        for (Item item : bibItem.getItems()) {
                            SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
                            searchItemResultRow.setCallNumber(item.getCallNumberSearch());
                            searchItemResultRow.setChronologyAndEnum(item.getVolumePartYear());
                            searchItemResultRow.setCustomerCode(item.getCustomerCode());
                            searchItemResultRow.setBarcode(item.getBarcode());
                            searchItemResultRow.setUseRestriction(item.getUseRestriction());
                            searchItemResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                            searchItemResultRow.setAvailability(item.getAvailability());
                            searchItemResultRows.add(searchItemResultRow);
                        }
                        searchResultRow.setShowItems(true);
                        searchResultRow.setSearchItemResultRows(searchItemResultRows);
                    }
                }
                searchResultRows.add(searchResultRow);
            }
            searchRecordsRequest.setSearchResultRows(searchResultRows);
        }
    }
}
