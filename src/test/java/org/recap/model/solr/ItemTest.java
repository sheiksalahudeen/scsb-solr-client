package org.recap.model.solr;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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

        Item searchItem = itemCrudRepository.findByBarcode(indexedItem.getBarcode());
        assertNotNull(searchItem);

        System.out.println("id-->"+searchItem.getId());
        Assert.assertEquals(indexedItem.getBarcode(),"1");
        Assert.assertEquals(indexedItem.getItemId(),"301");
        Assert.assertEquals(indexedItem.getDocType(),"Item");
        Assert.assertEquals(indexedItem.getAvailability(),"Available");
        Assert.assertEquals(indexedItem.getCallNumber(),"F864");
        Assert.assertEquals(indexedItem.getCustomerCode(),"PA");
        Assert.assertEquals(indexedItem.getCollectionGroupDesignation(),"Shared");
        Assert.assertEquals(indexedItem.getUseRestriction(),"Use Restriction");
        Assert.assertEquals(indexedItem.getVolumePartYear(),"1970");
        Assert.assertEquals(indexedItem.getSummaryHoldings(),"This item has 2 Holdings");
        Assert.assertTrue(indexedItem.getHoldingsIdList().equals(holdingsIdList));
        Assert.assertTrue(indexedItem.getItemBibIdList().equals(itemBibIdList));
    }
}
