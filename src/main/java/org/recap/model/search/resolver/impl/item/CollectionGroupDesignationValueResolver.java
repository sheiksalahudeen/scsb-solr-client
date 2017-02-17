package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by peris on 9/29/16.
 */
public class CollectionGroupDesignationValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "CollectionGroupDesignation".equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setCollectionGroupDesignation((String)value);
    }
}
