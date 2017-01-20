package org.recap.model.camel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rajeshbabuk on 19/1/17.
 */
public class EmailPayLoad implements Serializable {

    private String itemBarcode;
    private String itemInstitution;
    private String oldCgd;
    private String newCgd;
    private String notes;

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public String getItemInstitution() {
        return itemInstitution;
    }

    public void setItemInstitution(String itemInstitution) {
        this.itemInstitution = itemInstitution;
    }

    public String getOldCgd() {
        return oldCgd;
    }

    public void setOldCgd(String oldCgd) {
        this.oldCgd = oldCgd;
    }

    public String getNewCgd() {
        return newCgd;
    }

    public void setNewCgd(String newCgd) {
        this.newCgd = newCgd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
