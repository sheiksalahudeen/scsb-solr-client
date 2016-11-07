package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.Holdings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class HoldingsJSONUtil extends MarcUtil {

    Logger logger = LoggerFactory.getLogger(HoldingsJSONUtil.class);

    private ProducerTemplate producerTemplate;

    public Holdings generateHoldingsForIndex(HoldingsEntity holdingsEntity) {
        try {
            Holdings holdings = new Holdings();
            holdings.setHoldingsId(holdingsEntity.getHoldingsId());
            holdings.setDocType(RecapConstants.HOLDINGS);
            holdings.setId(holdingsEntity.getOwningInstitutionId()+holdingsEntity.getOwningInstitutionHoldingsId());
            String holdingsContent = new String(holdingsEntity.getContent());
            List<Record> records = convertMarcXmlToRecord(holdingsContent);
            Record marcRecord = records.get(0);
            holdings.setSummaryHoldings(getDataFieldValue(marcRecord, "866", null, null, "a"));
            InstitutionEntity institutionEntity = holdingsEntity.getInstitutionEntity();
            String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : "";
            holdings.setOwningInstitution(institutionCode);
            holdings.setHoldingsCreatedBy(holdingsEntity.getCreatedBy());
            holdings.setHoldingsCreatedDate(holdingsEntity.getCreatedDate());
            holdings.setHoldingsLastUpdatedBy(holdingsEntity.getLastUpdatedBy());
            holdings.setHoldingsLastUpdatedDate(holdingsEntity.getLastUpdatedDate());
            holdings.setDeletedHoldings(holdingsEntity.isDeleted());
            return holdings;
        } catch (Exception e) {
            saveExceptionReportForHoldings(holdingsEntity, e);
        }
        return null;
    }

    private void saveExceptionReportForHoldings(HoldingsEntity holdingsEntity, Exception e) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.SOLR_INDEX_EXCEPTION);
        reportEntity.setFileName(RecapConstants.SOLR_INDEX_FAILURE_REPORT);
        InstitutionEntity institutionEntity = holdingsEntity.getInstitutionEntity();
        String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : RecapConstants.NA;
        reportEntity.setInstitutionName(institutionCode);

        ReportDataEntity docTypeDataEntity = new ReportDataEntity();
        docTypeDataEntity.setHeaderName(RecapConstants.DOCTYPE);
        docTypeDataEntity.setHeaderValue(RecapConstants.HOLDINGS);
        reportDataEntities.add(docTypeDataEntity);

        ReportDataEntity owningInstDataEntity = new ReportDataEntity();
        owningInstDataEntity.setHeaderName(RecapConstants.OWNING_INSTITUTION);
        owningInstDataEntity.setHeaderValue(institutionCode);
        reportDataEntities.add(owningInstDataEntity);

        ReportDataEntity exceptionMsgDataEntity = new ReportDataEntity();
        exceptionMsgDataEntity.setHeaderName(RecapConstants.EXCEPTION_MSG);
        exceptionMsgDataEntity.setHeaderValue(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : e.toString());
        reportDataEntities.add(exceptionMsgDataEntity);

        if(holdingsEntity.getHoldingsId() != null) {
            ReportDataEntity holdingsIdDataEntity = new ReportDataEntity();
            holdingsIdDataEntity.setHeaderName(RecapConstants.HOLDINGS_ID);
            holdingsIdDataEntity.setHeaderValue(String.valueOf(holdingsEntity.getHoldingsId()));
            reportDataEntities.add(holdingsIdDataEntity);
        }

        reportEntity.addAll(reportDataEntities);
        producerTemplate.sendBody(RecapConstants.REPORT_Q, reportEntity);
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }
}
