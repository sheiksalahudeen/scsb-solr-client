package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

import java.util.List;

/**
 * Created by angelind on 21/11/16.
 */
public class ItemBibIdValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "ItemBibId".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setItemBibIdList((List<Integer>)value);
    }
}
