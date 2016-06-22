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

    BibliographicEntity bibliographicEntity;

    public BibItemRecordSetupCallable(BibliographicEntity bibliographicEntity) {
        this.bibliographicEntity = bibliographicEntity;
    }

    @Override
    public Object call() throws Exception {
        Map<String, List> stringListMap = new BibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity);
        return stringListMap ;
    }
}
