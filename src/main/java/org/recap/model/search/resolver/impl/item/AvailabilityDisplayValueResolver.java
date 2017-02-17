package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by angelind on 4/10/16.
 */
public class AvailabilityDisplayValueResolver implements ItemValueResolver {

    @Override
    public Boolean isInterested(String field) {
        return "Availability_display".equals(field);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setAvailabilityDisplay((String) value);
    }
}
