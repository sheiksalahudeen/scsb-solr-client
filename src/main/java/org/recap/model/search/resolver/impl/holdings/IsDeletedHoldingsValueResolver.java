package org.recap.model.search.resolver.impl.holdings;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 8/11/16.
 */
public class IsDeletedHoldingsValueResolver implements HoldingsValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase(RecapConstants.IS_DELETED_HOLDINGS);
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setDeletedHoldings((Boolean) value);
    }
}
