package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * Created by angelind on 26/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class SummaryReportReCAPCSVRecord {

    @DataField(pos = 1)
    private String numberOfBibsInTable;
    @DataField(pos = 2)
    private String numberOfItemsInTable;
    @DataField(pos = 3)
    private String matchingKeyField;
    @DataField(pos = 4)
    private String countOfBibMatches;
    @DataField(pos = 5)
    private String numberOfItemsAffected;

    public String getNumberOfBibsInTable() {
        return numberOfBibsInTable;
    }

    public void setNumberOfBibsInTable(String numberOfBibsInTable) {
        this.numberOfBibsInTable = numberOfBibsInTable;
    }

    public String getNumberOfItemsInTable() {
        return numberOfItemsInTable;
    }

    public void setNumberOfItemsInTable(String numberOfItemsInTable) {
        this.numberOfItemsInTable = numberOfItemsInTable;
    }

    public String getMatchingKeyField() {
        return matchingKeyField;
    }

    public void setMatchingKeyField(String matchingKeyField) {
        this.matchingKeyField = matchingKeyField;
    }

    public String getCountOfBibMatches() {
        return countOfBibMatches;
    }

    public void setCountOfBibMatches(String countOfBibMatches) {
        this.countOfBibMatches = countOfBibMatches;
    }

    public String getNumberOfItemsAffected() {
        return numberOfItemsAffected;
    }

    public void setNumberOfItemsAffected(String numberOfItemsAffected) {
        this.numberOfItemsAffected = numberOfItemsAffected;
    }
}
