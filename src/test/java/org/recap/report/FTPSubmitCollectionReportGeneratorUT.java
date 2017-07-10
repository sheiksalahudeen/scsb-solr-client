package org.recap.report;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by akulak on 30/5/17.
 */
public class FTPSubmitCollectionReportGeneratorUT extends BaseTestCase{

    @InjectMocks
    @Spy
    FTPSubmitCollectionReportGenerator ftpSubmitCollectionReportGenerator;

    @InjectMocks
    @Spy
    FTPSubmitCollectionSummaryReportGenerator ftpSubmitCollectionSummaryReportGenerator;

    @Mock
    private ReportDetailRepository reportDetailRepository;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    CamelContext camelContext;

    @Test
    public void testGenerateReport() throws Exception{
        camelContext.getEndpoint(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q, MockEndpoint.class);
        boolean isInterested = ftpSubmitCollectionReportGenerator.isInterested(RecapConstants.SUBMIT_COLLECTION);
        assertTrue(isInterested);
        boolean isTransmitted = ftpSubmitCollectionReportGenerator.isTransmitted(RecapConstants.FTP);
        assertTrue(isTransmitted);
        String response = ftpSubmitCollectionReportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION,saveSubmitCollectionExceptionReport(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT));
        assertNotNull(response);
        assertEquals("Success",response);
    }

    @Test
    public void testSubmitCollectionSummaryReportGenerator() throws Exception{
        camelContext.getEndpoint(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q, MockEndpoint.class);
        boolean isInterested = ftpSubmitCollectionSummaryReportGenerator.isInterested(RecapConstants.SUBMIT_COLLECTION_SUMMARY);
        assertTrue(isInterested);
        boolean isTransmitted = ftpSubmitCollectionSummaryReportGenerator.isTransmitted(RecapConstants.FTP);
        assertTrue(isTransmitted);
        String response = ftpSubmitCollectionSummaryReportGenerator.generateReport(RecapConstants.SUBMIT_COLLECTION,saveSubmitCollectionExceptionReport(RecapConstants.SUBMIT_COLLECTION_SUMMARY));
        assertNotNull(response);
    }

    private List<ReportEntity> saveSubmitCollectionExceptionReport(String reportType){
        List<ReportEntity> reportEntityList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.SUBMIT_COLLECTION_REPORT);
        reportEntity.setType(reportType);
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
        return reportEntityList;
    }
}
