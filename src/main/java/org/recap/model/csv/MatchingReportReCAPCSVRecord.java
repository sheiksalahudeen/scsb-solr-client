package org.recap.model.csv;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 22/8/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class MatchingReportReCAPCSVRecord implements Serializable{

    @DataField(pos = 1, columnName = "Bib Id")
    private String bibId;
    @DataField(pos = 2, columnName = "Title")
    private String title;
    @DataField(pos = 3, columnName = "Barcode")
    private String barcode;
    @DataField(pos = 4, columnName = "Volume Part Year")
    private String volumePartYear;
    @DataField(pos = 5, columnName = "Institution Id")
    private String institutionId;
    @DataField(pos = 6, columnName = "OCLC")
    private String oclc;
    @DataField(pos = 7, columnName = "ISBN")
    private String isbn;
    @DataField(pos = 8, columnName = "ISSN")
    private String issn;
    @DataField(pos = 9, columnName = "LCCN")
    private String lccn;
    @DataField(pos = 10, columnName = "Use Restrictions")
    private String useRestrictions;
    @DataField(pos = 11, columnName = "Summary Holdings")
    private String summaryHoldings;

    @Ignore
    private String titleWithoutSymbols;

    @Ignore
    private String localBibId;

    @Ignore
    private String materialType;

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public String getBibId() {
        return bibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(String bibId) {
        this.bibId = bibId;
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

    /**
     * Gets oclc.
     *
     * @return the oclc
     */
    public String getOclc() {
        return oclc;
    }

    /**
     * Sets oclc.
     *
     * @param oclc the oclc
     */
    public void setOclc(String oclc) {
        this.oclc = oclc;
    }

    /**
     * Gets isbn.
     *
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets issn.
     *
     * @return the issn
     */
    public String getIssn() {
        return issn;
    }

    /**
     * Sets issn.
     *
     * @param issn the issn
     */
    public void setIssn(String issn) {
        this.issn = issn;
    }

    /**
     * Gets lccn.
     *
     * @return the lccn
     */
    public String getLccn() {
        return lccn;
    }

    /**
     * Sets lccn.
     *
     * @param lccn the lccn
     */
    public void setLccn(String lccn) {
        this.lccn = lccn;
    }

    /**
     * Gets use restrictions.
     *
     * @return the use restrictions
     */
    public String getUseRestrictions() {
        return useRestrictions;
    }

    /**
     * Sets use restrictions.
     *
     * @param useRestrictions the use restrictions
     */
    public void setUseRestrictions(String useRestrictions) {
        this.useRestrictions = useRestrictions;
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
     * Gets local bib id.
     *
     * @return the local bib id
     */
    public String getLocalBibId() {
        return localBibId;
    }

    /**
     * Sets local bib id.
     *
     * @param localBibId the local bib id
     */
    public void setLocalBibId(String localBibId) {
        this.localBibId = localBibId;
    }

    /**
     * Gets title without symbols.
     *
     * @return the title without symbols
     */
    public String getTitleWithoutSymbols() {
        return titleWithoutSymbols;
    }

    /**
     * Sets title without symbols.
     *
     * @param titleWithoutSymbols the title without symbols
     */
    public void setTitleWithoutSymbols(String titleWithoutSymbols) {
        this.titleWithoutSymbols = titleWithoutSymbols;
    }

    /**
     * Gets material type.
     *
     * @return the material type
     */
    public String getMaterialType() {
        return materialType;
    }

    /**
     * Sets material type.
     *
     * @param materialType the material type
     */
    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }
}
