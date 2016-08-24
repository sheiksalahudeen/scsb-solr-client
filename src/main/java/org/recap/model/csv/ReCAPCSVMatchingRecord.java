package org.recap.model.csv;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.util.List;

/**
 * Created by angelind on 22/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class ReCAPCSVMatchingRecord {

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
    private String fileName;

    @Ignore
    private String type;

    @OneToMany(mappedTo = "org.recap.model.csv.MatchingReportReCAPCSVRecord")
    List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecordList;

    public List<MatchingReportReCAPCSVRecord> getMatchingReportReCAPCSVRecordList() {
        return matchingReportReCAPCSVRecordList;
    }

    public void setMatchingReportReCAPCSVRecordList(List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecordList) {
        this.matchingReportReCAPCSVRecordList = matchingReportReCAPCSVRecordList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
