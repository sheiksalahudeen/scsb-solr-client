package org.recap.model.search.resolver.impl.bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

import java.util.Date;

/**
 * Created by rajeshbabuk on 27/10/16.
 */
public class BibLastUpdatedDateValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "BibLastUpdatedDate".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setBibLastUpdatedDate((Date) value);
    }
}

