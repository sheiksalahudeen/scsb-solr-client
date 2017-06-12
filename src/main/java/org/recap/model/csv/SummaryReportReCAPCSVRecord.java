package org.recap.model.csv;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.io.Serializable;

/**
 * Created by angelind on 26/8/16.
 */
@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX")
public class SummaryReportReCAPCSVRecord implements Serializable{

    @DataField(pos = 1, columnName = "Count Of Bibs In Table")
    private String countOfBibsInTable;
    @DataField(pos = 2, columnName = "Count Of Items In Table")
    private String countOfItemsInTable;
    @DataField(pos = 3, columnName = "Matching Key Field")
    private String matchingKeyField;
    @DataField(pos = 4, columnName = "Count Of Bib Matches")
    private String countOfBibMatches;
    @DataField(pos = 5, columnName = "Count Of Item Affected")
    private String countOfItemAffected;

    /**
     * Gets count of bibs in table.
     *
     * @return the count of bibs in table
     */
    public String getCountOfBibsInTable() {
        return countOfBibsInTable;
    }

    /**
     * Sets count of bibs in table.
     *
     * @param countOfBibsInTable the count of bibs in table
     */
    public void setCountOfBibsInTable(String countOfBibsInTable) {
        this.countOfBibsInTable = countOfBibsInTable;
    }

    /**
     * Gets count of items in table.
     *
     * @return the count of items in table
     */
    public String getCountOfItemsInTable() {
        return countOfItemsInTable;
    }

    /**
     * Sets count of items in table.
     *
     * @param countOfItemsInTable the count of items in table
     */
    public void setCountOfItemsInTable(String countOfItemsInTable) {
        this.countOfItemsInTable = countOfItemsInTable;
    }

    /**
     * Gets matching key field.
     *
     * @return the matching key field
     */
    public String getMatchingKeyField() {
        return matchingKeyField;
    }

    /**
     * Sets matching key field.
     *
     * @param matchingKeyField the matching key field
     */
    public void setMatchingKeyField(String matchingKeyField) {
        this.matchingKeyField = matchingKeyField;
    }

    /**
     * Gets count of bib matches.
     *
     * @return the count of bib matches
     */
    public String getCountOfBibMatches() {
        return countOfBibMatches;
    }

    /**
     * Sets count of bib matches.
     *
     * @param countOfBibMatches the count of bib matches
     */
    public void setCountOfBibMatches(String countOfBibMatches) {
        this.countOfBibMatches = countOfBibMatches;
    }

    /**
     * Gets count of item affected.
     *
     * @return the count of item affected
     */
    public String getCountOfItemAffected() {
        return countOfItemAffected;
    }

    /**
     * Sets count of item affected.
     *
     * @param countOfItemAffected the count of item affected
     */
    public void setCountOfItemAffected(String countOfItemAffected) {
        this.countOfItemAffected = countOfItemAffected;
    }
}
