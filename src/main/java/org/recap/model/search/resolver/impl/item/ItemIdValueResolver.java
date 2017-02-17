package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by rajeshbabuk on 3/10/16.
 */
public class ItemIdValueResolver implements ItemValueResolver {

    @Override
    public Boolean isInterested(String field) {
        return "ItemId".equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setItemId((Integer) value);
    }
}
