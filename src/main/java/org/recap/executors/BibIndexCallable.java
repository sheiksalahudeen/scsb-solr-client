package org.recap.executors;

import org.recap.model.Bib;
import org.recap.repository.BibCrudRepository;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by pvsubrah on 6/13/16.
 */


public class BibIndexCallable implements Callable {
    private String coreName;
    private List<Bib> bibs;

    private BibCrudRepository bibCrudRepository;

    public BibIndexCallable(String coreName, List<Bib> bibs, BibCrudRepository bibCrudRepository) {
        this.coreName = coreName;
        this.bibs = bibs;
        this.bibCrudRepository = bibCrudRepository;
    }

    @Override
    public Object call() throws Exception {
        bibCrudRepository.save(bibs);

        //TODO: Need to return something more meaningful.
        return null;
    }
}
