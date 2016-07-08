package org.recap.util;

import com.csvreader.CsvWriter;
import org.apache.commons.lang3.StringUtils;
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

    public void createFile() {
        String fileName = "Matching_Algo_Phase1";
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
                    csvOutput.endRecord();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
    }

    public void closeFile() {
        csvOutput.flush();
        csvOutput.close();
    }
}
