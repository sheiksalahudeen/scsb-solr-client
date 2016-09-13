package org.recap.executors;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.solr.Holdings;
import org.recap.util.HoldingsJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class HoldingsRecordSetupCallable implements Callable {

    HoldingsEntity holdingsEntity;

    public HoldingsRecordSetupCallable(HoldingsEntity holdingsEntity) {
        this.holdingsEntity = holdingsEntity;
    }

    @Override
    public Object call() throws Exception {
        Holdings holdings = new HoldingsJSONUtil().generateHoldingsForIndex(holdingsEntity);
        return holdings ;
    }
}
