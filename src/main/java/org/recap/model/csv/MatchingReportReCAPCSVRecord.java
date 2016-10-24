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

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getVolumePartYear() {
        return volumePartYear;
    }

    public void setVolumePartYear(String volumePartYear) {
        this.volumePartYear = volumePartYear;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getOclc() {
        return oclc;
    }

    public void setOclc(String oclc) {
        this.oclc = oclc;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getLccn() {
        return lccn;
    }

    public void setLccn(String lccn) {
        this.lccn = lccn;
    }

    public String getUseRestrictions() {
        return useRestrictions;
    }

    public void setUseRestrictions(String useRestrictions) {
        this.useRestrictions = useRestrictions;
    }

    public String getSummaryHoldings() {
        return summaryHoldings;
    }

    public void setSummaryHoldings(String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
    }

    public String getLocalBibId() {
        return localBibId;
    }

    public void setLocalBibId(String localBibId) {
        this.localBibId = localBibId;
    }

    public String getTitleWithoutSymbols() {
        return titleWithoutSymbols;
    }

    public void setTitleWithoutSymbols(String titleWithoutSymbols) {
        this.titleWithoutSymbols = titleWithoutSymbols;
    }
}
