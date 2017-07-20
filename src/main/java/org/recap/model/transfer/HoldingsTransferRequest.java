package org.recap.model.transfer;

/**
 * Created by sheiks on 12/07/17.
 */
public class HoldingsTransferRequest {
    private Source source;
    private Destination destination;

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }
}
