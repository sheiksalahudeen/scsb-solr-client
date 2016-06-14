package org.recap.executors;

import org.recap.model.Bib;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/13/16.
 */


public class BibIndexCallable implements Callable {
    private String coreName;
    private String solrURL;
    private List<Bib> bibs;

    private BibCrudRepositoryMultiCoreSupport bibCrudRepository;

    public BibIndexCallable(String solrURL, String coreName, List<Bib> bibs) {
        this.coreName = coreName;
        this.bibs = bibs;
        this.solrURL = solrURL;
    }

    @Override
    public Object call() throws Exception {
        bibCrudRepository = new BibCrudRepositoryMultiCoreSupport(coreName, solrURL);
        bibCrudRepository.save(bibs);

        //TODO: Need to return something more meaningful.
        return null;
    }
}
