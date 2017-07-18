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

    @ApiModelProperty(name= "requestingInstitution", value= "Requesting Institution",position = 22)
    private String requestingInstitution = "";

    private boolean sortIncompleteRecords = false;


    /**
     * Instantiates a new search records request.
     */
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

    /**
     * Gets field value.
     *
     * @return the field value
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Sets field value.
     *
     * @param fieldValue the field value
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets field name.
     *
     * @param fieldName the field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets owning institutions.
     *
     * @return the owning institutions
     */
    public List<String> getOwningInstitutions() {
        if (null == owningInstitutions) {
            owningInstitutions = new ArrayList<>();
        }
        return owningInstitutions;
    }

    /**
     * Sets owning institutions.
     *
     * @param owningInstitutions the owning institutions
     */
    public void setOwningInstitutions(List<String> owningInstitutions) {
        this.owningInstitutions = owningInstitutions;
    }

    /**
     * Gets collection group designations.
     *
     * @return the collection group designations
     */
    public List<String> getCollectionGroupDesignations() {
        if (null == collectionGroupDesignations) {
            collectionGroupDesignations = new ArrayList<>();
        }
        return collectionGroupDesignations;
    }

    /**
     * Sets collection group designations.
     *
     * @param collectionGroupDesignations the collection group designations
     */
    public void setCollectionGroupDesignations(List<String> collectionGroupDesignations) {
        this.collectionGroupDesignations = collectionGroupDesignations;
    }

    /**
     * Gets availability.
     *
     * @return the availability
     */
    public List<String> getAvailability() {
        if (null == availability) {
            availability = new ArrayList<>();
        }
        return availability;
    }

    /**
     * Sets availability.
     *
     * @param availability the availability
     */
    public void setAvailability(List<String> availability) {
        this.availability = availability;
    }

    /**
     * Gets material types.
     *
     * @return the material types
     */
    public List<String> getMaterialTypes() {
        if (null == materialTypes) {
            materialTypes = new ArrayList<>();
        }
        return materialTypes;
    }

    /**
     * Sets material types.
     *
     * @param materialTypes the material types
     */
    public void setMaterialTypes(List<String> materialTypes) {
        this.materialTypes = materialTypes;
    }

    /**
     * Gets use restrictions.
     *
     * @return the use restrictions
     */
    public List<String> getUseRestrictions() {
        if(null == useRestrictions) {
            useRestrictions = new ArrayList<>();
        }
        return useRestrictions;
    }

    /**
     * Sets use restrictions.
     *
     * @param useRestrictions the use restrictions
     */
    public void setUseRestrictions(List<String> useRestrictions) {
        this.useRestrictions = useRestrictions;
    }

    /**
     * Gets search result rows.
     *
     * @return the search result rows
     */
    public List<SearchResultRow> getSearchResultRows() {
        if (null == searchResultRows) {
            searchResultRows = new ArrayList<>();
        }
        return searchResultRows;
    }

    /**
     * Sets search result rows.
     *
     * @param searchResultRows the search result rows
     */
    public void setSearchResultRows(List<SearchResultRow> searchResultRows) {
        this.searchResultRows = searchResultRows;
    }

    /**
     * Gets total page count.
     *
     * @return the total page count
     */
    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    /**
     * Sets total page count.
     *
     * @param totalPageCount the total page count
     */
    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    /**
     * Gets page number.
     *
     * @return the page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets page number.
     *
     * @param pageNumber the page number
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets total bib records count.
     *
     * @return the total bib records count
     */
    public String getTotalBibRecordsCount() {
        return totalBibRecordsCount;
    }

    /**
     * Sets total bib records count.
     *
     * @param totalBibRecordsCount the total bib records count
     */
    public void setTotalBibRecordsCount(String totalBibRecordsCount) {
        this.totalBibRecordsCount = totalBibRecordsCount;
    }

    /**
     * Gets total item records count.
     *
     * @return the total item records count
     */
    public String getTotalItemRecordsCount() {
        return totalItemRecordsCount;
    }

    /**
     * Sets total item records count.
     *
     * @param totalItemRecordsCount the total item records count
     */
    public void setTotalItemRecordsCount(String totalItemRecordsCount) {
        this.totalItemRecordsCount = totalItemRecordsCount;
    }

    /**
     * Gets total records count.
     *
     * @return the total records count
     */
    public String getTotalRecordsCount() {
        return totalRecordsCount;
    }

    /**
     * Sets total records count.
     *
     * @param totalRecordsCount the total records count
     */
    public void setTotalRecordsCount(String totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }

    /**
     * Is show results boolean.
     *
     * @return the boolean
     */
    public boolean isShowResults() {
        return showResults;
    }

    /**
     * Sets show results.
     *
     * @param showResults the show results
     */
    public void setShowResults(boolean showResults) {
        this.showResults = showResults;
    }

    /**
     * Is select all boolean.
     *
     * @return the boolean
     */
    public boolean isSelectAll() {
        return selectAll;
    }

    /**
     * Is select all facets boolean.
     *
     * @return the boolean
     */
    public boolean isSelectAllFacets() {
        return selectAllFacets;
    }

    /**
     * Sets select all facets.
     *
     * @param selectAllFacets the select all facets
     */
    public void setSelectAllFacets(boolean selectAllFacets) {
        this.selectAllFacets = selectAllFacets;
    }

    /**
     * Sets select all.
     *
     * @param selectAll the select all
     */
    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    /**
     * Is show total count boolean.
     *
     * @return the boolean
     */
    public boolean isShowTotalCount() {
        return showTotalCount;
    }

    /**
     * Sets show total count.
     *
     * @param showTotalCount the show total count
     */
    public void setShowTotalCount(boolean showTotalCount) {
        this.showTotalCount = showTotalCount;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }

    /**
     * Is deleted boolean.
     *
     * @return the boolean
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Sets deleted.
     *
     * @param deleted the deleted
     */
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * Gets cataloging status.
     *
     * @return the cataloging status
     */
    public String getCatalogingStatus() {
        return catalogingStatus;
    }

    /**
     * Sets cataloging status.
     *
     * @param catalogingStatus the cataloging status
     */
    public void setCatalogingStatus(String catalogingStatus) {
        this.catalogingStatus = catalogingStatus;
    }

    /**
     * Reset.
     */
    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }


    /**
     * Gets requesting institution.
     *
     * @return the requesting institution
     */
    public String getRequestingInstitution() {
        return requestingInstitution;
    }


    /**
     * Sets requesting institution.
     *
     * @param requestingInstitution the requesting institution
     */
    public void setRequestingInstitution(String requestingInstitution) {
        this.requestingInstitution = requestingInstitution;
    }

    /**
     * Is sort incomplete records boolean.
     *
     * @return the boolean
     */
    public boolean isSortIncompleteRecords() {
        return sortIncompleteRecords;
    }

    /**
     * Sets sort incomplete records.
     *
     * @param sortIncompleteRecords the sort incomplete records
     */
    public void setSortIncompleteRecords(boolean sortIncompleteRecords) {
        this.sortIncompleteRecords = sortIncompleteRecords;
    }
}
