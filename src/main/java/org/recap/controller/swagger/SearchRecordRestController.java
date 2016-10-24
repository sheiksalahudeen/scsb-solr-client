package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.recap.controller.SearchRecordsController;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.util.SearchRecordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhish on 13/10/16.
 */
@RestController
@RequestMapping("/searchService")
@Api(value="search", description="Search Records", position = 1)
public class SearchRecordRestController {

    private Logger logger = LoggerFactory.getLogger(SearchRecordsController.class);
    @Autowired
    SearchRecordsUtil searchRecordsUtil=new SearchRecordsUtil();

    @RequestMapping(value="/search", method = RequestMethod.GET)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is String", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public List<SearchResultRow> searchRecordsServiceGetParam(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="requestJson") @RequestParam String requestJson) {

//        logger.info("Get " +requestJson);

        ObjectMapper mapper = new ObjectMapper();
        SearchRecordsRequest searchRecordsRequest = null;
        try {
            searchRecordsRequest = mapper.readValue(requestJson, SearchRecordsRequest.class);
        } catch (IOException e) {
            logger.info("search : "+e.getMessage());
        }
        if (searchRecordsRequest ==null){
            searchRecordsRequest = new SearchRecordsRequest();
        }
        List<SearchResultRow> searchResultRows = null;
        try {
            searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
        } catch (Exception e) {
            searchResultRows = new ArrayList<>();
        }
        return searchResultRows;
    }
}
