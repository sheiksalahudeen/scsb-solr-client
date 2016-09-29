package org.recap.model.search.resolver;

import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;

/**
 * Created by peris on 9/29/16.
 */
public interface ItemValueResolver extends ValueResolver {
    public void setValue(Item item, Object Value);
}
