package org.recap.repository.solr.main;

import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.solr.BibItem;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
public interface CustomDocumentRepository {

    List<BibItem> search(SearchRecordsRequest searchRecordsRequest, Pageable page);
}
