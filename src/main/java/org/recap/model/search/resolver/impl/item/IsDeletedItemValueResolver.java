package org.recap.model.search.resolver.impl.item;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by angelind on 8/11/16.
 */
public class IsDeletedItemValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase(RecapConstants.IS_DELETED_ITEM);
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setDeletedItem((Boolean) value);
    }
}
