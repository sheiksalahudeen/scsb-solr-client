package org.recap.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 25/1/17.
 */
public class DeAccessionReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void FSDeAccessionReportGenerator() throws InterruptedException {
        List<ReportEntity> reportEntities = getReportEntity();
        Date createdDate = reportEntities.get(0).getCreatedDate();
        String generatedReportFileNameInFileSyatem = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FILE_SYSTEM, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFileSyatem);
    }

    @Test
    public void FTPDeAccessionReportGenerator() throws InterruptedException {
        List<ReportEntity> reportEntities = getReportEntity();
        Date createdDate = reportEntities.get(0).getCreatedDate();
        String generatedReportFileNameInFileSystem = reportGenerator.generateReport(RecapConstants.DEACCESSION_REPORT, RecapConstants.PRINCETON, RecapConstants.DEACCESSION_SUMMARY_REPORT, RecapConstants.FTP, dateUtil.getFromDate(createdDate), dateUtil.getToDate(createdDate));
        Thread.sleep(1000);
        assertNotNull(generatedReportFileNameInFileSystem);
    }

    private List<ReportEntity> getReportEntity(){
        List<ReportEntity> reportEntities = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.DEACCESSION_REPORT);
        reportEntity.setType(RecapConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setInstitutionName(RecapConstants.PRINCETON);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(RecapConstants.DATE_OF_DEACCESSION);
        dateReportDataEntity.setHeaderValue(formatter.format(new Date()));
        reportDataEntities.add(dateReportDataEntity);

        ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
        owningInstitutionReportDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        owningInstitutionReportDataEntity.setHeaderValue(RecapConstants.PRINCETON);
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
        return reportDetailRepository.save(reportEntities);
    }

}