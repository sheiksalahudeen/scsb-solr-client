package org.recap.repository.solr.main;

import org.recap.model.solr.BibItem;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Service;

/**
 * Created by angelind on 26/10/16.
 */
@Service
public interface DataDumpSolrDocumentRepository extends CustomDocumentRepository, SolrCrudRepository<BibItem, String> {
}
