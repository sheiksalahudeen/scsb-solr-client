package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.recap.controller.SearchRecordsController;
import org.recap.model.search.DataDumpSearchResult;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.util.SearchRecordsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

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
        List<DataDumpSearchResult> dataDumpSearchResults = null;
        Map responseMap = new HashMap();
        try {
            dataDumpSearchResults = searchRecordsUtil.searchRecordsForDataDump(searchRecordsRequest);
            responseMap.put("totalPageCount", searchRecordsRequest.getTotalPageCount());
            responseMap.put("totalRecordsCount", searchRecordsRequest.getTotalRecordsCount());
            responseMap.put("dataDumpSearchResults", dataDumpSearchResults);
        } catch (Exception e) {
            logger.info("search : "+e.getMessage());
        }
        return responseMap;
    }


    @RequestMapping(value="/searchByParam", method = RequestMethod.GET)
    @ApiOperation(value = "searchParam",notes = "Search Books in ReCAP - Using Method GET, Request data as parameter", nickname = "search", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public List<SearchResultRow> searchRecordsServiceGet(
            @RequestParam(name="fieldValue", required = false)  String fieldValue,
            @ApiParam(name="fieldName",required = false,allowableValues = "Author_search,Title_search,TitleStartsWith,Publisher,PublicationPlace,PublicationDate,Subject,ISBN,ISSN,OCLCNumber,Notes,CallNumber_search,Barcode") @RequestParam(name="fieldName", value = "fieldName" , required = false)  String fieldName,
            @ApiParam(name="owningInstitutions", value= "Owning Institutions : PUL, CUL, NYPL")@RequestParam(name="owningInstitutions",required = false ) String owningInstitutions,
            @ApiParam(name="collectionGroupDesignations", value = "collection Designations : Shared,Private,Open") @RequestParam(name="collectionGroupDesignations", value = "collectionGroupDesignations" , required = false)  String collectionGroupDesignations,
            @ApiParam(name="availability", value = "Availability: Available, NotAvailable") @RequestParam(name="availability", value = "availability" , required = false)  String availability,
            @ApiParam(name="materialTypes", value = "MaterialTypes: Monograph, Serial, Other") @RequestParam(name="materialTypes", value = "materialTypes" , required = false)  String materialTypes,
            @ApiParam(name="useRestrictions", value = "Use Restrictions: NoRestrictions, InLibraryUse, SupervisedUse") @RequestParam(name="useRestrictions", value = "useRestrictions" , required = false)  String useRestrictions,
            @ApiParam(name="pageSize", value = "Page Size in Numers - 10, 20 30...") @RequestParam(name="pageSize", required = false) Integer pageSize
    ) {

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        if (fieldValue !=null) {
            searchRecordsRequest.setFieldValue(fieldValue);
        }
        if (fieldName !=null) {
            searchRecordsRequest.setFieldName(fieldName);
        }
        if(owningInstitutions !=null && owningInstitutions.trim().length()>0) {
            searchRecordsRequest.setOwningInstitutions(Arrays.asList(owningInstitutions.split(",")));
        }
        if(collectionGroupDesignations !=null && collectionGroupDesignations.trim().length()>0) {
            searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList(collectionGroupDesignations.split(",")));
        }
        if(availability !=null && availability.trim().length()>0) {
            searchRecordsRequest.setAvailability(Arrays.asList(availability.split(",")));
        }
        if(materialTypes !=null && materialTypes.trim().length()>0) {
            searchRecordsRequest.setMaterialTypes(Arrays.asList(materialTypes.split(",")));
        }
        if(pageSize !=null) {
            searchRecordsRequest.setPageSize(pageSize);
        }
        List<SearchResultRow> searchResultRows = null;
        try {
            searchResultRows = searchRecordsUtil.searchRecords(searchRecordsRequest);
        } catch (Exception e) {
            searchResultRows = new ArrayList<>();
            logger.error("Exception",e);
        }
        return searchResultRows;
    }
}
