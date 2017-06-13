package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 2/1/17.
 */
public class SearchRecordsResponse {

    private List<SearchResultRow> searchResultRows = new ArrayList<>();
    private Integer totalPageCount = 0;
    private String totalBibRecordsCount = "0";
    private String totalItemRecordsCount = "0";
    private String totalRecordsCount = "0";
    private boolean showTotalCount;
    private String errorMessage;

    /**
     * Gets search result rows.
     *
     * @return the search result rows
     */
    public List<SearchResultRow> getSearchResultRows() {
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
}
