package org.recap.executors;

import org.recap.model.BibliographicEntity;
import org.recap.model.BibliographicHoldingsEntity;
import org.recap.model.HoldingsEntity;
import org.recap.model.ItemEntity;
import org.recap.util.BibJSONUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/17/16.
 */
public class BibRecordSetupCallable implements Callable {

    private final List<BibliographicHoldingsEntity> bibliographicHoldingsEntities;
    private final List<ItemEntity> itemEntities;
    BibliographicEntity bibliographicEntity;

    public BibRecordSetupCallable(BibliographicEntity bibliographicEntity, List<BibliographicHoldingsEntity> bibliographicHoldingsEntities, List<ItemEntity> itemEntities) {
        this.bibliographicEntity = bibliographicEntity;
        this.bibliographicHoldingsEntities = bibliographicHoldingsEntities;
        this.itemEntities = itemEntities;
    }

    @Override
    public Object call() throws Exception {
//        Map<String, List> stringListMap = new BibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity, bibliographicHoldingsEntities, itemEntities);
//        return stringListMap ;

        return null;
    }
}
