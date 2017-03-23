package org.recap.model.search.resolver.impl.bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

import java.util.List;

/**
 * Created by peris on 9/29/16.
 */
public class AuthorSearchValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "Author_search".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setAuthorSearch((List) value);
    }
}
