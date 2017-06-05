package org.recap.model;

/**
 * Created by akulak on 3/3/17.
 */
public class BibItemAvailabityStatusRequest {

    private String bibliographicId;

    private String institutionId;

    /**
     * Gets bibliographic id.
     *
     * @return the bibliographic id
     */
    public String getBibliographicId() {
        return bibliographicId;
    }

    /**
     * Sets bibliographic id.
     *
     * @param bibliographicId the bibliographic id
     */
    public void setBibliographicId(String bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    /**
     * Gets institution id.
     *
     * @return the institution id
     */
    public String getInstitutionId() {
        return institutionId;
    }

    /**
     * Sets institution id.
     *
     * @param institutionId the institution id
     */
    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }
}
