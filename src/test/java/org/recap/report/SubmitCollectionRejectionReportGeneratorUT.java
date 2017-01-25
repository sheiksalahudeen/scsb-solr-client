package org.recap.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/1/17.
 */
public class SubmitCollectionRejectionReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Test
    public void testFSSubmitCollectionExceptionReport() throws InterruptedException {
        List<ReportEntity> reportEntityList = saveSubmitCollectionExceptionReport();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION_REPORT,"PUL", RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT,RecapConstants.FILE_SYSTEM,getFromDate(createdDate),getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testFTPSubmitCollectionExceptionReport() throws InterruptedException {
        List<ReportEntity> reportEntityList = saveSubmitCollectionExceptionReport();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION_REPORT,"PUL", RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT,RecapConstants.FTP,getFromDate(createdDate),getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private List<ReportEntity> saveSubmitCollectionExceptionReport(){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.SUBMIT_COLLECTION_REPORT);
        reportEntity.setType(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity itemBarcodeReportDataEntity = new ReportDataEntity();
        itemBarcodeReportDataEntity.setHeaderName(RecapConstants.SUBMIT_COLLECTION_ITEM_BARCODE);
        itemBarcodeReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(itemBarcodeReportDataEntity);

        ReportDataEntity customerCodeReportDataEntity = new ReportDataEntity();
        customerCodeReportDataEntity.setHeaderName(RecapConstants.SUBMIT_COLLECTION_CUSTOMER_CODE);
        customerCodeReportDataEntity.setHeaderValue("PB");
        reportDataEntities.add(customerCodeReportDataEntity);

        ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
        owningInstitutionReportDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        owningInstitutionReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(owningInstitutionReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        reportEntityList.add(reportEntity);
        return reportDetailRepository.save(reportEntityList);

    }

}