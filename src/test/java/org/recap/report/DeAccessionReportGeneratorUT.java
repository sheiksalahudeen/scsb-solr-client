package org.recap.report;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.deAccession.DeAccessionDBResponseEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.service.deAccession.DeAccessionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 25/1/17.
 */
public class DeAccessionReportGeneratorUT extends BaseTestCase{

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    DeAccessionService deAccessionService;

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
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(RecapConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST);

        List<ReportEntity> reportEntities = deAccessionService.processAndSave(Arrays.asList(deAccessionDBResponseEntity));
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