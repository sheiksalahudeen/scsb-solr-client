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

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Integer getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(Integer numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public Integer getNumberOfDocs() {
        return numberOfDocs;
    }

    public void setNumberOfDocs(Integer numberOfDocs) {
        this.numberOfDocs = numberOfDocs;
    }

    public Integer getCommitInterval() {
        return commitInterval;
    }

    public void setCommitInterval(Integer commitInterval) {
        this.commitInterval = commitInterval;
    }

    public String getOwningInstitutionCode() {
        return owningInstitutionCode;
    }

    public void setOwningInstitutionCode(String owningInstitutionCode) {
        this.owningInstitutionCode = owningInstitutionCode;
    }

    public boolean isDoClean() {
        return doClean;
    }

    public void setDoClean(boolean doClean) {
        this.doClean = doClean;
    }

    public String getMatchingCriteria() {
        return matchingCriteria;
    }

    public void setMatchingCriteria(String matchingCriteria) {
        this.matchingCriteria = matchingCriteria;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }
}
