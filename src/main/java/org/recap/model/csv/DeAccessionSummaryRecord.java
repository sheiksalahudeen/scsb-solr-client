package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by chenchulakshmig on 30/9/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class DeAccessionSummaryRecord implements Serializable {

    @DataField(pos = 1, columnName = "Date of DeAccession")
    private String dateOfDeAccession;

    @DataField(pos = 2, columnName = "Owning Institution")
    private String owningInstitution;

    @DataField(pos = 3, columnName = "Barcode")
    private String barcode;

    @DataField(pos = 4, columnName = "Owning Inst Bib ID")
    private String owningInstitutionBibId;

    @DataField(pos = 5, columnName = "Title")
    private String title;

    @DataField(pos = 6, columnName = "CGD")
    private String collectionGroupCode;

    @DataField(pos = 7, columnName = "Item DeAccession Status")
    private String status;

    @DataField(pos = 8, columnName = "Reason for failure")
    private String reasonForFailure;

    /**
     * Gets date of deaccession.
     *
     * @return the date of de accession
     */
    public String getDateOfDeAccession() {
        return dateOfDeAccession;
    }

    /**
     * Sets date of deaccession.
     *
     * @param dateOfDeAccession the date of de accession
     */
    public void setDateOfDeAccession(String dateOfDeAccession) {
        this.dateOfDeAccession = dateOfDeAccession;
    }

    /**
     * Gets owning institution.
     *
     * @return the owning institution
     */
    public String getOwningInstitution() {
        return owningInstitution;
    }

    /**
     * Sets owning institution.
     *
     * @param owningInstitution the owning institution
     */
    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    /**
     * Gets barcode.
     *
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets barcode.
     *
     * @param barcode the barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Gets owning institution bib id.
     *
     * @return the owning institution bib id
     */
    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    /**
     * Sets owning institution bib id.
     *
     * @param owningInstitutionBibId the owning institution bib id
     */
    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets collection group code.
     *
     * @return the collection group code
     */
    public String getCollectionGroupCode() {
        return collectionGroupCode;
    }

    /**
     * Sets collection group code.
     *
     * @param collectionGroupCode the collection group code
     */
    public void setCollectionGroupCode(String collectionGroupCode) {
        this.collectionGroupCode = collectionGroupCode;
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
     * Gets reason for failure.
     *
     * @return the reason for failure
     */
    public String getReasonForFailure() {
        return reasonForFailure;
    }

    /**
     * Sets reason for failure.
     *
     * @param reasonForFailure the reason for failure
     */
    public void setReasonForFailure(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }
}
