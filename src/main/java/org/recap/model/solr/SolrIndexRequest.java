package org.recap.model.solr;

/**
 * Created by SheikS on 6/18/2016.
 */
public class SolrIndexRequest {
    private Integer numberOfThreads;
    private Integer numberOfDocs;
    private Integer owningInstitutionId;
    private boolean doClean;

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

    public Integer getOwningInstitutionId() {
        return owningInstitutionId;
    }

    public void setOwningInstitutionId(Integer owningInstitutionId) {
        this.owningInstitutionId = owningInstitutionId;
    }

    public boolean isDoClean() {
        return doClean;
    }

    public void setDoClean(boolean doClean) {
        this.doClean = doClean;
    }
}
