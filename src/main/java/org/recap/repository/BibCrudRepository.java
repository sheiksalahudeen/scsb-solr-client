package org.recap.repository;

import org.recap.model.Bib;
import org.springframework.context.annotation.Scope;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by pvsubrah on 6/11/16.
 */

@Scope("prototype")
public interface BibCrudRepository extends SolrCrudRepository<Bib, Long> {

}
