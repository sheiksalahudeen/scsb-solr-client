package org.recap;

import org.junit.Before;
import org.junit.Test;
import org.recap.model.solr.Item;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by angelind on 15/6/16.
 */
public class ItemIndexerTest extends  BaseTestCase {

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
    }
}
