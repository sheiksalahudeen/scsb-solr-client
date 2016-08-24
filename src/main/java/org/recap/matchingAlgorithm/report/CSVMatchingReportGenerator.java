package org.recap.matchingAlgorithm.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.csv.MatchingReportReCAPCSVRecord;
import org.recap.model.csv.ReCAPCSVMatchingRecord;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */

@Component
public class CSVMatchingReportGenerator implements ReportGeneratorInterface{

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.MATCHING_TYPE) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FILE_SYSTEM) ? true : false;
    }

    @Override
    public String generateReport(String fileName, String type, Date from, Date to) {

        List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecords = new ArrayList<>();
        ReCAPCSVMatchingRecord reCAPCSVMatchingRecord = new ReCAPCSVMatchingRecord();

        matchingAlgorithmHelperService.getMultipleMatchPointMatchRecords(fileName, type, from, to, matchingReportReCAPCSVRecords);

        matchingAlgorithmHelperService.getSingleMatchPointMatchRecords(fileName, type, from, to, matchingReportReCAPCSVRecords);

        if(!CollectionUtils.isEmpty(matchingReportReCAPCSVRecords)) {
            Collections.sort(matchingReportReCAPCSVRecords);
            reCAPCSVMatchingRecord.setFileName(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME);
            reCAPCSVMatchingRecord.setType(RecapConstants.MATCHING_TYPE);
            reCAPCSVMatchingRecord.setMatchingReportReCAPCSVRecordList(matchingReportReCAPCSVRecords);
            producer.sendBodyAndHeader(RecapConstants.CSV_MATCHING_ALGO_REPORT_Q, reCAPCSVMatchingRecord, RecapConstants.REPORT_FILE_NAME, fileName);
            DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
            String generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
            return generatedFileName;
        }

        return null;
    }
}
