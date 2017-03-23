package org.recap.model;

/**
 * Created by akulak on 3/3/17.
 */
public class BibItemAvailabityStatusRequest {

    String bibliographicId;
    String institutionId;

    public String getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(String bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }
}
