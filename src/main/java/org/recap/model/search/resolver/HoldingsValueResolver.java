package org.recap.model.search.resolver;

import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 6/10/16.
 */
public interface HoldingsValueResolver extends ValueResolver {
    /**
     * Gets value from object value and sets to holdings.
     *
     * @param holdings the holdings
     * @param value    the value
     */
    public void setValue(Holdings holdings, Object value);
}
