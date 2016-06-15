package org.recap.executors;

import org.recap.model.Item;
import org.recap.repository.temp.ItemCrudRepositoryMultiCoreSupport;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 15/6/16.
 */
public class ItemIndexCallable implements Callable {

    private String coreName;
    private String solrURL;
    private List<Item> items;

    private ItemCrudRepositoryMultiCoreSupport itemCrudRepositoryMultiCoreSupport;

    public ItemIndexCallable(String solrURL, String coreName, List<Item> items) {
        this.coreName = coreName;
        this.items = items;
        this.solrURL = solrURL;
    }

    @Override
    public Object call() throws Exception {
        itemCrudRepositoryMultiCoreSupport = new ItemCrudRepositoryMultiCoreSupport(coreName, solrURL);
        itemCrudRepositoryMultiCoreSupport.save(items);
        return null;
    }
}
