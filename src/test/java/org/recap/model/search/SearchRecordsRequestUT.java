package org.recap.model.search;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 2/8/16.
 */
public class SearchRecordsRequestUT {

    @Test
    public void testSearchRecordsRequest()throws Exception{
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        setSearchRecordsRequest(searchRecordsRequest);
        assertNotNull(searchRecordsRequest);
        assertEquals("Title1",searchRecordsRequest.getSearchResultRows().get(0).getTitle());
        assertEquals("Available",searchRecordsRequest.getAvailability());
    }

    private void setSearchRecordsRequest(SearchRecordsRequest searchRecordsRequest){
        searchRecordsRequest.setShowResults(true);
        List<SearchResultRow> searchResultRows = new ArrayList<>();
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title1");
        searchResultRow.setBibId(1);
        searchResultRows.add(searchResultRow);
        searchRecordsRequest.setSearchResultRows(searchResultRows);
        List<String> availability = new ArrayList<>();
        availability.add("Available");
        searchRecordsRequest.setAvailability(availability);


    }
}
