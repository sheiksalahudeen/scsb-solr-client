package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

import java.util.Date;

/**
 * Created by peris on 9/29/16.
 */
public class ItemLastUpdatedDateValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "ItemLastUpdatedDate".equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setItemLastUpdatedDate((Date) value);
    }
}
