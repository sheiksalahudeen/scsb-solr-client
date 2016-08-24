package org.recap.model.csv;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.OneToMany;

import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */

@CsvRecord(generateHeaderColumns = true, separator = ",", quoting = true, crlf = "UNIX", skipFirstLine = true)
public class ReCAPCSVExceptionRecord {

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
    private String fileName;

    @Ignore
    private String type;

    @OneToMany(mappedTo = "org.recap.model.csv.ExceptionReportReCAPCSVRecord")
    List<ExceptionReportReCAPCSVRecord> exceptionReportReCAPCSVRecordList;

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

    public List<ExceptionReportReCAPCSVRecord> getExceptionReportReCAPCSVRecordList() {
        return exceptionReportReCAPCSVRecordList;
    }

    public void setExceptionReportReCAPCSVRecordList(List<ExceptionReportReCAPCSVRecord> exceptionReportReCAPCSVRecordList) {
        this.exceptionReportReCAPCSVRecordList = exceptionReportReCAPCSVRecordList;
    }
}
