package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.controller.SearchRecordsController;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrDocumentRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.util.CsvUtil;
import org.recap.util.SearchRecordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by sudhish on 13/10/16.
 */
@RestController
@RequestMapping("/searchService")
@Api(value="search", description="Search Records", position = 1)
public class SearchRecordRestController {

    private Logger logger = LoggerFactory.getLogger(SearchRecordsController.class);

    @Autowired
    private BibSolrDocumentRepository bibSolrDocumentRepository;

    @Autowired
    private  ItemCrudRepository itemCrudRepository;

    @Autowired
    private CsvUtil csvUtil;

    @RequestMapping(value="/search-jsonString-GET", method = RequestMethod.GET)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is String", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public List<SearchResultRow> searchRecordsServiceGetParam(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="requestJson") @RequestParam String requestJson) {

//        logger.info("Get " +requestJson);
        ObjectMapper mapper = new ObjectMapper();
        SearchRecordsUtil searchRecordsUtil =new SearchRecordsUtil();
        SearchRecordsRequest searchRecordsRequest = null;
        try {
            searchRecordsRequest = mapper.readValue(requestJson, SearchRecordsRequest.class);
        } catch (IOException e) {
            searchRecordsRequest = new SearchRecordsRequest();
            logger.info("search : "+e.getMessage());
        }
        searchRecordsRequest = searchRecordsUtil.searchRecords(searchRecordsRequest);
        return searchRecordsRequest.getSearchResultRows();
    }
}
