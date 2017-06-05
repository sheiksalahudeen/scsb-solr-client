package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.DeAccessionSummaryRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.DeAccessionSummaryRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
@Component
public class FTPDeAccessionReportGenerator implements ReportGeneratorInterface {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.DEACCESSION_SUMMARY_REPORT) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FTP) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        String generatedFileName ;
        DeAccessionSummaryRecordGenerator deAccessionSummaryRecordGenerator = new DeAccessionSummaryRecordGenerator();
        List<DeAccessionSummaryRecord> deAccessionSummaryRecordList = new ArrayList<>();

        for(ReportEntity reportEntity : reportEntityList) {
            DeAccessionSummaryRecord deAccessionSummaryRecord = deAccessionSummaryRecordGenerator.prepareDeAccessionSummaryReportRecord(reportEntity);
            deAccessionSummaryRecordList.add(deAccessionSummaryRecord);
        }
        producerTemplate.sendBodyAndHeader(RecapConstants.FTP_DE_ACCESSION_SUMMARY_REPORT_Q, deAccessionSummaryRecordList, "fileName", fileName);

        DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        return generatedFileName;
    }
}
