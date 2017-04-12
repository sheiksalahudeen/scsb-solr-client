package org.recap.model.search.resolver.impl.bib;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public class OwningInstitutionBibIdValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return RecapConstants.OWNING_INSTITUTION_BIB_ID.equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setOwningInstitutionBibId((String)value);
    }
}
