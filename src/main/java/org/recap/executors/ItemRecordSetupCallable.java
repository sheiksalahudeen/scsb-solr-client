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

    private final HoldingsEntity holdingsEntity;
    private final ItemEntity itemEntity;

    public ItemRecordSetupCallable(ItemEntity itemEntity, HoldingsEntity holdingsEntity) {
        this.itemEntity = itemEntity;
        this.holdingsEntity = holdingsEntity;
    }

    @Override
    public Object call() throws Exception {
        Item item = new ItemJSONUtil().generateItemForIndex(itemEntity, holdingsEntity);
        return item;
    }
}
