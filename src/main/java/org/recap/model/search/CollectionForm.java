package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 12/10/16.
 */
public class CollectionForm {

    private String itemBarcodes;
    private boolean showResults = false;
    private boolean selectAll = false;
    private String errorMessage;
    private List<SearchResultRow> searchResultRows = new ArrayList<>();

    public String getItemBarcodes() {
        return itemBarcodes;
    }

    public void setItemBarcodes(String itemBarcodes) {
        this.itemBarcodes = itemBarcodes;
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

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
}
