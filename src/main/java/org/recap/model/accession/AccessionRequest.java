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

        if (itemBarcode != null ? !itemBarcode.equals(that.itemBarcode) : that.itemBarcode != null) return false;
        return customerCode != null ? customerCode.equals(that.customerCode) : that.customerCode == null;
    }

    @Override
    public int hashCode() {
        int result = itemBarcode != null ? itemBarcode.hashCode() : 0;
        result = 31 * result + (customerCode != null ? customerCode.hashCode() : 0);
        return result;
    }
}
