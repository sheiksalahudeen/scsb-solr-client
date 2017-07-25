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
    private String message;
    private String from;
    private String to;
    private String cc;
    private String subject;

    /**
     * Gets item barcode.
     *
     * @return the item barcode
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * Sets item barcode.
     *
     * @param itemBarcode the item barcode
     */
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    /**
     * Gets item institution.
     *
     * @return the item institution
     */
    public String getItemInstitution() {
        return itemInstitution;
    }

    /**
     * Sets item institution.
     *
     * @param itemInstitution the item institution
     */
    public void setItemInstitution(String itemInstitution) {
        this.itemInstitution = itemInstitution;
    }

    /**
     * Gets old cgd.
     *
     * @return the old cgd
     */
    public String getOldCgd() {
        return oldCgd;
    }

    /**
     * Sets old cgd.
     *
     * @param oldCgd the old cgd
     */
    public void setOldCgd(String oldCgd) {
        this.oldCgd = oldCgd;
    }

    /**
     * Gets new cgd.
     *
     * @return the new cgd
     */
    public String getNewCgd() {
        return newCgd;
    }

    /**
     * Sets new cgd.
     *
     * @param newCgd the new cgd
     */
    public void setNewCgd(String newCgd) {
        this.newCgd = newCgd;
    }

    /**
     * Gets notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets notes.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets job name.
     *
     * @return the job name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets job name.
     *
     * @param jobName the job name
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets job description.
     *
     * @return the job description
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Sets job description.
     *
     * @param jobDescription the job description
     */
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets from.
     *
     * @param from the from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets to.
     *
     * @return the to
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets to.
     *
     * @param to the to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets cc.
     *
     * @return the cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * Sets cc.
     *
     * @param cc the cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets subject.
     *
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
