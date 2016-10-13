package org.recap.executors;

import org.apache.camel.ProducerTemplate;
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
    ProducerTemplate producerTemplate;

    public BibRecordSetupCallable(BibliographicEntity bibliographicEntity, BibliographicDetailsRepository bibliographicDetailsRepository,
                                  HoldingsDetailsRepository holdingsDetailsRepository, ProducerTemplate producerTemplate) {
        this.bibliographicEntity = bibliographicEntity;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Object call() throws Exception {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        bibJSONUtil.setProducerTemplate(producerTemplate);
        Bib bib = bibJSONUtil.generateBibForIndex(bibliographicEntity, bibliographicDetailsRepository, holdingsDetailsRepository);
        return bib ;
    }
}
