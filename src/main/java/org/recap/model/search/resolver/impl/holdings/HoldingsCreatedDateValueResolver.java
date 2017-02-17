package org.recap.model.search.resolver.impl.holdings;

import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

import java.util.Date;

/**
 * Created by peris on 9/29/16.
 */
public class HoldingsCreatedDateValueResolver implements HoldingsValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "HoldingsCreatedDate".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setHoldingsCreatedDate((Date) value);
    }
}
