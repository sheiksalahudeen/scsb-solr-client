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

    /**
     * Gets doc type.
     *
     * @return the doc type
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets doc type.
     *
     * @param docType the doc type
     */
    public void setDocType(String docType) {
        this.docType = docType;
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
     * Gets holdings id.
     *
     * @return the holdings id
     */
    public String getHoldingsId() {
        return holdingsId;
    }

    /**
     * Sets holdings id.
     *
     * @param holdingsId the holdings id
     */
    public void setHoldingsId(String holdingsId) {
        this.holdingsId = holdingsId;
    }

    /**
     * Gets item id.
     *
     * @return the item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Sets item id.
     *
     * @param itemId the item id
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets exception message.
     *
     * @return the exception message
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * Sets exception message.
     *
     * @param exceptionMessage the exception message
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
