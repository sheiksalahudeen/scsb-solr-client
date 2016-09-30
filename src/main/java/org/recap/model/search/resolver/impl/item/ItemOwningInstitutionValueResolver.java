package org.recap.model.search.resolver.impl.item;

import org.recap.model.search.resolver.ItemValueResolver;
import org.recap.model.search.resolver.ValueResolver;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;

/**
 * Created by peris on 9/29/16.
 */
public class ItemOwningInstitutionValueResolver implements ItemValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equals("ItemOwningInstitution");
    }

    @Override
    public void setValue(Item item, Object value) {
        item.setOwningInstitution((String)value);
    }
}
