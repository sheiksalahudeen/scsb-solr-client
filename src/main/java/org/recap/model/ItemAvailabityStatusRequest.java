package org.recap.model;

import java.util.List;

/**
 * Created by akulak on 3/3/17.
 */
public class ItemAvailabityStatusRequest {

    List<String> Barcodes;

    public List<String> getBarcodes() {
        return Barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        Barcodes = barcodes;
    }
}
