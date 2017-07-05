package org.recap.model.search.resolver.impl.holdings;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.HoldingsValueResolver;
import org.recap.model.solr.Holdings;

/**
 * Created by rajeshbabuk on 3/7/17.
 */
public class OwningInstitutionHoldingsIdValueResolver implements HoldingsValueResolver {

    /**
     * Returns true if field name is 'OwningInstitutionHoldingsId'.
     *
     * @param field the field
     * @return
     */
    @Override
    public Boolean isInterested(String field) {
        return RecapConstants.OWNING_INSTITUTION_HOLDINGS_ID.equals(field);
    }

    /**
     * Set owning Institution Holdings Id value to holdings
     *
     * @param holdings the holdings
     * @param value the value
     */
    @Override
    public void setValue(Holdings holdings, Object value) {
        holdings.setOwningInstitutionHoldingsId((String) value);
    }
}