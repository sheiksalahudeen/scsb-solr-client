package org.recap.model.search.resolver.impl.holdings;

import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 6/10/16.
 */
public class HoldingsRootValueResolver implements HoldingsValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equals("_root_");
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setRoot((String) value);
    }
}
