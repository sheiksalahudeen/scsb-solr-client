package org.recap.model.transfer;

import java.util.List;

/**
 * Created by sheiks on 13/07/17.
 */
public class ItemSource extends Source {
    private String owningInstitutionItemId;

    public String getOwningInstitutionItemId() {
        return owningInstitutionItemId;
    }

    public void setOwningInstitutionItemId(String owningInstitutionItemId) {
        this.owningInstitutionItemId = owningInstitutionItemId;
    }
}
