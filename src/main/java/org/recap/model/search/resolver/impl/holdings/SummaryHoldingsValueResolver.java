package org.recap.model.search.resolver.impl.holdings;

import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by peris on 9/29/16.
 */
public class SummaryHoldingsValueResolver implements HoldingsValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "SummaryHoldings".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setSummaryHoldings((String) value);
    }
}
