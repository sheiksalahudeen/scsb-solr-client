package org.recap.matchingAlgorithm.report;

import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */

@Component
public class ReportGenerator {

    Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    @Autowired
    ReportDetailRepository reportDetailRepository;

    List<ReportGeneratorInterface> reportGenerators;

    @Autowired
    CSVMatchingAndExceptionReportGenerator csvMatchingAndExceptionReportGenerator;

    @Autowired
    FTPMatchingAndExceptionReportGenerator ftpMatchingAndExceptionReportGenerator;

    @Autowired
    CSVSummaryReportGenerator csvSummaryReportGenerator;

    @Autowired
    FTPSummaryReportGenerator ftpSummaryReportGenerator;

    public String generateReport(String fileName, String reportType, String transmissionType, Date from, Date to) {

        for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
            ReportGeneratorInterface reportGeneratorInterface = iterator.next();
            if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                String generatedFileName = reportGeneratorInterface.generateReport(fileName, reportType, from, to);
                logger.info("The Generated File Name is : " + generatedFileName);
                return generatedFileName;
            }
        }

        return null;
    }

    public List<ReportGeneratorInterface> getReportGenerators() {
        if(CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
            reportGenerators.add(csvMatchingAndExceptionReportGenerator);
            reportGenerators.add(ftpMatchingAndExceptionReportGenerator);
            reportGenerators.add(csvSummaryReportGenerator);
            reportGenerators.add(ftpSummaryReportGenerator);
        }
        return reportGenerators;
    }
}
