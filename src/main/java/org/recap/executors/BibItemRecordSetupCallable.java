package org.recap.executors;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.BibJSONUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class BibItemRecordSetupCallable implements Callable {

    private final List<HoldingsEntity> holdingsEntities;
    private final List<ItemEntity> itemEntities;
    BibliographicEntity bibliographicEntity;

    public BibItemRecordSetupCallable(BibliographicEntity bibliographicEntity, List<HoldingsEntity> holdingsEntities, List<ItemEntity> itemEntities) {
        this.bibliographicEntity = bibliographicEntity;
        this.holdingsEntities = holdingsEntities;
        this.itemEntities = itemEntities;
    }

    @Override
    public Object call() throws Exception {
        Map<String, List> stringListMap = new BibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity, holdingsEntities, itemEntities);
        return stringListMap ;
    }
}
