package org.recap.model.search.resolver.impl.item;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

import java.util.List;

/**
 * Created by rajeshbabuk on 4/7/17.
 */
public class HoldingsIdsValueResolver implements ItemValueResolver {

    /**
     * Returns true if field name is 'HoldingsId'.
     *
     * @param field the field
     * @return
     */
    @Override
    public Boolean isInterested(String field) {
        return RecapConstants.HOLDINGS_ID.equalsIgnoreCase(field);
    }

    /**
     * Set holdings id value to the item
     *
     * @param item  the item
     * @param value the value
     */
    @Override
    public void setValue(Item item, Object value) {
        item.setHoldingsIdList((List<Integer>)value);
    }
}
