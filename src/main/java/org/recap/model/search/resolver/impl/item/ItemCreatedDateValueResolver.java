package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

import java.util.Date;

/**
 * Created by rajeshbabuk on 3/10/16.
 */
public class ItemCreatedDateValueResolver implements ItemValueResolver {

    @Override
    public Boolean isInterested(String field) {
        return "ItemCreatedDate".equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setItemCreatedDate((Date) value);
    }
}
