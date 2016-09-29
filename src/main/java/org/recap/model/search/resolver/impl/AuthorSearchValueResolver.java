package org.recap.model.search.resolver.impl;

import org.recap.model.search.resolver.ValueResolver;
import org.recap.model.solr.BibItem;

import java.util.List;

/**
 * Created by peris on 9/29/16.
 */
public class AuthorSearchValueResolver implements ValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("Author_search");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setAuthorSearch((List) value);
    }
}
