package org.recap.model;

/**
 * Created by akulak on 3/3/17.
 */
public class ItemAvailabilityResponse {
    private String itemBarcode;
    private String itemAvailabilityStatus;
    private String errorMessage;

    /**
     * Gets item barcode.
     *
     * @return the item barcode
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * Sets item barcode.
     *
     * @param itemBarcode the item barcode
     */
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    /**
     * Gets item availability status.
     *
     * @return the item availability status
     */
    public String getItemAvailabilityStatus() {
        return itemAvailabilityStatus;
    }

    /**
     * Sets item availability status.
     *
     * @param itemAvailabilityStatus the item availability status
     */
    public void setItemAvailabilityStatus(String itemAvailabilityStatus) {
        this.itemAvailabilityStatus = itemAvailabilityStatus;
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
