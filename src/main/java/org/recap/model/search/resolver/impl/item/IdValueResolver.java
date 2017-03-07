package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by rajeshbabuk on 3/10/16.
 */
public class IdValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "id".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setId((String) value);
    }
}
