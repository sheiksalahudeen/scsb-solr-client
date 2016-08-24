package org.recap.model.csv;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * Created by angelind on 23/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class ExceptionReportReCAPCSVRecord implements Comparable<ExceptionReportReCAPCSVRecord>{

    @DataField(pos = 1)
    private String matchingPointTag;
    @DataField(pos = 2)
    private String matchPointContent;
    @DataField(pos = 3)
    private String bibId;
    @DataField(pos = 4)
    private String title;
    @DataField(pos = 5)
    private String barcode;
    @DataField(pos = 6)
    private String institutionId;
    @DataField(pos = 7)
    private String useRestrictions;
    @DataField(pos = 8)
    private String summaryHoldings;

    @Ignore
    private String localBibId;

    public String getMatchingPointTag() {
        return matchingPointTag;
    }

    public void setMatchingPointTag(String matchingPointTag) {
        this.matchingPointTag = matchingPointTag;
    }

    public String getMatchPointContent() {
        return matchPointContent;
    }

    public void setMatchPointContent(String matchPointContent) {
        this.matchPointContent = matchPointContent;
    }

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

    public int compareTo(ExceptionReportReCAPCSVRecord exceptionReportReCAPCSVRecord) {
        return title.compareToIgnoreCase(exceptionReportReCAPCSVRecord.getTitle());
    }
}
