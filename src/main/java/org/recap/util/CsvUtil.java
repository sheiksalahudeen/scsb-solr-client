package org.recap.util;

import com.csvreader.CsvWriter;
import org.apache.commons.lang3.StringUtils;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.MatchingRecordReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 4/7/16.
 */
@Component
public class CsvUtil {

    @Value("${solr.report.directory}")
    private String reportDirectoryPath;

    private CsvWriter csvOutput;

    private boolean headerExists;

    Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    public void createFile(String fileName) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        File file = new File(reportDirectoryPath + File.separator + fileName + "_" + df.format(new Date()) + ".csv");
        boolean fileExists = file.exists();
        headerExists = false;
        try {
            if (!fileExists) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdir();
                }
            }
            // Use FileWriter constructor that specifies open for appending
            csvOutput = new CsvWriter(new FileWriter(file), ',');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMatchingAlgorithmReportToCsv(List<MatchingRecordReport> matchingRecordReports) {
        if (!CollectionUtils.isEmpty(matchingRecordReports)) {
            try {
                //Create Header for CSV
                if (!headerExists) {
                    csvOutput.write("Match Point Tag");
                    csvOutput.write("Match Point Content");
                    csvOutput.write("Bib ID");
                    csvOutput.write("245 $a");
                    csvOutput.write("Barcode");
                    csvOutput.write("Institution ID");
                    csvOutput.write("Use Restrictions");
                    csvOutput.write("Summary Holdings");
                    csvOutput.endRecord();
                    headerExists = true;
                }
                for (MatchingRecordReport matchingRecordReport : matchingRecordReports) {
                    csvOutput.write(matchingRecordReport.getMatchPointTag());
                    csvOutput.write(matchingRecordReport.getMatchPointContent());
                    csvOutput.write(matchingRecordReport.getBibId());
                    csvOutput.write(matchingRecordReport.getTitle());
                    csvOutput.write(matchingRecordReport.getBarcode());
                    csvOutput.write(matchingRecordReport.getInstitutionId());
                    String useRestrictions = matchingRecordReport.getUseRestrictions();
                    csvOutput.write(StringUtils.isNotBlank(useRestrictions) ? useRestrictions : "null");
                    String summaryHoldings = matchingRecordReport.getSummaryHoldings();
                    csvOutput.write(StringUtils.isNotBlank(summaryHoldings) ? summaryHoldings : "null");
                    csvOutput.endRecord();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    /**
     * Generates a csv file for the selected search result rows.
     * @param searchResultRows
     * @param fileNameWithExtension
     * @return
     */
    public File writeSearchResultsToCsv(List<SearchResultRow> searchResultRows, String fileNameWithExtension) {
        File file = new File(fileNameWithExtension);
        if (!CollectionUtils.isEmpty(searchResultRows)) {
            try {
                csvOutput = new CsvWriter(new FileWriter(file), ',');
                writeMainHeaderRow();
                for (SearchResultRow searchResultRow : searchResultRows) {
                    if (searchResultRow.isSelected()) {
                        writeMainDataRow(searchResultRow);
                    } else if (!CollectionUtils.isEmpty(searchResultRow.getSearchItemResultRows())) {
                        if (searchResultRow.isSelectAllItems()) {
                            writeMainDataRow(searchResultRow);
                        } else if (isAnyItemSelected(searchResultRow.getSearchItemResultRows())) {
                            writeMainDataRow(searchResultRow);
                        }
                        boolean isHeaderExists = false;
                        for (SearchItemResultRow searchItemResultRow : searchResultRow.getSearchItemResultRows()) {
                            if (searchItemResultRow.isSelectedItem()) {
                                if (!isHeaderExists) {
                                    writeChildHeaderRow();
                                    isHeaderExists = true;
                                }
                                writeChildDataRow(searchItemResultRow);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                csvOutput.flush();
                csvOutput.close();
            }
        }
        return file;
    }

    private void writeMainHeaderRow() throws IOException {
        csvOutput.write("Title");
        csvOutput.write("Author");
        csvOutput.write("Publisher");
        csvOutput.write("Publisher Date");
        csvOutput.write("Owning Institution");
        csvOutput.write("Customer Code");
        csvOutput.write("Collection Group Designation");
        csvOutput.write("Use Restriction");
        csvOutput.write("Barcode");
        csvOutput.write("Summary Holdings");
        csvOutput.endRecord();
    }

    private void writeMainDataRow(SearchResultRow searchResultRow) throws IOException {
        csvOutput.write(searchResultRow.getTitle());
        csvOutput.write(searchResultRow.getAuthor());
        csvOutput.write(searchResultRow.getPublisher());
        csvOutput.write(searchResultRow.getPublisherDate());
        csvOutput.write(searchResultRow.getOwningInstitution());
        csvOutput.write(searchResultRow.getCustomerCode());
        csvOutput.write(searchResultRow.getCollectionGroupDesignation());
        csvOutput.write(searchResultRow.getUseRestriction());
        csvOutput.write(searchResultRow.getBarcode());
        csvOutput.write(searchResultRow.getSummaryHoldings());
        csvOutput.endRecord();
    }

    private void writeChildHeaderRow() throws IOException {
        csvOutput.write("");
        csvOutput.write("");
        csvOutput.write("");
        csvOutput.write("Call Number");
        csvOutput.write("Chronology & Enumeration");
        csvOutput.write("Customer Code");
        csvOutput.write("Collection Group Designation");
        csvOutput.write("Use Restriction");
        csvOutput.write("Barcode");
        csvOutput.endRecord();
    }

    private void writeChildDataRow(SearchItemResultRow searchItemResultRow) throws IOException {
        csvOutput.write("");
        csvOutput.write("");
        csvOutput.write("");
        csvOutput.write(searchItemResultRow.getCallNumber());
        csvOutput.write(searchItemResultRow.getChronologyAndEnum());
        csvOutput.write(searchItemResultRow.getCustomerCode());
        csvOutput.write(searchItemResultRow.getCollectionGroupDesignation());
        csvOutput.write(searchItemResultRow.getUseRestriction());
        csvOutput.write(searchItemResultRow.getBarcode());
        csvOutput.endRecord();
    }

    private boolean isAnyItemSelected(List<SearchItemResultRow> searchItemResultRows) {
        for (SearchItemResultRow searchItemResultRow : searchItemResultRows) {
            if (searchItemResultRow.isSelectedItem()) {
                return true;
            }
        }
        return false;
    }

    public void closeFile() {
        csvOutput.flush();
        csvOutput.close();
    }
}
