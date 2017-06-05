package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by hemalathas on 22/11/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class AccessionSummaryRecord implements Serializable{

    @DataField(pos = 1, columnName = "Count of new bibs loaded")
    private String successBibCount;

    @DataField(pos = 2, columnName = "Failed new additions - Bibs")
    private String failedBibCount;

    @DataField(pos = 3, columnName = "Bib-Reason for Failure")
    private String reasonForFailureBib;

    @DataField(pos = 4, columnName = "Number of bibs that match existing from same inst")
    private String noOfBibMatches;

    @DataField(pos = 5, columnName = "Count of New Items Loaded")
    private String successItemCount;

    @DataField(pos = 6, columnName = "Failed new additions - Items")
    private String failedItemCount;

    @DataField(pos = 7, columnName = "Item-Reason for Failure")
    private String reasonForFailureItem;


    /**
     * Gets success bib count.
     *
     * @return the success bib count
     */
    public String getSuccessBibCount() {
        return successBibCount;
    }

    /**
     * Sets success bib count.
     *
     * @param successBibCount the success bib count
     */
    public void setSuccessBibCount(String successBibCount) {
        this.successBibCount = successBibCount;
    }

    /**
     * Gets failed bib count.
     *
     * @return the failed bib count
     */
    public String getFailedBibCount() {
        return failedBibCount;
    }

    /**
     * Sets failed bib count.
     *
     * @param failedBibCount the failed bib count
     */
    public void setFailedBibCount(String failedBibCount) {
        this.failedBibCount = failedBibCount;
    }

    /**
     * Gets no of bib matches.
     *
     * @return the no of bib matches
     */
    public String getNoOfBibMatches() {
        return noOfBibMatches;
    }

    /**
     * Sets no of bib matches.
     *
     * @param noOfBibMatches the no of bib matches
     */
    public void setNoOfBibMatches(String noOfBibMatches) {
        this.noOfBibMatches = noOfBibMatches;
    }

    /**
     * Gets success item count.
     *
     * @return the success item count
     */
    public String getSuccessItemCount() {
        return successItemCount;
    }

    /**
     * Sets success item count.
     *
     * @param successItemCount the success item count
     */
    public void setSuccessItemCount(String successItemCount) {
        this.successItemCount = successItemCount;
    }

    /**
     * Gets failed item count.
     *
     * @return the failed item count
     */
    public String getFailedItemCount() {
        return failedItemCount;
    }

    /**
     * Sets failed item count.
     *
     * @param failedItem the failed item
     */
    public void setFailedItemCount(String failedItem) {
        this.failedItemCount = failedItem;
    }

    /**
     * Gets reason for failure bib.
     *
     * @return the reason for failure bib
     */
    public String getReasonForFailureBib() {
        return reasonForFailureBib;
    }

    /**
     * Sets reason for failure bib.
     *
     * @param reasonForFailureBib the reason for failure bib
     */
    public void setReasonForFailureBib(String reasonForFailureBib) {
        this.reasonForFailureBib = reasonForFailureBib;
    }

    /**
     * Gets reason for failure item.
     *
     * @return the reason for failure item
     */
    public String getReasonForFailureItem() {
        return reasonForFailureItem;
    }

    /**
     * Sets reason for failure item.
     *
     * @param reasonForFailureItem the reason for failure item
     */
    public void setReasonForFailureItem(String reasonForFailureItem) {
        this.reasonForFailureItem = reasonForFailureItem;
    }
}
