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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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


    @RequestMapping(value="/searchRecords", method = RequestMethod.POST)
    @ApiOperation(value = "searchRecords",notes = "Search Books in ReCAP - Using Method Post, Request data is String", nickname = "searchRecords", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public Map searchRecords(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="requestJson") @RequestBody SearchRecordsRequest searchRecordsRequest) {
        List<SearchResultRow> searchResultRows = null;
        Map responseMap = new HashMap();
        try {
            searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
            responseMap.put("totalPageCount", searchRecordsRequest.getTotalPageCount());
            responseMap.put("totalBibsCount", searchRecordsRequest.getTotalBibRecordsCount());
            responseMap.put("totalItemsCount", searchRecordsRequest.getTotalItemRecordsCount());
            responseMap.put("searchResultRows", searchResultRows);
        } catch (Exception e) {
            logger.info("search : "+e.getMessage());
        }
        return responseMap;
    }
}
