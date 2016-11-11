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

    public String getDateOfDeAccession() {
        return dateOfDeAccession;
    }

    public void setDateOfDeAccession(String dateOfDeAccession) {
        this.dateOfDeAccession = dateOfDeAccession;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCollectionGroupCode() {
        return collectionGroupCode;
    }

    public void setCollectionGroupCode(String collectionGroupCode) {
        this.collectionGroupCode = collectionGroupCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReasonForFailure() {
        return reasonForFailure;
    }

    public void setReasonForFailure(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }
}
