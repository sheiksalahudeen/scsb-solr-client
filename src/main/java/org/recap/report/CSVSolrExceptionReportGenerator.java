package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.model.csv.SolrExceptionReportReCAPCSVRecord;
import org.recap.model.jpa.ReportEntity;
import org.recap.util.ReCAPCSVSolrExceptionRecordGenerator;
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
 * Created by angelind on 30/9/16.
 */
@Component
public class CSVSolrExceptionReportGenerator implements ReportGeneratorInterface{

    private static final Logger logger = LoggerFactory.getLogger(CSVSolrExceptionReportGenerator.class);

    @Autowired
    private ProducerTemplate producer;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.SOLR_INDEX_EXCEPTION) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FILE_SYSTEM) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<SolrExceptionReportReCAPCSVRecord> solrExceptionReportReCAPCSVRecords = new ArrayList<>();

        ReCAPCSVSolrExceptionRecordGenerator reCAPCSVSolrExceptionRecordGenerator = new ReCAPCSVSolrExceptionRecordGenerator();
        for(ReportEntity reportEntity : reportEntityList) {
            SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord = reCAPCSVSolrExceptionRecordGenerator.prepareSolrExceptionReportReCAPCSVRecord(reportEntity, new SolrExceptionReportReCAPCSVRecord());
            solrExceptionReportReCAPCSVRecords.add(solrExceptionReportReCAPCSVRecord);
        }

        stopWatch.stop();
        logger.info("Total time taken to prepare CSVRecords : {} ",stopWatch.getTotalTimeSeconds());
        logger.info("Total Num of CSVRecords Prepared : {}  ",solrExceptionReportReCAPCSVRecords.size());

        if(!CollectionUtils.isEmpty(solrExceptionReportReCAPCSVRecords)) {
            producer.sendBodyAndHeader(RecapConstants.CSV_SOLR_EXCEPTION_REPORT_Q, solrExceptionReportReCAPCSVRecords, RecapConstants.REPORT_FILE_NAME, fileName);
            DateFormat df = new SimpleDateFormat(RecapConstants.DATE_FORMAT_FOR_FILE_NAME);
            return fileName + "-" + df.format(new Date()) + ".csv";
        }

        return null;
    }
}
