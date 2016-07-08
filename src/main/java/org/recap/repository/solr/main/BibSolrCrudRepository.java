package org.recap.repository.solr.main;

import org.recap.model.solr.Bib;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */

public interface BibSolrCrudRepository extends SolrCrudRepository<Bib, String> {

    Bib findByBibId(Integer bibId);

    List<Bib> findByOclcNumber(String oclcNumber);

    List<Bib> findByIsbn(String isbn);

    List<Bib> findByIssn(String issn);

    List<Bib> findByLccn(String lccn);
}
