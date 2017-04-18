package org.recap.model.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.recap.RecapConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
@ApiModel(value="SearchRecordsRequest", description="Model for showing user details")
public class SearchRecordsRequest implements Serializable {


    @ApiModelProperty(name= "fieldValue", value= "Search Value",  position = 0)
    private String fieldValue = "";

    @ApiModelProperty(name ="fieldName", value= "Select a field name",position = 1)
    private String fieldName;

    @ApiModelProperty(name= "owningInstitutions", value= "Publications Owning Instutions", position = 3, allowableValues="PUL, NYPL, CUL")
    private List<String> owningInstitutions = null;

    @ApiModelProperty(name= "collectionGroupDesignations", value= "Collection Group Designations",position = 4)
    private List<String> collectionGroupDesignations = null;

    @ApiModelProperty(name= "availability", value= "Availability of books in ReCAP",position = 5)
    private List<String> availability = null;

    @ApiModelProperty(name= "materialTypes", value= "Material Types",position = 6)
    private List<String> materialTypes = null;

    @ApiModelProperty(name= "useRestrictions", value= "Book Use Restrictions",position = 7)
    private List<String> useRestrictions = null;

    @ApiModelProperty(name= "searchResultRows", value= "Search Response",position = 8)
    private List<SearchResultRow> searchResultRows = new ArrayList<>();

    @ApiModelProperty(name= "totalPageCount", value= "Total Page Count",position = 9)
    private Integer totalPageCount = 0;

    @ApiModelProperty(name= "totalBibRecordsCount", value= "Total Bibliograph Records Count",position = 10)
    private String totalBibRecordsCount = "0";

    @ApiModelProperty(name= "totalItemRecordsCount", value= "Total Item Count",position = 11)
    private String totalItemRecordsCount = "0";

    @ApiModelProperty(name= "totalRecordsCount", value= "Total Records Count",position = 12)
    private String totalRecordsCount = "0";

    @ApiModelProperty(name= "pageNumber", value= "Current Page Number",position = 13)
    private Integer pageNumber = 0;

    @ApiModelProperty(name= "pageSize", value= "Total records to show is page",position = 14)
    private Integer pageSize = 10;

    @ApiModelProperty(name= "showResults", value= "Show Results",position = 15)
    private boolean showResults = false;

    @ApiModelProperty(name= "selectAll", value= "select All Fields",position = 16)
    private boolean selectAll = false;

    @ApiModelProperty(name= "selectAllFacets", value= "Select All Facets",position = 17)
    private boolean selectAllFacets = false;

    @ApiModelProperty(name= "showTotalCount", value= "Show Total Count",position = 18)
    private boolean showTotalCount = false;

    @ApiModelProperty(name= "index", value= "index",position = 19)
    private Integer index;

    @ApiModelProperty(name= "errorMessage", value= "Error Message",position = 20)
    private String errorMessage;

    @ApiModelProperty(name= "isDeleted", value= "Is Deleted",position = 21)
    private boolean isDeleted = false;

    @ApiModelProperty(name= "catalogingStatus", value= "Cataloging Status",position = 22)
    private String catalogingStatus;

    private boolean sortIncompleteRecords = false;


    public SearchRecordsRequest() {
        this.setFieldName("");
        this.setFieldValue("");
        this.setSelectAllFacets(true);
        this.setDeleted(false);
        this.setCatalogingStatus(RecapConstants.COMPLETE_STATUS);

        this.getOwningInstitutions().add("NYPL");
        this.getOwningInstitutions().add("CUL");
        this.getOwningInstitutions().add("PUL");

        this.getCollectionGroupDesignations().add("Shared");
        this.getCollectionGroupDesignations().add("Private");
        this.getCollectionGroupDesignations().add("Open");

        this.getAvailability().add("Available");
        this.getAvailability().add("NotAvailable");

        this.getMaterialTypes().add("Monograph");
        this.getMaterialTypes().add("Serial");
        this.getMaterialTypes().add("Other");

        this.getUseRestrictions().add("NoRestrictions");
        this.getUseRestrictions().add("InLibraryUse");
        this.getUseRestrictions().add("SupervisedUse");

        this.setPageNumber(0);
        this.setPageSize(10);
        this.setShowResults(false);
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getOwningInstitutions() {
        if (null == owningInstitutions) {
            owningInstitutions = new ArrayList<>();
        }
        return owningInstitutions;
    }

    public void setOwningInstitutions(List<String> owningInstitutions) {
        this.owningInstitutions = owningInstitutions;
    }

    public List<String> getCollectionGroupDesignations() {
        if (null == collectionGroupDesignations) {
            collectionGroupDesignations = new ArrayList<>();
        }
        return collectionGroupDesignations;
    }

    public void setCollectionGroupDesignations(List<String> collectionGroupDesignations) {
        this.collectionGroupDesignations = collectionGroupDesignations;
    }

    public List<String> getAvailability() {
        if (null == availability) {
            availability = new ArrayList<>();
        }
        return availability;
    }

    public void setAvailability(List<String> availability) {
        this.availability = availability;
    }

    public List<String> getMaterialTypes() {
        if (null == materialTypes) {
            materialTypes = new ArrayList<>();
        }
        return materialTypes;
    }

    public void setMaterialTypes(List<String> materialTypes) {
        this.materialTypes = materialTypes;
    }

    public List<String> getUseRestrictions() {
        if(null == useRestrictions) {
            useRestrictions = new ArrayList<>();
        }
        return useRestrictions;
    }

    public void setUseRestrictions(List<String> useRestrictions) {
        this.useRestrictions = useRestrictions;
    }

    public List<SearchResultRow> getSearchResultRows() {
        if (null == searchResultRows) {
            searchResultRows = new ArrayList<>();
        }
        return searchResultRows;
    }

    public void setSearchResultRows(List<SearchResultRow> searchResultRows) {
        this.searchResultRows = searchResultRows;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalBibRecordsCount() {
        return totalBibRecordsCount;
    }

    public void setTotalBibRecordsCount(String totalBibRecordsCount) {
        this.totalBibRecordsCount = totalBibRecordsCount;
    }

    public String getTotalItemRecordsCount() {
        return totalItemRecordsCount;
    }

    public void setTotalItemRecordsCount(String totalItemRecordsCount) {
        this.totalItemRecordsCount = totalItemRecordsCount;
    }

    public String getTotalRecordsCount() {
        return totalRecordsCount;
    }

    public void setTotalRecordsCount(String totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }

    public boolean isShowResults() {
        return showResults;
    }

    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public boolean isSelectAllFacets() {
        return selectAllFacets;
    }

    public void setSelectAllFacets(boolean selectAllFacets) {
        this.selectAllFacets = selectAllFacets;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public boolean isShowTotalCount() {
        return showTotalCount;
    }

    public void setShowTotalCount(boolean showTotalCount) {
        this.showTotalCount = showTotalCount;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void resetPageNumber() {
        this.pageNumber = 0;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCatalogingStatus() {
        return catalogingStatus;
    }

    public void setCatalogingStatus(String catalogingStatus) {
        this.catalogingStatus = catalogingStatus;
    }

    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }

    public boolean isSortIncompleteRecords() {
        return sortIncompleteRecords;
    }

    public void setSortIncompleteRecords(boolean sortIncompleteRecords) {
        this.sortIncompleteRecords = sortIncompleteRecords;
    }
}
