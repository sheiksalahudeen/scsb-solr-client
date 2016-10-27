package org.recap.model.search.resolver.impl.Bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by rajeshbabuk on 27/10/16.
 */
public class BibCreatedByValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("BibCreatedBy");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setBibCreatedBy((String) value);
    }
}
