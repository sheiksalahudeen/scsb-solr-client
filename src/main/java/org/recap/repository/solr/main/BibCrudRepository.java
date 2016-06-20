package org.recap.repository.solr.main;

import org.recap.model.solr.Bib;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by pvsubrah on 6/11/16.
 */

public interface BibCrudRepository extends SolrCrudRepository<Bib, String> {

    Bib findByBarcode(String bibId);

}
