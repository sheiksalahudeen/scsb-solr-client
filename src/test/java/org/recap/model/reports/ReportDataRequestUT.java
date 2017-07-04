package org.recap.model.reports;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 3/7/17.
 */
public class ReportDataRequestUT extends BaseTestCase{

    @Test
    public void testReportDataRequest(){
        ReportDataRequest reportDataRequest = new ReportDataRequest();
        reportDataRequest.setFileName("Accession_Summary_Report");
        reportDataRequest.setInstitutionCode("PUL");
        reportDataRequest.setReportType("Summary");
        reportDataRequest.setTransmissionType("FTP");
        assertNotNull(reportDataRequest.getFileName());
        assertNotNull(reportDataRequest.getInstitutionCode());
        assertNotNull(reportDataRequest.getReportType());
        assertNotNull(reportDataRequest.getTransmissionType());
    }

}