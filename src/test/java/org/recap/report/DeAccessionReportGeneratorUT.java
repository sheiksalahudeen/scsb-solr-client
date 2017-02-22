package org.recap.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 25/1/17.
 */
public class DeAccessionReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Test
    public void FSDeAccessionReportGenerator() throws InterruptedException {
        List<ReportEntity> reportEntities = getReportEntity();
        Date createdDate = reportEntities.get(0).getCreatedDate();
        String generatedReportFileNameInFileSyatem = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FILE_SYSTEM, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFileSyatem);
    }

    @Test
    public void FTPDeAccessionReportGenerator() throws InterruptedException {
        List<ReportEntity> reportEntities = getReportEntity();
        Date createdDate = reportEntities.get(0).getCreatedDate();
        String generatedReportFileNameInFileSyatem = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FTP, getFromDate(createdDate), getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFileSyatem);
    }

    private List<ReportEntity> getReportEntity(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.DEACCESSION_REPORT);
        reportEntity.setType(RecapConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(RecapConstants.DATE_OF_DEACCESSION);
        dateReportDataEntity.setHeaderValue(formatter.format(new Date()));
        reportDataEntities.add(dateReportDataEntity);

        ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
        owningInstitutionReportDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        owningInstitutionReportDataEntity.setHeaderValue("PUL");
        reportDataEntities.add(owningInstitutionReportDataEntity);

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(RecapConstants.BARCODE);
        barcodeReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(barcodeReportDataEntity);

        ReportDataEntity owningInstitutionBibIdReportDataEntity = new ReportDataEntity();
        owningInstitutionBibIdReportDataEntity.setHeaderName(RecapConstants.OWNING_INST_BIB_ID);
        owningInstitutionBibIdReportDataEntity.setHeaderValue("3456");
        reportDataEntities.add(owningInstitutionBibIdReportDataEntity);

        ReportDataEntity collectionGroupCodeReportDataEntity = new ReportDataEntity();
        collectionGroupCodeReportDataEntity.setHeaderName(RecapConstants.COLLECTION_GROUP_CODE);
        collectionGroupCodeReportDataEntity.setHeaderValue("Private");
        reportDataEntities.add(collectionGroupCodeReportDataEntity);

        ReportDataEntity statusReportDataEntity = new ReportDataEntity();
        statusReportDataEntity.setHeaderName(RecapConstants.STATUS);
        statusReportDataEntity.setHeaderValue("Success");
        reportDataEntities.add(statusReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntities.add(reportEntity);
        return reportEntities;
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

}