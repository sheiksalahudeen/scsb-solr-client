package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
public class SearchRecordsRequest {

    private String fieldValue = "";
    private String fieldName;
    private List<String> owningInstitutions = null;
    private List<String> collectionGroupDesignations = null;
    private List<String> availability = null;
    private List<String> materialTypes = null;
    private List<String> useRestrictions = null;
    private List<SearchResultRow> searchResultRows = new ArrayList<>();

    private Integer totalPageCount = 0;
    private String totalBibRecordsCount = "0";
    private String totalItemRecordsCount = "0";
    private Integer pageNumber = 0;
    private Integer pageSize = 10;

    private Boolean loopedPreviously = false;

    private boolean showResults = false;
    private boolean selectAll = false;
    private boolean selectAllFacets = false;

    private Integer index;
    private String errorMessage;
    private String operationType;

    public SearchRecordsRequest() {
        this.setFieldName("");
        this.setFieldValue("");
        this.setSelectAllFacets(true);
        this.setOperationType("");
        this.setLoopedPreviously(false);

        this.getOwningInstitutions().add("NYPL");
        this.getOwningInstitutions().add("CUL");
        this.getOwningInstitutions().add("PUL");

        this.getCollectionGroupDesignations().add("Shared");
        this.getCollectionGroupDesignations().add("Private");
        this.getCollectionGroupDesignations().add("Open");

        this.getAvailability().add("Available");
        this.getAvailability().add("Not Available");

        this.getMaterialTypes().add("Monograph");
        this.getMaterialTypes().add("Serial");
        this.getMaterialTypes().add("Other");

        this.getUseRestrictions().add("No Restrictions");
        this.getUseRestrictions().add("In Library Use");
        this.getUseRestrictions().add("Supervised Use");

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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean loopedPreviously() {
        return loopedPreviously;
    }

    public void setLoopedPreviously(Boolean loopedPreviously) {
        this.loopedPreviously = loopedPreviously;
    }
}
