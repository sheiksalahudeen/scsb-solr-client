package org.recap.model.search.resolver.impl.holdings;

import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 6/10/16.
 */
public class DocTypeValueResolver implements HoldingsValueResolver{
    @Override
    public Boolean isInterested(String field) {
        return "DocType".equals(field);
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setDocType((String) value);
    }
}
