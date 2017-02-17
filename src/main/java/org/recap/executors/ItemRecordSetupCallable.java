package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.ItemJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class ItemRecordSetupCallable implements Callable {

    private final ItemEntity itemEntity;
    ProducerTemplate producerTemplate;

    public ItemRecordSetupCallable(ItemEntity itemEntity, ProducerTemplate producerTemplate) {
        this.itemEntity = itemEntity;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {
        ItemJSONUtil itemJSONUtil = new ItemJSONUtil();
        itemJSONUtil.setProducerTemplate(producerTemplate);
        return itemJSONUtil.generateItemForIndex(itemEntity);
    }
}
