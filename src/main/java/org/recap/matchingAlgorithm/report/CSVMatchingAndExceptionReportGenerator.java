package org.recap.matchingAlgorithm.report;

import com.google.common.collect.Ordering;
import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.csv.MatchingReportReCAPCSVRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.ReCAPCSVMatchingRecordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */

@Component
public class CSVMatchingAndExceptionReportGenerator implements ReportGeneratorInterface{

    Logger logger = LoggerFactory.getLogger(CSVMatchingAndExceptionReportGenerator.class);

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Override
    public boolean isInterested(String reportType) {
        return (reportType.equalsIgnoreCase(RecapConstants.MATCHING_TYPE) || reportType.equalsIgnoreCase(RecapConstants.EXCEPTION_TYPE)) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FILE_SYSTEM) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecords = new ArrayList<>();

        ReCAPCSVMatchingRecordGenerator reCAPCSVMatchingRecordGenerator = new ReCAPCSVMatchingRecordGenerator();
        String reportType = "";
        if(!CollectionUtils.isEmpty(reportEntityList)) {
            reportType = reportEntityList.get(0).getType();
            for(ReportEntity reportEntity : reportEntityList) {
                MatchingReportReCAPCSVRecord matchingReportReCAPCSVRecord = reCAPCSVMatchingRecordGenerator.prepareMatchingReportReCAPCSVRecord(reportEntity, new MatchingReportReCAPCSVRecord());
                matchingReportReCAPCSVRecord.setTitleWithoutSymbols(matchingReportReCAPCSVRecord.getTitle().replaceAll("[^\\w\\s]", "").trim());
                matchingReportReCAPCSVRecord.setTitle(matchingReportReCAPCSVRecord.getTitle().replaceAll("\"", "\"\""));
                matchingReportReCAPCSVRecords.add(matchingReportReCAPCSVRecord);
            }
        }

        stopWatch.stop();
        logger.info("Total time taken to prepare CSVRecords : " + stopWatch.getTotalTimeSeconds());
        logger.info("Total Num of CSVRecords Prepared : " + matchingReportReCAPCSVRecords.size());

        if(!CollectionUtils.isEmpty(matchingReportReCAPCSVRecords)) {
            if(RecapConstants.MATCHING_TYPE.equalsIgnoreCase(reportType)) {
                matchingReportReCAPCSVRecords.sort(Comparator.comparing(MatchingReportReCAPCSVRecord::getTitleWithoutSymbols, String.CASE_INSENSITIVE_ORDER));
            } else {
                matchingReportReCAPCSVRecords.sort(Comparator.comparing(MatchingReportReCAPCSVRecord::getOclc, Ordering.natural().nullsLast())
                .thenComparing(Comparator.comparing(MatchingReportReCAPCSVRecord::getIsbn, Ordering.natural().nullsLast()))
                .thenComparing(Comparator.comparing(MatchingReportReCAPCSVRecord::getIssn, Ordering.natural().nullsLast()))
                .thenComparing(Comparator.comparing(MatchingReportReCAPCSVRecord::getLccn, Ordering.natural().nullsLast())));
            }

            producer.sendBodyAndHeader(RecapConstants.CSV_MATCHING_ALGO_REPORT_Q, matchingReportReCAPCSVRecords, RecapConstants.REPORT_FILE_NAME, fileName);

            DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
            String generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
            return generatedFileName;
        }

        return null;
    }
}
