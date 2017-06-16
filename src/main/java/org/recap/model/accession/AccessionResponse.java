package org.recap.model.accession;

/**
 * Created by premkb on 8/3/17.
 */
public class AccessionResponse {

    private String itemBarcode;

    private String message;

    /**
     * This method gets item barcode.
     *
     * @return the item barcode
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * This method sets item barcode.
     *
     * @param itemBarcode the item barcode
     */
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    /**
     * This method gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessionResponse that = (AccessionResponse) o;

        return itemBarcode != null ? itemBarcode.equals(that.itemBarcode) : that.itemBarcode == null;
    }

    @Override
    public int hashCode() {
        return itemBarcode != null ? itemBarcode.hashCode() : 0;
    }
}
