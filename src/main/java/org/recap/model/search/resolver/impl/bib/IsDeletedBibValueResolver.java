package org.recap.model.search.resolver.impl.bib;

import org.recap.RecapConstants;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by angelind on 8/11/16.
 */
public class IsDeletedBibValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase(RecapConstants.IS_DELETED_BIB);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setDeletedBib((Boolean) value);
    }
}
