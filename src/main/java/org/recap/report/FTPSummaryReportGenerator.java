package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.SummaryReportReCAPCSVRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.ReCAPCSVSummaryRecordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 31/8/16.
 */

@Component
public class FTPSummaryReportGenerator implements ReportGeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(CSVSummaryReportGenerator.class);

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

    @Override
    public boolean isInterested(String reportType) {
        return RecapConstants.SUMMARY_TYPE.equalsIgnoreCase(reportType) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return RecapConstants.FTP.equalsIgnoreCase(transmissionType) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<SummaryReportReCAPCSVRecord> summaryReportReCAPCSVRecords = new ArrayList<>();

        ReCAPCSVSummaryRecordGenerator reCAPCSVSummaryRecordGenerator = new ReCAPCSVSummaryRecordGenerator();
        if(!CollectionUtils.isEmpty(reportEntityList)) {
            for(ReportEntity reportEntity : reportEntityList) {
                SummaryReportReCAPCSVRecord summaryReportReCAPCSVRecord = reCAPCSVSummaryRecordGenerator.prepareSummaryReportReCAPCSVRecord(reportEntity, new SummaryReportReCAPCSVRecord());
                summaryReportReCAPCSVRecords.add(summaryReportReCAPCSVRecord);
            }
        }

        stopWatch.stop();
        logger.info("Total time taken to prepare CSVRecords : {} " , stopWatch.getTotalTimeSeconds());
        logger.info("Total Num of CSVRecords Prepared : {}  " , summaryReportReCAPCSVRecords.size());

        if(!CollectionUtils.isEmpty(summaryReportReCAPCSVRecords)) {

            producer.sendBodyAndHeader(RecapConstants.FTP_SUMMARY_ALGO_REPORT_Q, summaryReportReCAPCSVRecords, RecapConstants.REPORT_FILE_NAME, fileName);

            DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
            return fileName + "-" + df.format(new Date()) + ".csv";
        }

        return null;
    }


}
