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


    @RequestMapping(value="/search", method = RequestMethod.POST)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is @RequestBody", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsRequest searchRecordsService(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="searchRecordsRequest") @RequestBody SearchRecordsRequest searchRecordsRequest) {

        logger.info("search");
        return searchRecords(searchRecordsRequest);
    }

    @RequestMapping(value="/search-Model-Post", method = RequestMethod.POST)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is @ModelAttribute", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsRequest searchRecordsServiceByModel(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="searchRecordsRequest") @ModelAttribute SearchRecordsRequest searchRecordsRequest) {

        logger.info("search");
        return searchRecords(searchRecordsRequest);
    }

    @RequestMapping(value="/search-jsonString-GET", method = RequestMethod.GET)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is String", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsRequest searchRecordsServiceGetParam(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="requestJson") @RequestParam String requestJson) {

//        logger.info("Get " +requestJson);
        ObjectMapper mapper = new ObjectMapper();
        SearchRecordsRequest searchRecordsRequest = null;
        try {
            searchRecordsRequest = mapper.readValue(requestJson, SearchRecordsRequest.class);
        } catch (IOException e) {
            searchRecordsRequest = new SearchRecordsRequest();
            logger.info("search : "+e.getMessage());
        }
        return searchRecords(searchRecordsRequest);
    }

    @RequestMapping(value="/search-Model-GET", method = RequestMethod.GET)
    @ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is @ModelAttribute", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsRequest searchRecordsServiceGet(@ApiParam(value = "Paramerters for Searching Records" , required = true, name="searchRecordsRequest") @ModelAttribute SearchRecordsRequest searchRecordsRequest) {

        logger.info("Get ");
        SearchRecordsRequest srq= searchRecords(searchRecordsRequest);
        logger.info("exit ");
        return searchRecords(srq);
    }

    private SearchRecordsRequest searchRecords(SearchRecordsRequest searchRecordsRequest) {

        if(!isEmptySearch(searchRecordsRequest)){
            searchRecordsRequest.reset();
            searchRecordsRequest.resetPageNumber();
            Map<String, Object> searchResponse = bibSolrDocumentRepository.search(searchRecordsRequest);
            String errorResponse = (String) searchResponse.get(RecapConstants.SEARCH_ERROR_RESPONSE);
            if(errorResponse != null) {
                searchRecordsRequest.setErrorMessage(RecapConstants.SERVER_ERROR_MSG);
            } else {
                List<BibItem> bibItems = (List<BibItem>) searchResponse.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
                buildResults(searchRecordsRequest, bibItems);
            }
            return searchRecordsRequest;
        }
        searchRecordsRequest.setErrorMessage(RecapConstants.EMPTY_FACET_ERROR_MSG);
        return searchRecordsRequest;
    }

    private void buildResults(SearchRecordsRequest searchRecordsRequest, List<BibItem> bibItems) {
        searchRecordsRequest.setSearchResultRows(null);
        searchRecordsRequest.setShowResults(true);
        searchRecordsRequest.setSelectAll(false);
        if (!CollectionUtils.isEmpty(bibItems)) {
            List<SearchResultRow> searchResultRows = new ArrayList<>();
            for (BibItem bibItem : bibItems) {
                SearchResultRow searchResultRow = new SearchResultRow();
                searchResultRow.setBibId(bibItem.getBibId());
                searchResultRow.setTitle(bibItem.getTitleDisplay());
                searchResultRow.setAuthor(bibItem.getAuthorDisplay());
                searchResultRow.setPublisher(bibItem.getPublisher());
                searchResultRow.setPublisherDate(bibItem.getPublicationDate());
                searchResultRow.setOwningInstitution(bibItem.getOwningInstitution());
                searchResultRow.setLeaderMaterialType(bibItem.getLeaderMaterialType());
                Holdings holdings = CollectionUtils.isEmpty(bibItem.getHoldingsList()) ? new Holdings() : bibItem.getHoldingsList().get(0);
                if (null != bibItem.getItems() && bibItem.getItems().size() == 1 && !RecapConstants.SERIAL.equals(bibItem.getLeaderMaterialType())) {
                    Item item = bibItem.getItems().get(0);
                    if (null != item) {
                        searchResultRow.setCustomerCode(item.getCustomerCode());
                        searchResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                        searchResultRow.setUseRestriction(item.getUseRestrictionDisplay());
                        searchResultRow.setBarcode(item.getBarcode());
                        searchResultRow.setAvailability(item.getAvailabilityDisplay());
                        searchResultRow.setSummaryHoldings(holdings.getSummaryHoldings());
                    }
                } else {
                    if (!CollectionUtils.isEmpty(bibItem.getItems())) {
                        List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
                        for (Item item : bibItem.getItems()) {
                            if (null != item) {
                                SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
                                searchItemResultRow.setCallNumber(item.getCallNumberDisplay());
                                searchItemResultRow.setChronologyAndEnum(item.getVolumePartYear());
                                searchItemResultRow.setCustomerCode(item.getCustomerCode());
                                searchItemResultRow.setBarcode(item.getBarcode());
                                searchItemResultRow.setUseRestriction(item.getUseRestrictionDisplay());
                                searchItemResultRow.setCollectionGroupDesignation(item.getCollectionGroupDesignation());
                                searchItemResultRow.setAvailability(item.getAvailabilityDisplay());
                                searchItemResultRows.add(searchItemResultRow);
                            }
                        }
                        searchResultRow.setSummaryHoldings(holdings.getSummaryHoldings());
                        searchResultRow.setShowItems(true);
                        Collections.sort(searchItemResultRows);
                        searchResultRow.setSearchItemResultRows(searchItemResultRows);
                    }
                }
                searchResultRows.add(searchResultRow);
            }
            searchRecordsRequest.setSearchResultRows(searchResultRows);
        }
    }

    private boolean isEmptySearch(SearchRecordsRequest searchRecordsRequest) {
        boolean emptySearch = false;
        if (searchRecordsRequest.getMaterialTypes().size() == 0 && searchRecordsRequest.getAvailability().size() == 0 &&
                searchRecordsRequest.getCollectionGroupDesignations().size() == 0 && searchRecordsRequest.getOwningInstitutions().size() == 0 && searchRecordsRequest.getUseRestrictions().size() == 0) {
            emptySearch = true;
        } else if(!((CollectionUtils.isNotEmpty(searchRecordsRequest.getMaterialTypes()) || CollectionUtils.isNotEmpty(searchRecordsRequest.getOwningInstitutions())) &&
                (CollectionUtils.isNotEmpty(searchRecordsRequest.getAvailability()) || CollectionUtils.isNotEmpty(searchRecordsRequest.getCollectionGroupDesignations())
                        || CollectionUtils.isNotEmpty(searchRecordsRequest.getUseRestrictions())))) {
            emptySearch = true;
        }
        return emptySearch;
    }

}
