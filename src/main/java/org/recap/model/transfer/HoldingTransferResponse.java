package org.recap.model.transfer;

/**
 * Created by sheiks on 12/07/17.
 */
public class HoldingTransferResponse {
    private String message;
    private HoldingsTransferRequest holdingsTransferRequest;

    public HoldingTransferResponse() {
    }

    public HoldingTransferResponse(String message, HoldingsTransferRequest holdingsTransferRequest) {
        this.message = message;
        this.holdingsTransferRequest = holdingsTransferRequest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HoldingsTransferRequest getHoldingsTransferRequest() {
        return holdingsTransferRequest;
    }

    public void setHoldingsTransferRequest(HoldingsTransferRequest holdingsTransferRequest) {
        this.holdingsTransferRequest = holdingsTransferRequest;
    }
}
