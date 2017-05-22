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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 24/1/17.
 */
public class SubmitCollectionExceptionReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testFSSubmitCollectionExceptionReport() throws InterruptedException {
        List<ReportEntity> reportEntityList = saveSubmitCollectionExceptionReport();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION_REPORT,"PUL", RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT,RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
    }

    @Test
    public void testFTPSubmitCollectionExceptionReport() throws InterruptedException {
        List<ReportEntity> reportEntityList = saveSubmitCollectionExceptionReport();
        Date createdDate = reportEntityList.get(0).getCreatedDate();
        String generatedReportFileName = reportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION_REPORT,"PUL", RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT,RecapConstants.FTP, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileName);
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