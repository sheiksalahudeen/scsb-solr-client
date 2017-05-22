package org.recap.matchingalgorithm.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.CSVSummaryReportGenerator;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 13/9/16.
 */
public class CSVSummaryReportGeneratorUT extends BaseTestCase{


    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    CSVSummaryReportGenerator csvSummaryReportGenerator;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testSummaryReportForFileSystem() throws Exception{
        ReportEntity reportEntity1 = saveSummaryReportEntity();
        Date createdDate = reportEntity1.getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.SUMMARY_TYPE, RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);

        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testSummaryReportForFtp() throws Exception{
        ReportEntity reportEntity1 = saveSummaryReportEntity();
        Date createdDate = reportEntity1.getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUMMARY_REPORT_FILE_NAME, RecapConstants.ALL_INST, RecapConstants.SUMMARY_TYPE, RecapConstants.FTP, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);

        assertNotNull(generatedReportFileName);
    }

    private ReportEntity saveSummaryReportEntity(){
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setFileName(RecapConstants.SUMMARY_REPORT_FILE_NAME);
        reportEntity.setType(RecapConstants.SUMMARY_TYPE);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(RecapConstants.SUMMARY_NUM_BIBS_IN_TABLE);
        reportDataEntity1.setHeaderValue("1");
        reportDataEntities.add(reportDataEntity1);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName(RecapConstants.SUMMARY_NUM_ITEMS_IN_TABLE);
        reportDataEntity2.setHeaderValue("2");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName(RecapConstants.SUMMARY_MATCHING_KEY_FIELD);
        reportDataEntity3.setHeaderValue(RecapConstants.OCLC_TAG);
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName(RecapConstants.SUMMARY_MATCHING_BIB_COUNT);
        reportDataEntity4.setHeaderValue("3");
        reportDataEntities.add(reportDataEntity4);

        ReportDataEntity reportDataEntity5 = new ReportDataEntity();
        reportDataEntity5.setHeaderName(RecapConstants.SUMMARY_NUM_ITEMS_AFFECTED);
        reportDataEntity5.setHeaderValue("4");
        reportDataEntities.add(reportDataEntity5);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);

    }

}