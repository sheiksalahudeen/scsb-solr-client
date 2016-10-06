package org.recap.executors;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.util.BibJSONUtil;

import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/17/16.
 */
public class BibRecordSetupCallable implements Callable {

    BibliographicEntity bibliographicEntity;
    BibliographicDetailsRepository bibliographicDetailsRepository;
    HoldingsDetailsRepository holdingsDetailsRepository;

    public BibRecordSetupCallable(BibliographicEntity bibliographicEntity, BibliographicDetailsRepository bibliographicDetailsRepository,
                                  HoldingsDetailsRepository holdingsDetailsRepository) {
        this.bibliographicEntity = bibliographicEntity;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
    }

    @Override
    public Object call() throws Exception {
        Bib bib = new BibJSONUtil().generateBibForIndex(bibliographicEntity, bibliographicDetailsRepository, holdingsDetailsRepository);
        return bib ;
    }
}
