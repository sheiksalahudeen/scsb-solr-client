package org.recap.matchingAlgorithm.report;

import org.recap.repository.jpa.ReportDetailRepository;
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

    @Autowired
    ReportDetailRepository reportDetailRepository;

    List<ReportGeneratorInterface> reportGenerators;

    @Autowired
    CSVExceptionReportGenerator csvExceptionReportGenerator;

    @Autowired
    CSVMatchingReportGenerator csvMatchingReportGenerator;

    @Autowired
    FTPExceptionReportGenerator ftpExceptionReportGenerator;

    @Autowired
    FTPMatchingReportGenerator ftpMatchingReportGenerator;

    public String generateReport(String fileName, String reportType, String transmissionType, Date from, Date to) {
        for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
            ReportGeneratorInterface reportGeneratorInterface = iterator.next();
            if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                String generatedFileName = reportGeneratorInterface.generateReport(fileName, reportType, from, to);
                return generatedFileName;
            }
        }

        return null;
    }

    public List<ReportGeneratorInterface> getReportGenerators() {
        if(CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
            reportGenerators.add(csvExceptionReportGenerator);
            reportGenerators.add(csvMatchingReportGenerator);
            reportGenerators.add(ftpExceptionReportGenerator);
            reportGenerators.add(ftpMatchingReportGenerator);
        }
        return reportGenerators;
    }
}
