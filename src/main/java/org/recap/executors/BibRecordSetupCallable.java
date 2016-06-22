package org.recap.executors;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.util.BibJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/17/16.
 */
public class BibRecordSetupCallable implements Callable {

    BibliographicEntity bibliographicEntity;

    public BibRecordSetupCallable(BibliographicEntity bibliographicEntity) {
        this.bibliographicEntity = bibliographicEntity;
    }

    @Override
    public Object call() throws Exception {
        Bib bib = new BibJSONUtil().generateBibForIndex(bibliographicEntity);
        return bib ;
    }
}
