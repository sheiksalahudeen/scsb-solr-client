package org.recap.repository.solr.main;

import org.recap.model.search.SearchRecordsRequest;

import java.util.Map;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
public interface CustomDocumentRepository {

    /**
     * This method is used to search records based on the given search records request.
     *
     * @param searchRecordsRequest the search records request
     * @return the map
     */
    Map<String,Object> search(SearchRecordsRequest searchRecordsRequest);

    /**
     * Gets page number on changing the page size in UI.
     *
     * @param searchRecordsRequest the search records request
     * @return the page number on page size change
     */
    Integer getPageNumberOnPageSizeChange(SearchRecordsRequest searchRecordsRequest);
}
