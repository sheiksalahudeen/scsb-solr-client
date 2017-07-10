package org.recap.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/1/17.
 */
public class AccessionReportGeneratorUT extends BaseTestCase{
    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testAccessionSummaryReportForFileSystem() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity(RecapConstants.ACCESSION_SUMMARY_REPORT);
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ACCESSION_SUMMARY_REPORT, RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testAccessionSummaryReportForFTP() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity(RecapConstants.ACCESSION_SUMMARY_REPORT);
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ACCESSION_SUMMARY_REPORT, RecapConstants.FTP, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testOngoingAccessionSummaryReportForFileSystem() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity(RecapConstants.ONGOING_ACCESSION_REPORT);
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ONGOING_ACCESSION_REPORT, RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testOngoingAccessionSummaryReportForFTP() throws Exception{
        List<ReportEntity> reportEntityList = saveSummaryReportEntity(RecapConstants.ONGOING_ACCESSION_REPORT);
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.ACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.ONGOING_ACCESSION_REPORT, RecapConstants.FTP, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }


    private List<ReportEntity> saveSummaryReportEntity(String reportType){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(reportType);
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

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName(RecapConstants.NUMBER_OF_BIB_MATCHES);
        existsBibCountReportDataEntity.setHeaderValue(String.valueOf(0));
        reportDataEntities.add(existsBibCountReportDataEntity);

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