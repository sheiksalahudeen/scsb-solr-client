package org.recap.model.search.resolver.impl;

import org.recap.model.search.resolver.ValueResolver;
import org.recap.model.solr.BibItem;

import java.util.List;

/**
 * Created by peris on 9/29/16.
 */
public class ISSNValueResolver implements ValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("ISSN");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setIssn((List<String>)value);
    }

}
