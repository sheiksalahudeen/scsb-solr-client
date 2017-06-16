package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.csv.SolrExceptionReportReCAPCSVRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/2/17.
 */
public class ReCAPCSVSolrExceptionRecordGeneratorUT extends BaseTestCase{

    @Test
    public void testSolrExceptionRecordGenerator(){
        SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord = new SolrExceptionReportReCAPCSVRecord();
        solrExceptionReportReCAPCSVRecord.setDocType("Test");
        solrExceptionReportReCAPCSVRecord.setOwningInstitutionBibId("124566");
        solrExceptionReportReCAPCSVRecord.setOwningInstitution("PUL");
        solrExceptionReportReCAPCSVRecord.setBibId("123");
        solrExceptionReportReCAPCSVRecord.setHoldingsId("231");
        solrExceptionReportReCAPCSVRecord.setItemId("1");
        solrExceptionReportReCAPCSVRecord.setExceptionMessage("Title is mandatory");
        ReportEntity reportEntity = getReportEntity();
        ReCAPCSVSolrExceptionRecordGenerator reCAPCSVSolrExceptionRecordGenerator = new ReCAPCSVSolrExceptionRecordGenerator();
        SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord1 = reCAPCSVSolrExceptionRecordGenerator.prepareSolrExceptionReportReCAPCSVRecord(reportEntity,solrExceptionReportReCAPCSVRecord);
        assertNotNull(solrExceptionReportReCAPCSVRecord1);
        assertEquals(solrExceptionReportReCAPCSVRecord1.getOwningInstitution(),"PUL");
        assertEquals(solrExceptionReportReCAPCSVRecord1.getExceptionMessage(),RecapConstants.ITEM_ALREADY_ACCESSIONED);
    }

    private ReportEntity getReportEntity(){
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ACCESSION_REPORT);
        reportEntity.setType(RecapConstants.ACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");

        ReportDataEntity successBibCountReportDataEntity = new ReportDataEntity();
        successBibCountReportDataEntity.setHeaderName("docType");
        successBibCountReportDataEntity.setHeaderValue("Test");
        reportDataEntities.add(successBibCountReportDataEntity);

        ReportDataEntity successItemCountReportDataEntity = new ReportDataEntity();
        successItemCountReportDataEntity.setHeaderName("owningInstitution");
        successItemCountReportDataEntity.setHeaderValue("PUL");
        reportDataEntities.add(successItemCountReportDataEntity);

        ReportDataEntity failedBibCountReportDataEntity = new ReportDataEntity();
        failedBibCountReportDataEntity.setHeaderName("owningInstitutionBibId");
        failedBibCountReportDataEntity.setHeaderValue("124566");
        reportDataEntities.add(failedBibCountReportDataEntity);

        ReportDataEntity existsBibCountReportDataEntity = new ReportDataEntity();
        existsBibCountReportDataEntity.setHeaderName("bibId");
        existsBibCountReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(existsBibCountReportDataEntity);

        ReportDataEntity failedItemCountReportDataEntity = new ReportDataEntity();
        failedItemCountReportDataEntity.setHeaderName("holdingsId");
        failedItemCountReportDataEntity.setHeaderValue("231");
        reportDataEntities.add(failedItemCountReportDataEntity);

        ReportDataEntity reasonForBibFailureReportDataEntity = new ReportDataEntity();
        reasonForBibFailureReportDataEntity.setHeaderName("itemId");
        reasonForBibFailureReportDataEntity.setHeaderValue("1");
        reportDataEntities.add(reasonForBibFailureReportDataEntity);

        ReportDataEntity reasonForItemFailureReportDataEntity = new ReportDataEntity();
        reasonForItemFailureReportDataEntity.setHeaderName("exceptionMessage");
        reasonForItemFailureReportDataEntity.setHeaderValue(RecapConstants.ITEM_ALREADY_ACCESSIONED);
        reportDataEntities.add(reasonForItemFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;

    }

}