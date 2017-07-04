package org.recap.matchingalgorithm.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Value("${scsb.collection.report.directory}")
    String reportsDirectory;

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testMatchingReportForFileSystem() throws Exception {
        saveAccessionSummaryReportEntity();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ACCESSION_SUMMARY_REPORT, RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(new Date()), dateUtil.getToDate(new Date()));
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println("Total Time taken to generate matching report : " + stopWatch.getTotalTimeSeconds());
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testMatchingReportForFTP() throws Exception {
        saveAccessionSummaryReportEntity();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ACCESSION_SUMMARY_REPORT, RecapConstants.FTP, dateUtil.getFromDate(new Date()), dateUtil.getToDate(new Date()));
        Thread.sleep(1000);
        stopWatch.stop();
        System.out.println("Total Time taken to generate matching report : " + stopWatch.getTotalTimeSeconds());
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testReportDataEntity(){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setRecordNumber(1);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");
        reportEntity.setFileName("Accession");
        reportEntity.setType("Accession");
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setReportDataId(1);
        reportDataEntity.setRecordNum("10");
        reportDataEntity.setHeaderName("ItemBarcode");
        reportDataEntity.setHeaderValue("3328456458454714");
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        assertNotNull(reportEntity.getRecordNumber());
        assertNotNull(reportEntity.getFileName());
        assertNotNull(reportEntity.getReportDataEntities());
        assertNotNull(reportEntity.getType());
        assertNotNull(reportEntity.getCreatedDate());
        assertNotNull(reportEntity.getInstitutionName());
        assertNotNull(reportDataEntity.getRecordNum());
        assertNotNull(reportDataEntity.getReportDataId());
        assertNotNull(reportDataEntity.getHeaderName());
        assertNotNull(reportDataEntity.getHeaderValue());
    }

    private List<ReportEntity> saveAccessionSummaryReportEntity(){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_SUCCESS_COUNT);
        successBibCountReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_SUCCESS_COUNT);
        successItemCountReportDataEntity.setHeaderValue(String.valueOf(1));
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName(RecapConstants.BIB_FAILURE_COUNT);
        failedBibCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName(RecapConstants.ITEM_FAILURE_COUNT);
        failedItemCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_BIB_REASON);
        reasonForBibFailureReportDataEntity.setHeaderValue("");
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName(RecapConstants.FAILURE_ITEM_REASON);
        reasonForItemFailureReportDataEntity.setHeaderValue("");
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        return reportDetailRepository.save(reportEntityList);

    }

}