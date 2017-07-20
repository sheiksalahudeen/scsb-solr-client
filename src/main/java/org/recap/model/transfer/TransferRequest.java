package org.recap.model.transfer;

import java.util.List;

/**
 * Created by sheiks on 12/07/17.
 */
public class TransferRequest {
    private String institution;
    private List<HoldingsTransferRequest> holdingTransfers;
    private List<ItemTransferRequest> itemTransfers;

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public List<HoldingsTransferRequest> getHoldingTransfers() {
        return holdingTransfers;
    }

    public void setHoldingTransfers(List<HoldingsTransferRequest> holdingTransfers) {
        this.holdingTransfers = holdingTransfers;
    }

    public List<ItemTransferRequest> getItemTransfers() {
        return itemTransfers;
    }

    public void setItemTransfers(List<ItemTransferRequest> itemTransfers) {
        this.itemTransfers = itemTransfers;
    }
}
