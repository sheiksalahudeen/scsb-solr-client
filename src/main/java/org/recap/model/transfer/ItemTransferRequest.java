package org.recap.model.transfer;

import java.util.List;

/**
 * Created by sheiks on 13/07/17.
 */
public class ItemTransferRequest {
    private ItemSource source;
    private ItemDestination destination;

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
