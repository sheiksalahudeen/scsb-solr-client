package org.recap.model;

/**
 * Created by akulak on 3/3/17.
 */
public class ItemAvailabilityResponse {
    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String errorMessage;

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getItemAvailabilityStatus() {
        return itemAvailabilityStatus;
    }

    public void setItemAvailabilityStatus(String itemAvailabilityStatus) {
        this.itemAvailabilityStatus = itemAvailabilityStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
