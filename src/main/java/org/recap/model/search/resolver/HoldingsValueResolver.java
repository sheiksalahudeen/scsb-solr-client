package org.recap.model.search.resolver;

import org.recap.model.solr.Holdings;

/**
 * Created by angelind on 6/10/16.
 */
public interface HoldingsValueResolver extends ValueResolver {
    public void setValue(Holdings holdings, Object value);
}
