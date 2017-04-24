package org.recap.model.camel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rajeshbabuk on 19/1/17.
 */
public class EmailPayLoad implements Serializable {

    private String itemBarcode;
    private String itemInstitution;
    private String oldCgd;
    private String newCgd;
    private String notes;
    private String jobName;
    private String jobDescription;
    private Date startDate;
    private String status;

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
