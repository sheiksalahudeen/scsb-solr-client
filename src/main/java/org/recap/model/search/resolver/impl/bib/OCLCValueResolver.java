package org.recap.model.search.resolver.impl.bib;

import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.solr.BibItem;

import java.util.List;

/**
 * Created by peris on 9/29/16.
 */
public class OCLCValueResolver implements BibValueResolver {
    @Override
    public Boolean isInterested(String field) {
        return "OCLCNumber".equalsIgnoreCase(field);
    }

    @Override
    public void setValue(BibItem bibItem, Object value) {
        bibItem.setOclcNumber((List<String>)value);
    }

}
