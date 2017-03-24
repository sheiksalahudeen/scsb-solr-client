package org.recap.model.search.resolver.impl.Bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by angelind on 23/3/17.
 */
public class TitleSubFieldAValueResolver implements BibValueResolver {


    @Override
    public Boolean isInterested(String field) {
        return "Title_subfield_a".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setTitleSubFieldA((String) value);
    }
}
