package org.recap.model.search.resolver.impl.Bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

import java.util.Date;

/**
 * Created by rajeshbabuk on 27/10/16.
 */
public class BibCreatedDateValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("BibCreatedDate");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setBibCreatedDate((Date) value);
    }
}
