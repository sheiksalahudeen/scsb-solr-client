package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 30/9/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class SolrExceptionReportReCAPCSVRecord implements Serializable{

    @DataField(pos = 1, columnName = "Document Type")
    private String docType;
    @DataField(pos = 2, columnName = "Owning Institution")
    private String owningInstitution;
    @DataField(pos = 3, columnName = "Owning Institution BibId")
    private String owningInstitutionBibId;
    @DataField(pos = 4, columnName = "Bib Id")
    private String bibId;
    @DataField(pos = 5, columnName = "Holdings Id")
    private String holdingsId;
    @DataField(pos = 6, columnName = "Item Id")
    private String itemId;
    @DataField(pos = 7, columnName = "Exception Message")
    private String exceptionMessage;

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getHoldingsId() {
        return holdingsId;
    }

    public void setHoldingsId(String holdingsId) {
        this.holdingsId = holdingsId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
