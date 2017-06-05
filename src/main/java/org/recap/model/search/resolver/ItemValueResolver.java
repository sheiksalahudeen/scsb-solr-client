package org.recap.model.search.resolver;

import org.recap.model.solr.Item;

/**
 * Created by peris on 9/29/16.
 */
public interface ItemValueResolver extends ValueResolver {
    /**
     * Gets value from the object value and sets to item.
     *
     * @param item  the item
     * @param value the value
     */
    public void setValue(Item item, Object value);
}
