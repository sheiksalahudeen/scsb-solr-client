package org.recap.model.search;

/**
 * Created by rajesh on 18-Jul-16.
 */
public class SearchItemResultRow {

    private String callNumber;
    private String chronologyAndEnum;
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
}
