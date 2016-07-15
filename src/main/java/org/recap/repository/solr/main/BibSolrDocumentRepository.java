package org.recap.repository.solr.main;

import org.recap.model.solr.BibItem;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
public interface BibSolrDocumentRepository extends CustomDocumentRepository, SolrCrudRepository<BibItem, String> {
}
