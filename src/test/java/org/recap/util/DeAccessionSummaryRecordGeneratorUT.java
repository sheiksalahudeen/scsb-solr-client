package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.csv.DeAccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/2/17.
 */
public class DeAccessionSummaryRecordGeneratorUT extends BaseTestCase{

    @Test
    public void testDeaccessionSummaryRecord(){
        DeAccessionSummaryRecordGenerator deAccessionSummaryRecordGenerator = new DeAccessionSummaryRecordGenerator();
        ReportEntity reportEntity = getReportEntity();
        DeAccessionSummaryRecord deAccessionSummaryRecord = deAccessionSummaryRecordGenerator.prepareDeAccessionSummaryReportRecord(reportEntity);
        assertNotNull(deAccessionSummaryRecord);
        assertEquals(deAccessionSummaryRecord.getBarcode(),"123");
        assertEquals(deAccessionSummaryRecord.getCollectionGroupCode(),"Shared");
        assertEquals(deAccessionSummaryRecord.getTitle(),"test");
        assertEquals(deAccessionSummaryRecord.getStatus(),"Available");
        assertEquals(deAccessionSummaryRecord.getOwningInstitution(),"PUL");

    }

    private ReportEntity getReportEntity(){
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.DEACCESSION_REPORT);
        reportEntity.setType(RecapConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName("dateOfDeAccession");
        successBibCountReportDataEntity.setHeaderValue(new Date().toString());
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName("owningInstitution");
        successItemCountReportDataEntity.setHeaderValue("PUL");
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName("barcode");
        existsBibCountReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(existsBibCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName("owningInstitutionBibId");
        failedBibCountReportDataEntity.setHeaderValue("124566");
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName("title");
        failedItemCountReportDataEntity.setHeaderValue("test");
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName("collectionGroupCode");
        reasonForBibFailureReportDataEntity.setHeaderValue("Shared");
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity status = new ReportDataEntity();
        status.setHeaderName("status");
        status.setHeaderValue("Available");
        reportDataEntities.add(status);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName("reasonForFailure");
        reasonForItemFailureReportDataEntity.setHeaderValue(RecapConstants.ITEM_BARCDE_DOESNOT_EXIST);
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;

    }

}