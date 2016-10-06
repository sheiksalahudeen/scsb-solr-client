package org.recap.model.search.resolver.impl.holdings;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;

/**
 * Created by peris on 9/29/16.
 */
public class SummaryHoldingsValueResolver implements HoldingsValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("SummaryHoldings");
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setSummaryHoldings((String) value);
    }
}
