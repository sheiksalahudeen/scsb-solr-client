package org.recap.report;

import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

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

    @Autowired
    CSVSolrExceptionReportGenerator csvSolrExceptionReportGenerator;

    @Autowired
    FTPSolrExceptionReportGenerator ftpSolrExceptionReportGenerator;

    @Autowired
    FSDeAccessionReportGenerator fsDeAccessionReportGenerator;

    @Autowired
    FTPDeAccessionReportGenerator ftpDeAccessionReportGenerator;

    @Autowired
    FSAccessionReportGenerator fsAccessionReportGenerator;

    @Autowired
    FTPAccessionReportGenerator ftpAccessionReportGenerator;

    @Autowired
    FSSubmitCollectionRejectionReportGenerator fsSubmitCollectionRejectionReportGenerator;

    @Autowired
    FTPSubmitCollectionRejectionReportGenerator ftpSubmitCollectionRejectionReportGenerator;

    @Autowired
    FSSubmitCollectionExceptionReportGenerator fsSubmitCollectionExceptionReportGenerator;

    @Autowired
    FTPSubmitCollectionExceptionReportGenerator ftpSubmitCollectionExceptionReportGenerator;

    @Autowired
    FSOngoingAccessionReportGenerator fsOngoingAccessionReportGenerator;

    @Autowired
    FTPOngoingAccessionReportGenerator ftpOngoingAccessionReportGenerator;

    public String generateReport(String fileName, String institutionName, String reportType, String transmissionType, Date from, Date to) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ReportEntity> reportEntityList;
        if(institutionName.equalsIgnoreCase(RecapConstants.ALL_INST)) {
            reportEntityList = reportDetailRepository.findByFileAndTypeAndDateRange(fileName, reportType, from, to);
        } else {
            reportEntityList = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(fileName, institutionName, reportType, from, to);
        }

        if(reportType.equalsIgnoreCase(RecapConstants.ACCESSION_SUMMARY_REPORT)){
            fileName = fileName+"-"+institutionName;
        } else if (reportType.equalsIgnoreCase(RecapConstants.ONGOING_ACCESSION_REPORT)){
            fileName = RecapConstants.ONGOING_ACCESSION_REPORT+"-"+institutionName;
        }
        else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)){
            fileName = RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT+"-"+institutionName;
        }else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT)){
            fileName = RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT+"-"+institutionName;
        }

        stopWatch.stop();
        logger.info("Total Time taken to fetch Report Entities From DB : {} " , stopWatch.getTotalTimeSeconds());
        logger.info("Total Num of Report Entities Fetched From DB : {} " , reportEntityList.size());

        for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
            ReportGeneratorInterface reportGeneratorInterface = iterator.next();
            if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                String generatedFileName = reportGeneratorInterface.generateReport(fileName, reportEntityList);
                logger.info("The Generated File Name is : " , generatedFileName);
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
            reportGenerators.add(csvSolrExceptionReportGenerator);
            reportGenerators.add(ftpSolrExceptionReportGenerator);
            reportGenerators.add(fsDeAccessionReportGenerator);
            reportGenerators.add(ftpDeAccessionReportGenerator);
            reportGenerators.add(fsAccessionReportGenerator);
            reportGenerators.add(ftpAccessionReportGenerator);
            reportGenerators.add(fsSubmitCollectionRejectionReportGenerator);
            reportGenerators.add(ftpSubmitCollectionRejectionReportGenerator);
            reportGenerators.add(fsSubmitCollectionExceptionReportGenerator);
            reportGenerators.add(ftpSubmitCollectionExceptionReportGenerator);
            reportGenerators.add(fsOngoingAccessionReportGenerator);
            reportGenerators.add(ftpOngoingAccessionReportGenerator);
        }
        return reportGenerators;
    }
}
