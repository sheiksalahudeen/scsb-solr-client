package org.recap.repository.solr.main;

import org.recap.model.Bib;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by pvsubrah on 6/11/16.
 */

public interface BibCrudRepository extends SolrCrudRepository<Bib, String> {

    Bib findByBarcode(String bibId);

}
