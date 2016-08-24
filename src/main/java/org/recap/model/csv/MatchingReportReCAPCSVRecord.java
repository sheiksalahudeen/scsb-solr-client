package org.recap.model.csv;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * Created by angelind on 22/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class MatchingReportReCAPCSVRecord implements Comparable<MatchingReportReCAPCSVRecord>{

    @DataField(pos = 1)
    private String bibId;
    @DataField(pos = 2)
    private String title;
    @DataField(pos = 3)
    private String barcode;
    @DataField(pos = 4)
    private String institutionId;
    @DataField(pos = 5)
    private String oclc;
    @DataField(pos = 6)
    private String isbn;
    @DataField(pos = 7)
    private String issn;
    @DataField(pos = 8)
    private String lccn;
    @DataField(pos = 9)
    private String useRestrictions;
    @DataField(pos = 10)
    private String summaryHoldings;

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

    public int compareTo(MatchingReportReCAPCSVRecord matchingReportReCAPCSVRecord) {
        return title.compareToIgnoreCase(matchingReportReCAPCSVRecord.getTitle());
    }
}
