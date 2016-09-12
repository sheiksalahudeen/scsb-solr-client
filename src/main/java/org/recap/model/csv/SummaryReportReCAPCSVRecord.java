package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

/**
 * Created by angelind on 26/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class SummaryReportReCAPCSVRecord {

    @DataField(pos = 1)
    private String countOfBibsInTable;
    @DataField(pos = 2)
    private String countOfItemsInTable;
    @DataField(pos = 3)
    private String matchingKeyField;
    @DataField(pos = 4)
    private String countOfBibMatches;
    @DataField(pos = 5)
    private String countOfItemAffected;

    public String getCountOfBibsInTable() {
        return countOfBibsInTable;
    }

    public void setCountOfBibsInTable(String countOfBibsInTable) {
        this.countOfBibsInTable = countOfBibsInTable;
    }

    public String getCountOfItemsInTable() {
        return countOfItemsInTable;
    }

    public void setCountOfItemsInTable(String countOfItemsInTable) {
        this.countOfItemsInTable = countOfItemsInTable;
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

    public String getCountOfItemAffected() {
        return countOfItemAffected;
    }

    public void setCountOfItemAffected(String countOfItemAffected) {
        this.countOfItemAffected = countOfItemAffected;
    }
}
