package org.recap.matchingAlgorithm.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.ExceptionReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVExceptionRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.ReCAPCSVExceptionRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */

@Component
public class FTPExceptionReportGenerator implements ReportGeneratorInterface {

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.EXCEPTION_TYPE) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FTP) ? true : false;
    }

    @Override
    public String generateReport(String fileName, String type, Date from, Date to) {
        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileAndTypeAndDateRange(fileName, type, from, to);
        ReCAPCSVExceptionRecordGenerator reCAPCSVExceptionRecordGenerator = new ReCAPCSVExceptionRecordGenerator();
        List<ExceptionReportReCAPCSVRecord> exceptionReportReCAPCSVRecordList = new ArrayList<>();
        ReCAPCSVExceptionRecord reCAPCSVExceptionRecord = new ReCAPCSVExceptionRecord();
        if(!CollectionUtils.isEmpty(reportEntityList)) {
            for(ReportEntity reportEntity : reportEntityList) {
                ExceptionReportReCAPCSVRecord exceptionReportReCAPCSVRecord = reCAPCSVExceptionRecordGenerator.prepareExceptionReportReCAPCSVRecord(reportEntity);
                exceptionReportReCAPCSVRecordList.add(exceptionReportReCAPCSVRecord);
            }
            if(!CollectionUtils.isEmpty(exceptionReportReCAPCSVRecordList)) {
                reCAPCSVExceptionRecord.setExceptionReportReCAPCSVRecordList(exceptionReportReCAPCSVRecordList);
                reCAPCSVExceptionRecord.setFileName(RecapConstants.EXCEPTION_REPORT_FILE_NAME);
                reCAPCSVExceptionRecord.setType(RecapConstants.EXCEPTION_TYPE);
                producer.sendBodyAndHeader(RecapConstants.FTP_EXCEPTION_REPORT_Q, reCAPCSVExceptionRecord, RecapConstants.REPORT_FILE_NAME, fileName);
                DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
                String generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
                return generatedFileName;
            }
        }
        return null;
    }
}
