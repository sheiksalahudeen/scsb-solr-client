package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 11/7/16.
 */
public class SearchResultRow {

    private String title;
    private String author;
    private String publisher;
    private String publisherDate;
    private String owningInstitution;
    private String customerCode;
    private String collectionGroupDesignation;
    private String useRestriction;
    private String barcode;
    private String summaryHoldings;
    private String availability;
    private String leaderMaterialType;
    private boolean selected = false;
    private boolean showItems = false;
    private boolean selectAllItems = false;
    private List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherDate() {
        return publisherDate;
    }

    public void setPublisherDate(String publisherDate) {
        this.publisherDate = publisherDate;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    public String getUseRestriction() {
        return useRestriction;
    }

    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSummaryHoldings() {
        return summaryHoldings;
    }

    public void setSummaryHoldings(String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLeaderMaterialType() {
        return leaderMaterialType;
    }

    public void setLeaderMaterialType(String leaderMaterialType) {
        this.leaderMaterialType = leaderMaterialType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }

    public List<SearchItemResultRow> getSearchItemResultRows() {
        return searchItemResultRows;
    }

    public void setSearchItemResultRows(List<SearchItemResultRow> searchItemResultRows) {
        this.searchItemResultRows = searchItemResultRows;
    }

    public boolean isSelectAllItems() {
        return selectAllItems;
    }

    public void setSelectAllItems(boolean selectAllItems) {
        this.selectAllItems = selectAllItems;
    }
}