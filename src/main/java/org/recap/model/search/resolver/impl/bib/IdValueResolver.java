package org.recap.model.search.resolver.impl.bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public class IdValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "id".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
            bibItem.setId((String)value);
    }
}
