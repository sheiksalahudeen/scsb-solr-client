package org.recap.model.solr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Ignore
public class ItemAT extends  BaseTestCase {

    @Before
    public void setUp() throws Exception {
        assertNotNull(itemCrudRepository);
        itemCrudRepository.deleteAll();
    }

    @Test
    public void indexItem() throws Exception {

        List<Integer> itemBibIdList = new ArrayList<>();
        List<Integer> holdingsIdList = new ArrayList<>();
        itemBibIdList.add(101);
        itemBibIdList.add(102);
        holdingsIdList.add(201);
        holdingsIdList.add(202);

        Item item = new Item();
        item.setBarcode("1");
        item.setItemId(301);
        item.setDocType("Item");
        item.setAvailability("Available");
        item.setCallNumberSearch("F864");
        item.setCustomerCode("PA");
        item.setCollectionGroupDesignation("Shared");
        item.setUseRestriction("Use Restriction");
        item.setVolumePartYear("1970");
        item.setHoldingsIdList(holdingsIdList);
        item.setItemBibIdList(itemBibIdList);
        Item indexedItem = itemCrudRepository.save(item);
        assertNotNull(indexedItem);


        assertEquals(indexedItem.getBarcode(),"1");
        assertEquals(indexedItem.getItemId(),new Integer(301));
        assertEquals(indexedItem.getDocType(),"Item");
        assertEquals(indexedItem.getAvailability(),"Available");
        assertEquals(indexedItem.getCallNumberSearch(),"F864");
        assertEquals(indexedItem.getCustomerCode(),"PA");
        assertEquals(indexedItem.getCollectionGroupDesignation(),"Shared");
        assertEquals(indexedItem.getUseRestriction(),"Use Restriction");
        assertEquals(indexedItem.getVolumePartYear(),"1970");
        assertTrue(indexedItem.getHoldingsIdList().equals(holdingsIdList));
        assertTrue(indexedItem.getItemBibIdList().equals(itemBibIdList));
    }
}
