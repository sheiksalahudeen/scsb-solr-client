package org.recap.executors;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.util.BibJSONUtil;
import org.recap.util.ItemJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class ItemRecordSetupCallable implements Callable {

    private final ItemEntity itemEntity;

    public ItemRecordSetupCallable(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public Object call() throws Exception {
        Item item = new ItemJSONUtil().generateItemForIndex(itemEntity);
        return item;
    }
}
