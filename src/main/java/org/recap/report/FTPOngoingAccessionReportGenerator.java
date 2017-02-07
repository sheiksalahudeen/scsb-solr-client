package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.OngoingAccessionReportRecord;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.OngoingAccessionReportGenerator;
import org.recap.util.SubmitCollectionReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by premkb on 07/02/17.
 */
@Component
public class FTPOngoingAccessionReportGenerator implements ReportGeneratorInterface {

    @Autowired
    ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.ONGOING_ACCESSION_REPORT) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FTP) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        String generatedFileName = null;
        List<OngoingAccessionReportRecord> ongoingAccessionReportRecordList = new ArrayList<>();
        OngoingAccessionReportGenerator ongoingAccessionReportGenerator = new OngoingAccessionReportGenerator();
        for(ReportEntity reportEntity : reportEntityList) {
            List<OngoingAccessionReportRecord> ongoingAccessionReportRecords = ongoingAccessionReportGenerator.prepareOngoingAccessionReportRecord(reportEntity);
            ongoingAccessionReportRecordList.addAll(ongoingAccessionReportRecords);
        }
        producerTemplate.sendBodyAndHeader(RecapConstants.FTP_ONGOING_ACCESSON_REPORT_Q, ongoingAccessionReportRecordList, "fileName", fileName);

        DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        return generatedFileName;
    }
}
