package org.recap.model.transfer;

import java.util.List;

/**
 * Created by sheiks on 13/07/17.
 */
public class ItemTransferRequest {
    private String barcode;
    private ItemSource source;
    private ItemDestination destination;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ItemSource getSource() {
        return source;
    }

    public void setSource(ItemSource source) {
        this.source = source;
    }

    public ItemDestination getDestination() {
        return destination;
    }

    public void setDestination(ItemDestination destination) {
        this.destination = destination;
    }
}
