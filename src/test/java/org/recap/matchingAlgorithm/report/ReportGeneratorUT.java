package org.recap.matchingAlgorithm.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 23/8/16.
 */
public class ReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Value("${solr.report.directory}")
    String matchingReportsDirectory;

    @Autowired
    ReportGenerator reportGenerator;

    @Test
    public void testMatchingReportForFileSystem() throws Exception {
        ReportEntity reportEntity1 = saveMatchingReportEntity();
        saveMatchingReportEntity();
        saveMatchingReportEntity();

        Date createdDate = reportEntity1.getCreatedDate();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.MATCHING_TYPE, RecapConstants.FILE_SYSTEM, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println("Total Time taken to generate matching report : " + stopWatch.getTotalTimeSeconds());
        assertNotNull(generatedReportFileName);
        File file = new File(matchingReportsDirectory + File.separator + generatedReportFileName);
        assertTrue(file.exists());
    }

    @Test
    public void testExceptionReportForFileSystem() throws Exception {
        ReportEntity reportEntity1 = saveExceptionReportEntity();
        ReportEntity reportEntity2 = saveExceptionReportEntity();

        Date createdDate = reportEntity1.getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_FILE_NAME, RecapConstants.EXCEPTION_TYPE, RecapConstants.FILE_SYSTEM, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);

        assertNotNull(generatedReportFileName);
        File file = new File(matchingReportsDirectory + File.separator + generatedReportFileName);
        assertTrue(file.exists());
    }

    @Test
    public void testMatchingReportForFTP() throws Exception {
        ReportEntity reportEntity1 = saveMatchingReportEntity();
        ReportEntity reportEntity2 = saveMatchingReportEntity();
        ReportEntity reportEntity3 = saveMatchingReportEntity();

        Date createdDate = reportEntity1.getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.MATCHING_TYPE, RecapConstants.FTP, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);

        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testExceptionReportForFTP() throws Exception {
        ReportEntity reportEntity1 = saveExceptionReportEntity();
        ReportEntity reportEntity2 = saveExceptionReportEntity();

        Date createdDate = reportEntity1.getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.EXCEPTION_REPORT_FILE_NAME, RecapConstants.EXCEPTION_TYPE, RecapConstants.FTP, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);

        assertNotNull(generatedReportFileName);
    }

    private Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    private Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private ReportEntity saveExceptionReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.EXCEPTION_REPORT_FILE_NAME);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.EXCEPTION_TYPE);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(RecapConstants.MATCHING_BIB_ID);
        reportDataEntity1.setHeaderValue("1");
        reportDataEntities.add(reportDataEntity1);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName(RecapConstants.MATCHING_INSTITUTION_ID);
        reportDataEntity2.setHeaderValue("NYPL");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName(RecapConstants.MATCHING_BARCODE);
        reportDataEntity3.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName(RecapConstants.MATCHING_OCLC);
        reportDataEntity4.setHeaderValue("213654");
        reportDataEntities.add(reportDataEntity4);

        ReportDataEntity reportDataEntity5 = new ReportDataEntity();
        reportDataEntity5.setHeaderName(RecapConstants.MATCHING_USE_RESTRICTIONS);
        reportDataEntity5.setHeaderValue("In Library Use");
        reportDataEntities.add(reportDataEntity5);

        ReportDataEntity reportDataEntity6 = new ReportDataEntity();
        reportDataEntity6.setHeaderName(RecapConstants.MATCHING_SUMMARY_HOLDINGS);
        reportDataEntity6.setHeaderValue("no.1 18292938");
        reportDataEntities.add(reportDataEntity6);

        ReportDataEntity reportDataEntity7 = new ReportDataEntity();
        reportDataEntity7.setHeaderName(RecapConstants.MATCHING_TITLE);
        reportDataEntity7.setHeaderValue("Testing the Matching Algorithm");
        reportDataEntities.add(reportDataEntity7);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);
    }

    private ReportEntity saveMatchingReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setFileName(RecapConstants.MATCHING_ALGO_FULL_FILE_NAME);
        reportEntity.setType(RecapConstants.MATCHING_TYPE);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(RecapConstants.MATCHING_BIB_ID);
        reportDataEntity1.setHeaderValue("1");
        reportDataEntities.add(reportDataEntity1);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName(RecapConstants.MATCHING_INSTITUTION_ID);
        reportDataEntity2.setHeaderValue("NYPL");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName(RecapConstants.MATCHING_BARCODE);
        reportDataEntity3.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName(RecapConstants.MATCHING_OCLC);
        reportDataEntity4.setHeaderValue("213654");
        reportDataEntities.add(reportDataEntity4);

        ReportDataEntity reportDataEntity5 = new ReportDataEntity();
        reportDataEntity5.setHeaderName(RecapConstants.MATCHING_USE_RESTRICTIONS);
        reportDataEntity5.setHeaderValue("In Library Use");
        reportDataEntities.add(reportDataEntity5);

        ReportDataEntity reportDataEntity6 = new ReportDataEntity();
        reportDataEntity6.setHeaderName(RecapConstants.MATCHING_SUMMARY_HOLDINGS);
        reportDataEntity6.setHeaderValue("no.1 18292938");
        reportDataEntities.add(reportDataEntity6);

        ReportDataEntity reportDataEntity7 = new ReportDataEntity();
        reportDataEntity7.setHeaderName(RecapConstants.MATCHING_TITLE);
        reportDataEntity7.setHeaderValue("Testing the Matching Algorithm");
        reportDataEntities.add(reportDataEntity7);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);
    }

}