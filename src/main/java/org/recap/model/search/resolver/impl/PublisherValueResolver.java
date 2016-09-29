package org.recap.model.search.resolver.impl;

import org.recap.model.search.resolver.ValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public class PublisherValueResolver implements ValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("Publisher");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setPublisher((String) value);
    }
}
