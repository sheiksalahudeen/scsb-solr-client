package org.recap.report;

import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    List<ReportGeneratorInterface> reportGenerators;

    @Autowired
    private CSVSolrExceptionReportGenerator csvSolrExceptionReportGenerator;

    @Autowired
    private FTPSolrExceptionReportGenerator ftpSolrExceptionReportGenerator;

    @Autowired
    private FSDeAccessionReportGenerator fsDeAccessionReportGenerator;

    @Autowired
    private FTPDeAccessionReportGenerator ftpDeAccessionReportGenerator;

    @Autowired
    private FSAccessionReportGenerator fsAccessionReportGenerator;

    @Autowired
    private FTPAccessionReportGenerator ftpAccessionReportGenerator;

    @Autowired
    private FSSubmitCollectionRejectionReportGenerator fsSubmitCollectionRejectionReportGenerator;

    @Autowired
    private FTPSubmitCollectionRejectionReportGenerator ftpSubmitCollectionRejectionReportGenerator;

    @Autowired
    private FSSubmitCollectionExceptionReportGenerator fsSubmitCollectionExceptionReportGenerator;

    @Autowired
    private FTPSubmitCollectionExceptionReportGenerator ftpSubmitCollectionExceptionReportGenerator;

    @Autowired
    private FTPSubmitCollectionSummaryReportGenerator ftpSubmitCollectionSummaryReportGenerator;

    @Autowired
    private FSSubmitCollectionSummaryReportGenerator fsSubmitCollectionSummaryReportGenerator;

    @Autowired
    private FSOngoingAccessionReportGenerator fsOngoingAccessionReportGenerator;

    @Autowired
    private FTPOngoingAccessionReportGenerator ftpOngoingAccessionReportGenerator;

    @Autowired
    private FTPSubmitCollectionReportGenerator ftpSubmitCollectionReportGenerator;

    @Autowired
    private FTPSubmitCollectionSuccessReportGenerator ftpSubmitCollectionSuccessReportGenerator;

    @Autowired
    private FSSubmitCollectionSuccessReportGenerator fsSubmitCollectionSuccessReportGenerator;

    @Autowired
    private FTPSubmitCollectionFailureReportGenerator ftpSubmitCollectionFailureReportGenerator;

    @Autowired
    private FSSubmitCollectionFailureReportGenerator fsSubmitCollectionFailureReportGenerator;

    /**
     * This method is used to generate report based on the reportType.
     *
     * @param fileName         the file name
     * @param institutionName  the institution name
     * @param reportType       the report type
     * @param transmissionType the transmission type
     * @param from             the from
     * @param to               the to
     * @return the string
     */
    public String generateReport(String fileName, String institutionName, String reportType, String transmissionType, Date from, Date to) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ReportEntity> reportEntityList;
        reportEntityList = getReportEntities(fileName, institutionName, reportType, from, to);

        if(CollectionUtils.isNotEmpty(reportEntityList)) {
            String actualFileName = fileName;
            if(reportType.equalsIgnoreCase(RecapConstants.ACCESSION_SUMMARY_REPORT) || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUMMARY)){
                actualFileName = fileName+"-"+institutionName;
            } else if (reportType.equalsIgnoreCase(RecapConstants.ONGOING_ACCESSION_REPORT)){
                actualFileName = RecapConstants.ONGOING_ACCESSION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_SUCCESS_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FAILURE_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_FAILURE_REPORT+"-"+institutionName;
            } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT)){
                actualFileName = RecapConstants.SUBMIT_COLLECTION_SUMMARY_REPORT+"-"+institutionName;
            }

            stopWatch.stop();
            logger.info("Total Time taken to fetch Report Entities From DB : {} " , stopWatch.getTotalTimeSeconds());
            logger.info("Total Num of Report Entities Fetched From DB : {} " , reportEntityList.size());

            for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
                ReportGeneratorInterface reportGeneratorInterface = iterator.next();
                if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                    String generatedFileName = reportGeneratorInterface.generateReport(actualFileName, reportEntityList);
                    logger.info("The Generated File Name is : {}" , generatedFileName);
                    return generatedFileName;
                }
            }
        }

        return null;
    }

    private List<ReportEntity> getReportEntities(String fileName, String institutionName, String reportType, Date from, Date to) {
        List<ReportEntity> reportEntityList;
        if(!institutionName.equalsIgnoreCase(RecapConstants.ALL_INST) && (reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FAILURE_REPORT))){
            fileName = getFileNameLike(fileName);
            reportEntityList = reportDetailRepository.findByFileLikeAndInstitutionAndTypeAndDateRange(fileName,institutionName,reportType,from,to);
        }else if(institutionName.equalsIgnoreCase(RecapConstants.ALL_INST) && (reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)
                || reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_FAILURE_REPORT))){
            fileName = getFileNameLike(fileName);
            reportEntityList = reportDetailRepository.findByFileLikeAndTypeAndDateRange(fileName,reportType,from,to);
        } else if(reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION_SUMMARY)){
            reportEntityList = reportDetailRepository.findByFileName(fileName);
        } else if(institutionName.equalsIgnoreCase(RecapConstants.ALL_INST)) {
            reportEntityList = reportDetailRepository.findByFileLikeAndTypeAndDateRange(fileName, reportType, from, to);
        } else {
            reportEntityList = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(fileName, institutionName, reportType, from, to);
        }
        return reportEntityList;
    }


    /**
     * Gets report generators.
     *
     * @return the report generators
     */
    public List<ReportGeneratorInterface> getReportGenerators() {
        if(CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
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
            reportGenerators.add(ftpSubmitCollectionSummaryReportGenerator);
            reportGenerators.add(fsSubmitCollectionSummaryReportGenerator);
            reportGenerators.add(fsOngoingAccessionReportGenerator);
            reportGenerators.add(ftpOngoingAccessionReportGenerator);
            reportGenerators.add(ftpSubmitCollectionReportGenerator);
            reportGenerators.add(ftpSubmitCollectionSuccessReportGenerator);
            reportGenerators.add(fsSubmitCollectionSuccessReportGenerator);
            reportGenerators.add(ftpSubmitCollectionFailureReportGenerator);
            reportGenerators.add(fsSubmitCollectionFailureReportGenerator);
        }
        return reportGenerators;
    }

    /**
     * Generate submit collection  report to the FTP.
     *
     * @param reportRecordNumberList the report record number list
     * @param reportType             the report type
     * @param transmissionType       the transmission type
     * @return the string
     */
    public String generateReportBasedOnReportRecordNum(List<Integer> reportRecordNumberList,String reportType,String transmissionType) {
        String response = null;
        List<ReportGeneratorInterface> reportGeneratorInterfaces = getReportGenerators();
        for (ReportGeneratorInterface reportGeneratorInterface : reportGeneratorInterfaces) {
            if(reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)){
                 response = reportGeneratorInterface.generateReport(RecapConstants.SUBMIT_COLLECTION, getReportEntityList(reportRecordNumberList));
            }
        }
        return response;
    }

    private List<ReportEntity> getReportEntityList(List<Integer> reportRecordNumberList) {
        return reportDetailRepository.findByRecordNumberIn(reportRecordNumberList);
    }

    private String getFileNameLike(String fileName) {
        return fileName+"%";
    }
}
