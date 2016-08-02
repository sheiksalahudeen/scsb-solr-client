package org.recap.model.search;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 2/8/16.
 */
public class SearchItemResultRowUT {

    @Test
    public void testSearchItemResultRow(){
        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        setSearchItemResultRow(searchItemResultRow);
        assertNotNull(searchItemResultRow);
        assertEquals("Available",searchItemResultRow.getAvailability());
        assertEquals("BC123",searchItemResultRow.getBarcode());
        assertEquals("CL425",searchItemResultRow.getCallNumber());
        assertEquals("CE",searchItemResultRow.getChronologyAndEnum());
        assertEquals("CG",searchItemResultRow.getCollectionGroupDesignation());
        assertEquals("NA",searchItemResultRow.getCustomerCode());
        assertEquals("Allowed",searchItemResultRow.getUseRestriction());
    }

    private void setSearchItemResultRow(SearchItemResultRow searchItemResultRow){
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setBarcode("BC123");
        searchItemResultRow.setCallNumber("CL425");
        searchItemResultRow.setChronologyAndEnum("CE");
        searchItemResultRow.setCollectionGroupDesignation("CG");
        searchItemResultRow.setCustomerCode("NA");
        searchItemResultRow.setUseRestriction("Allowed");
    }
}
