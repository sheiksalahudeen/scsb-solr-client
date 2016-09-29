package org.recap.model.search.resolver.impl.Bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public class LCCNValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return field.equalsIgnoreCase("LCCN");
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setLccn((String)value);
    }

}
