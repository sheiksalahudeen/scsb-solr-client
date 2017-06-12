package org.recap.model.solr;

import java.util.Date;

/**
 * Created by SheikS on 6/18/2016.
 */
public class SolrIndexRequest {
    private String docType;
    private Integer numberOfThreads;
    private Integer numberOfDocs;
    private Integer commitInterval;
    private String owningInstitutionCode;
    private boolean doClean;
    private String dateFrom;

    private String matchingCriteria;
    private String reportType;
    private String transmissionType;
    private Date createdDate;
    private String processType;
    private Date toDate;

    /**
     * Gets doc type.
     *
     * @return the doc type
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets doc type.
     *
     * @param docType the doc type
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets number of threads.
     *
     * @return the number of threads
     */
    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    /**
     * Sets number of threads.
     *
     * @param numberOfThreads the number of threads
     */
    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    /**
     * Gets number of docs.
     *
     * @return the number of docs
     */
    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    /**
     * Sets number of docs.
     *
     * @param numberOfDocs the number of docs
     */
    public void setNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    /**
     * Gets commit interval.
     *
     * @return the commit interval
     */
    public Integer getCommitInterval() {
        return commitInterval;
    }

    /**
     * Sets commit interval.
     *
     * @param commitInterval the commit interval
     */
    public void setCommitInterval(Integer commitInterval) {
        this.commitInterval = commitInterval;
    }

    /**
     * Gets owning institution code.
     *
     * @return the owning institution code
     */
    public String getOwningInstitutionCode() {
        return owningInstitutionCode;
    }

    /**
     * Sets owning institution code.
     *
     * @param owningInstitutionCode the owning institution code
     */
    public void setOwningInstitutionCode(String owningInstitutionCode) {
        this.owningInstitutionCode = owningInstitutionCode;
    }

    /**
     * Is do clean boolean.
     *
     * @return the boolean
     */
    public boolean isDoClean() {
        return doClean;
    }

    /**
     * Sets do clean.
     *
     * @param doClean the do clean
     */
    public void setDoClean(boolean doClean) {
        this.doClean = doClean;
    }

    /**
     * Gets matching criteria.
     *
     * @return the matching criteria
     */
    public String getMatchingCriteria() {
        return matchingCriteria;
    }

    /**
     * Sets matching criteria.
     *
     * @param matchingCriteria the matching criteria
     */
    public void setMatchingCriteria(String matchingCriteria) {
        this.matchingCriteria = matchingCriteria;
    }

    /**
     * Gets report type.
     *
     * @return the report type
     */
    public String getReportType() {
        return reportType;
    }

    /**
     * Sets report type.
     *
     * @param reportType the report type
     */
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Gets transmission type.
     *
     * @return the transmission type
     */
    public String getTransmissionType() {
        return transmissionType;
    }

    /**
     * Sets transmission type.
     *
     * @param transmissionType the transmission type
     */
    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets created date.
     *
     * @param createdDate the created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets date from.
     *
     * @return the date from
     */
    public String getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets date from.
     *
     * @param dateFrom the date from
     */
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Gets process type.
     *
     * @return the process type
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * Sets process type.
     *
     * @param processType the process type
     */
    public void setProcessType(String processType) {
        this.processType = processType;
    }

    /**
     * Gets to date.
     *
     * @return the to date
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets to date.
     *
     * @param toDate the to date
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}
