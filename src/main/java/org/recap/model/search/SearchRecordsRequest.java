package org.recap.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
@ApiModel(value="SearchRecordsRequest", description="Model for showing user details")
public class SearchRecordsRequest implements Serializable {


    @ApiModelProperty(name= "SearchValue", value= "Search Value",  position = 0)
    private String fieldValue = "";

    @ApiModelProperty(position = 1)
    private String fieldName;

    @ApiModelProperty(name= "owningInstitutions", value= "Publications Owning Instutions", position = 3, allowableValues="PUL,NYPL,CUL")
    private List<String> owningInstitutions = null;

    @ApiModelProperty(position = 4)
    private List<String> collectionGroupDesignations = null;

    @ApiModelProperty(position = 5)
    private List<String> availability = null;
    @JsonProperty
    @ApiModelProperty(position = 6)
    private List<String> materialTypes = null;
    @JsonProperty
    @ApiModelProperty(position = 7)
    private List<String> useRestrictions = null;
    @JsonProperty
    @ApiModelProperty(position = 8)
    private List<SearchResultRow> searchResultRows = new ArrayList<>();
    @JsonProperty
    @ApiModelProperty(position = 9)
    private Integer totalPageCount = 0;

    @JsonProperty
    @ApiModelProperty(position = 10)
    private String totalBibRecordsCount = "0";

    @JsonProperty
    @ApiModelProperty(position = 11)
    private String totalItemRecordsCount = "0";
    @JsonProperty
    @ApiModelProperty(position = 12)
    private String totalRecordsCount = "0";

    @JsonProperty
    @ApiModelProperty(position = 13)
    private Integer pageNumber = 0;

    @JsonProperty
    @ApiModelProperty(position = 14)
    private Integer pageSize = 10;

    @JsonProperty
    @ApiModelProperty(position = 15)
    private boolean showResults = false;
    @JsonProperty
    @ApiModelProperty(position = 16)
    private boolean selectAll = false;
    @JsonProperty
    @ApiModelProperty(position = 17)
    private boolean selectAllFacets = false;
    @JsonProperty
    @ApiModelProperty(position = 18)
    private boolean showTotalCount = false;

    @JsonProperty
    @ApiModelProperty(position = 19)
    private Integer index;

    @ApiModelProperty(position = 20)
    private String errorMessage;

    public SearchRecordsRequest() {
        this.setFieldName("");
        this.setFieldValue("");
        this.setSelectAllFacets(true);

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
        return fieldName;
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

    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }
}
