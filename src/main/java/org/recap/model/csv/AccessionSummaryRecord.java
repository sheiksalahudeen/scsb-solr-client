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

    @DataField(pos = 8, columnName = "Owning Institution")
    private String owningInstitution;


    public String getSuccessBibCount() {
        return successBibCount;
    }

    public void setSuccessBibCount(String successBibCount) {
        this.successBibCount = successBibCount;
    }

    public String getFailedBibCount() {
        return failedBibCount;
    }

    public void setFailedBibCount(String failedBibCount) {
        this.failedBibCount = failedBibCount;
    }

    public String getNoOfBibMatches() {
        return noOfBibMatches;
    }

    public void setNoOfBibMatches(String noOfBibMatches) {
        this.noOfBibMatches = noOfBibMatches;
    }

    public String getSuccessItemCount() {
        return successItemCount;
    }

    public void setSuccessItemCount(String successItemCount) {
        this.successItemCount = successItemCount;
    }

    public String getFailedItemCount() {
        return failedItemCount;
    }

    public void setFailedItemCount(String failedItem) {
        this.failedItemCount = failedItem;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getReasonForFailureBib() {
        return reasonForFailureBib;
    }

    public void setReasonForFailureBib(String reasonForFailureBib) {
        this.reasonForFailureBib = reasonForFailureBib;
    }

    public String getReasonForFailureItem() {
        return reasonForFailureItem;
    }

    public void setReasonForFailureItem(String reasonForFailureItem) {
        this.reasonForFailureItem = reasonForFailureItem;
    }
}
