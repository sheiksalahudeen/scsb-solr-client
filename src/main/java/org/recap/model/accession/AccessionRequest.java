package org.recap.model.accession;

/**
 * Created by chenchulakshmig on 19/10/16.
 */
public class AccessionRequest {
    private String itemBarcode;
    private String customerCode;

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
     * This method gets customer code.
     *
     * @return the customer code
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * This method sets customer code.
     *
     * @param customerCode the customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessionRequest that = (AccessionRequest) o;

        return itemBarcode != null ? itemBarcode.equals(that.itemBarcode) : that.itemBarcode == null;
    }

    @Override
    public int hashCode() {
        return itemBarcode != null ? itemBarcode.hashCode() : 0;
    }
}
