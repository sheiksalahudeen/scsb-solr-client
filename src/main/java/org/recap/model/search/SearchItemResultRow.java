package org.recap.model.search;

import java.util.Comparator;

/**
 * Created by rajesh on 18-Jul-16.
 */
public class SearchItemResultRow implements Comparable<SearchItemResultRow> {

    private String callNumber;
    private String chronologyAndEnum;
    private String customerCode;
    private String barcode;
    private String useRestriction;
    private String collectionGroupDesignation;
    private String availability;
    private boolean selectedItem = false;

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getChronologyAndEnum() {
        return chronologyAndEnum;
    }

    public void setChronologyAndEnum(String chronologyAndEnum) {
        this.chronologyAndEnum = chronologyAndEnum;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUseRestriction() {
        return useRestriction;
    }

    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public boolean isSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(boolean selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    public int compareTo(SearchItemResultRow searchItemResultRow) {
        return this.getChronologyAndEnum().compareTo(searchItemResultRow.getChronologyAndEnum());
    }
}
