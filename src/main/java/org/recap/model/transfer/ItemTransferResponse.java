package org.recap.model.transfer;

/**
 * Created by sheiks on 13/07/17.
 */
public class ItemTransferResponse {
    private String message;
    private ItemTransferRequest itemTransferRequest;

    public ItemTransferResponse() {
    }

    public ItemTransferResponse(String message, ItemTransferRequest itemTransferRequest) {
        this.message = message;
        this.itemTransferRequest = itemTransferRequest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ItemTransferRequest getItemTransferRequest() {
        return itemTransferRequest;
    }

    public void setItemTransferRequest(ItemTransferRequest itemTransferRequest) {
        this.itemTransferRequest = itemTransferRequest;
    }
}
