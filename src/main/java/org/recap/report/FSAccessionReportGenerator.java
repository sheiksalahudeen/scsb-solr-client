package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.AccessionSummaryRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hemalathas on 21/11/16.
 */
@Component
public class FSAccessionReportGenerator implements ReportGeneratorInterface{

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.ACCESSION_SUMMARY_REPORT) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FILE_SYSTEM) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        String generatedFileName;
        List<AccessionSummaryRecord> accessionSummaryRecordList;
        AccessionSummaryRecordGenerator accessionSummaryRecordGenerator = new AccessionSummaryRecordGenerator();
        accessionSummaryRecordList = accessionSummaryRecordGenerator.prepareAccessionSummaryReportRecord(reportEntityList);
        producerTemplate.sendBodyAndHeader(RecapConstants.FS_ACCESSION_SUMMARY_REPORT_Q, accessionSummaryRecordList, "fileName", fileName);
        DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        return generatedFileName;
    }
}
