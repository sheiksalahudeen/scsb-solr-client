package org.recap.repository.solr.main;

import org.recap.model.search.SearchRecordsRequest;

import java.util.Map;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
public interface CustomDocumentRepository {

    Map<String,Object> search(SearchRecordsRequest searchRecordsRequest);
}
