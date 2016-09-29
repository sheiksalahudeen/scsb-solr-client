package org.recap.model.search.resolver;

import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public interface ValueResolver {
    public Boolean isInterested(String field);

    public void setValue(BibItem bibItem, String Value);
}
