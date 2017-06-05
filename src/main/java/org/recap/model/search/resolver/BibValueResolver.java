package org.recap.model.search.resolver;

import org.recap.model.solr.BibItem;

/**
 * Created by peris on 9/29/16.
 */
public interface BibValueResolver extends ValueResolver {
    /**
     * Gets value from object value and sets to bib item.
     *
     * @param bibItem the bib item
     * @param value   the value
     */
    public void setValue(BibItem bibItem, Object value);

}
