package org.recap.model.transfer;

import java.util.List;

/**
 * Created by sheiks on 12/07/17.
 */
public class TransferResponse {
    private String message;
    private List<HoldingTransferResponse> holdingTransferResponses;
    private List<ItemTransferResponse> itemTransferResponses;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HoldingTransferResponse> getHoldingTransferResponses() {
        return holdingTransferResponses;
    }

    public void setHoldingTransferResponses(List<HoldingTransferResponse> holdingTransferResponses) {
        this.holdingTransferResponses = holdingTransferResponses;
    }

    public List<ItemTransferResponse> getItemTransferResponses() {
        return itemTransferResponses;
    }

    public void setItemTransferResponses(List<ItemTransferResponse> itemTransferResponses) {
        this.itemTransferResponses = itemTransferResponses;
    }
}
