package org.recap.util;

import com.csvreader.CsvWriter;
import org.recap.RecapConstants;
import org.recap.model.matchingReports.TitleExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by angelind on 27/6/17.
 */
@Component
public class CsvUtil {

    private static final Logger logger = LoggerFactory.getLogger(CsvUtil.class);

    /**
     * Create title exception report file file.
     *
     * @param fileNameWithExtension the file name with extension
     * @param maxTitleCount         the max title count
     * @param titleExceptionReports the title exception reports
     * @return the file
     */
    public File createTitleExceptionReportFile(String fileNameWithExtension, int maxTitleCount, List<TitleExceptionReport> titleExceptionReports) {
        File file = new File(fileNameWithExtension);
        CsvWriter csvOutput = null;
        try (FileWriter fileWriter = new FileWriter(file, true)){
            csvOutput = new CsvWriter(fileWriter, ',');
            writeHeaderRowForTitleExceptionReport(csvOutput, maxTitleCount);
            for(TitleExceptionReport exceptionReport : titleExceptionReports) {
                writeDataRowForTitleExceptionReport(exceptionReport, csvOutput);
            }
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        } finally {
            csvOutput.flush();
            csvOutput.close();
        }
        return file;
    }

    /**
     * Write header row for title exception report.
     *
     * @param csvOutput     the csv output
     * @param maxTitleCount the max title count
     * @throws IOException the io exception
     */
    public void writeHeaderRowForTitleExceptionReport(CsvWriter csvOutput, int maxTitleCount) throws IOException {
        csvOutput.write("OwningInstitution");
        csvOutput.write("BibId");
        csvOutput.write("OwningInstitutionBibId");
        csvOutput.write("MaterialType");
        csvOutput.write("OCLCNumber");
        csvOutput.write("ISBN");
        csvOutput.write("ISSN");
        csvOutput.write("LCCN");
        for(int i=1; i<=maxTitleCount; i++) {
            csvOutput.write("Title" + i);
        }
        csvOutput.endRecord();
    }

    /**
     * Write data row for title exception report.
     *
     * @param titleExceptionReport the title exception report
     * @param csvOutput            the csv output
     * @throws IOException the io exception
     */
    public void writeDataRowForTitleExceptionReport(TitleExceptionReport titleExceptionReport, CsvWriter csvOutput) throws IOException {
        csvOutput.write(titleExceptionReport.getOwningInstitution());
        csvOutput.write(titleExceptionReport.getBibId());
        csvOutput.write(titleExceptionReport.getOwningInstitutionBibId());
        csvOutput.write(titleExceptionReport.getMaterialType());
        csvOutput.write(titleExceptionReport.getOCLC());
        csvOutput.write(titleExceptionReport.getISBN());
        csvOutput.write(titleExceptionReport.getISSN());
        csvOutput.write(titleExceptionReport.getLCCN());
        for(String title : titleExceptionReport.getTitleList()) {
            csvOutput.write(title);
        }
        csvOutput.endRecord();
    }

    /**
     * Write header row for serils and mvms report.
     *
     * @param csvWriter the csv writer
     * @throws IOException the io exception
     */
    public void writeHeaderRowForSerilsAndMvmsReport(CsvWriter csvWriter) throws IOException {

    }
}
