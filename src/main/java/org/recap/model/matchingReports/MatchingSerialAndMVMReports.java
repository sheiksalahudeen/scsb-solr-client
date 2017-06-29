package org.recap.model.matchingReports;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 22/6/17.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class MatchingSerialAndMVMReports implements Serializable{

    @DataField(pos = 1, columnName = "OwningInstitutionId")
    private String owningInstitutionId;

    @DataField(pos = 2, columnName = "Title")
    private String title;

    @DataField(pos = 3, columnName = "Summary Holdings")
    private String summaryHoldings;

    @DataField(pos = 4, columnName = "Volume Part Year")
    private String volumePartYear;

    @DataField(pos = 5, columnName = "Use Restriction")
    private String useRestriction;

    @DataField(pos = 6, columnName = "BibId")
    private String BibId;

    @DataField(pos = 7, columnName = "OwningInstitutionBibId")
    private String owningInstitutionBibId;

    @DataField(pos = 8, columnName = "Barcode")
    private String barcode;

    /**
     * Gets owning institution id.
     *
     * @return the owning institution id
     */
    public String getOwningInstitutionId() {
        return owningInstitutionId;
    }

    /**
     * Sets owning institution id.
     *
     * @param owningInstitutionId the owning institution id
     */
    public void setOwningInstitutionId(String owningInstitutionId) {
        this.owningInstitutionId = owningInstitutionId;
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
     * Gets summary holdings.
     *
     * @return the summary holdings
     */
    public String getSummaryHoldings() {
        return summaryHoldings;
    }

    /**
     * Sets summary holdings.
     *
     * @param summaryHoldings the summary holdings
     */
    public void setSummaryHoldings(String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
    }

    /**
     * Gets volume part year.
     *
     * @return the volume part year
     */
    public String getVolumePartYear() {
        return volumePartYear;
    }

    /**
     * Sets volume part year.
     *
     * @param volumePartYear the volume part year
     */
    public void setVolumePartYear(String volumePartYear) {
        this.volumePartYear = volumePartYear;
    }

    /**
     * Gets use restriction.
     *
     * @return the use restriction
     */
    public String getUseRestriction() {
        return useRestriction;
    }

    /**
     * Sets use restriction.
     *
     * @param useRestriction the use restriction
     */
    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public String getBibId() {
        return BibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(String bibId) {
        BibId = bibId;
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
}
