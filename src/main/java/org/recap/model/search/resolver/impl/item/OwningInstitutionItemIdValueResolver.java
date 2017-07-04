package org.recap.model.search.resolver.impl.item;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.solr.Item;

/**
 * Created by rajeshbabuk on 3/7/17.
 */
public class OwningInstitutionItemIdValueResolver implements ItemValueResolver {

    /**
     * Returns true if field name is 'OwningInstitutionItemId'.
     *
     * @param field the field
     * @return
     */
    @Override
    public Boolean isInterested(String field) {
        return RecapConstants.OWNING_INSTITUTION_ITEM_ID.equals(field);
    }

    /**
     * Set owning Institution Item Id value to the item
     *
     * @param item  the item
     * @param value the value
     */
    @Override
    public void setValue(Item item, Object value) {
        item.setOwningInstitutionItemId((String) value);
    }
}
