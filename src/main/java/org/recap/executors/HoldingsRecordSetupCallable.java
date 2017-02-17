package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.util.HoldingsJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class HoldingsRecordSetupCallable implements Callable {

    HoldingsEntity holdingsEntity;
    ProducerTemplate producerTemplate;

    public HoldingsRecordSetupCallable(HoldingsEntity holdingsEntity, ProducerTemplate producerTemplate) {
        this.holdingsEntity = holdingsEntity;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {
        HoldingsJSONUtil holdingsJSONUtil = new HoldingsJSONUtil();
        holdingsJSONUtil.setProducerTemplate(producerTemplate);
        return holdingsJSONUtil.generateHoldingsForIndex(holdingsEntity);
    }
}
