package org.recap.model.reports;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
public class ReportsRequest {

    private String accessionDeaccessionFromDate;
    private String accessionDeaccessionToDate;
    private List<String> owningInstitutions = new ArrayList<>();
    private List<String> collectionGroupDesignations = new ArrayList<>();
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private String deaccessionOwningInstitution;
    //IncompleteRecordsReport
    private String incompleteRequestingInstitution;
    private Integer incompletePageNumber = 0;
    private Integer incompletePageSize = 10;
    private boolean export;

    /**
     * Gets accession deaccession from date.
     *
     * @return the accession deaccession from date
     */
    public String getAccessionDeaccessionFromDate() {
        return accessionDeaccessionFromDate;
    }

    /**
     * Sets accession deaccession from date.
     *
     * @param accessionDeaccessionFromDate the accession deaccession from date
     */
    public void setAccessionDeaccessionFromDate(String accessionDeaccessionFromDate) {
        this.accessionDeaccessionFromDate = accessionDeaccessionFromDate;
    }

    /**
     * Gets accession deaccession to date.
     *
     * @return the accession deaccession to date
     */
    public String getAccessionDeaccessionToDate() {
        return accessionDeaccessionToDate;
    }

    /**
     * Sets accession deaccession to date.
     *
     * @param accessionDeaccessionToDate the accession deaccession to date
     */
    public void setAccessionDeaccessionToDate(String accessionDeaccessionToDate) {
        this.accessionDeaccessionToDate = accessionDeaccessionToDate;
    }

    /**
     * Gets owning institutions.
     *
     * @return the owning institutions
     */
    public List<String> getOwningInstitutions() {
        return owningInstitutions;
    }

    /**
     * Sets owning institutions.
     *
     * @param owningInstitutions the owning institutions
     */
    public void setOwningInstitutions(List<String> owningInstitutions) {
        this.owningInstitutions = owningInstitutions;
    }

    /**
     * Gets collection group designations.
     *
     * @return the collection group designations
     */
    public List<String> getCollectionGroupDesignations() {
        return collectionGroupDesignations;
    }

    /**
     * Sets collection group designations.
     *
     * @param collectionGroupDesignations the collection group designations
     */
    public void setCollectionGroupDesignations(List<String> collectionGroupDesignations) {
        this.collectionGroupDesignations = collectionGroupDesignations;
    }

    /**
     * Gets page number.
     *
     * @return the page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets page number.
     *
     * @param pageNumber the page number
     */
    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets page size.
     *
     * @return the page size
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Sets page size.
     *
     * @param pageSize the page size
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Gets deaccession owning institution.
     *
     * @return the deaccession owning institution
     */
    public String getDeaccessionOwningInstitution() {
        return deaccessionOwningInstitution;
    }

    /**
     * Sets deaccession owning institution.
     *
     * @param deaccessionOwningInstitution the deaccession owning institution
     */
    public void setDeaccessionOwningInstitution(String deaccessionOwningInstitution) {
        this.deaccessionOwningInstitution = deaccessionOwningInstitution;
    }

    /**
     * Gets incomplete requesting institution.
     *
     * @return the incomplete requesting institution
     */
    public String getIncompleteRequestingInstitution() {
        return incompleteRequestingInstitution;
    }

    /**
     * Sets incomplete requesting institution.
     *
     * @param incompleteRequestingInstitution the incomplete requesting institution
     */
    public void setIncompleteRequestingInstitution(String incompleteRequestingInstitution) {
        this.incompleteRequestingInstitution = incompleteRequestingInstitution;
    }

    /**
     * Gets incomplete page number.
     *
     * @return the incomplete page number
     */
    public Integer getIncompletePageNumber() {
        return incompletePageNumber;
    }

    /**
     * Sets incomplete page number.
     *
     * @param incompletePageNumber the incomplete page number
     */
    public void setIncompletePageNumber(Integer incompletePageNumber) {
        this.incompletePageNumber = incompletePageNumber;
    }

    /**
     * Gets incomplete page size.
     *
     * @return the incomplete page size
     */
    public Integer getIncompletePageSize() {
        return incompletePageSize;
    }

    /**
     * Sets incomplete page size.
     *
     * @param incompletePageSize the incomplete page size
     */
    public void setIncompletePageSize(Integer incompletePageSize) {
        this.incompletePageSize = incompletePageSize;
    }

    /**
     * Is export boolean.
     *
     * @return the boolean
     */
    public boolean isExport() {
        return export;
    }

    /**
     * Sets export.
     *
     * @param export the export
     */
    public void setExport(boolean export) {
        this.export = export;
    }
}
