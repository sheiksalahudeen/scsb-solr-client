package org.recap.model.accession;

/**
 * Created by premkb on 8/3/17.
 */
public class AccessionResponse {

    private String itemBarcode;

    private String message;

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
