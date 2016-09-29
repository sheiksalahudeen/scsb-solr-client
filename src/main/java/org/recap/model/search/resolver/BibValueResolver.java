package org.recap.model.search.resolver;

import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public interface BibValueResolver extends ValueResolver {
    public void setValue(BibItem bibItem, Object Value);

}
