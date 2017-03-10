package org.recap.model.accession;

/**
 * Created by chenchulakshmig on 19/10/16.
 */
public class AccessionRequest {
    private String itemBarcode;
    private String customerCode;

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
