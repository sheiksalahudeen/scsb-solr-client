package org.recap.model.solr;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ItemTest extends  BaseTestCase {

    @Before
    public void setUp() throws Exception {
        assertNotNull(itemCrudRepository);
        itemCrudRepository.deleteAll();
    }

    @Test
    public void indexItem() throws Exception {

        List<String> itemBibIdList = new ArrayList<>();
        List<String> holdingsIdList = new ArrayList<>();
        itemBibIdList.add("101");
        itemBibIdList.add("102");
        holdingsIdList.add("201");
        holdingsIdList.add("202");

        Item item = new Item();
        item.setBarcode("1");
        item.setItemId("301");
        item.setDocType("Item");
        item.setAvailability("Available");
        item.setCallNumber("F864");
        item.setCustomerCode("PA");
        item.setCollectionGroupDesignation("Shared");
        item.setUseRestriction("Use Restriction");
        item.setVolumePartYear("1970");
        item.setSummaryHoldings("This item has 2 Holdings");
        item.setHoldingsIdList(holdingsIdList);
        item.setItemBibIdList(itemBibIdList);
        Item indexedItem = itemCrudRepository.save(item);
        assertNotNull(indexedItem);


        assertEquals(indexedItem.getBarcode(),"1");
        assertEquals(indexedItem.getItemId(),"301");
        assertEquals(indexedItem.getDocType(),"Item");
        assertEquals(indexedItem.getAvailability(),"Available");
        assertEquals(indexedItem.getCallNumber(),"F864");
        assertEquals(indexedItem.getCustomerCode(),"PA");
        assertEquals(indexedItem.getCollectionGroupDesignation(),"Shared");
        assertEquals(indexedItem.getUseRestriction(),"Use Restriction");
        assertEquals(indexedItem.getVolumePartYear(),"1970");
        assertEquals(indexedItem.getSummaryHoldings(),"This item has 2 Holdings");
        assertTrue(indexedItem.getHoldingsIdList().equals(holdingsIdList));
        assertTrue(indexedItem.getItemBibIdList().equals(itemBibIdList));
    }
}
