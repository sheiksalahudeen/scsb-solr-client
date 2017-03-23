package org.recap.model;

import java.util.List;

/**
 * Created by akulak on 3/3/17.
 */
public class ItemAvailabityStatusRequest {

    List<String> barcodes;

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }
}
