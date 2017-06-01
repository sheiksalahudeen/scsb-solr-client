package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.recap.RecapConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akulak on 30/5/17.
 */
@Component
public class FTPSubmitCollectionReportGenerator implements ReportGeneratorInterface{

    @Autowired
    private ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(RecapConstants.SUBMIT_COLLECTION) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(RecapConstants.FTP) ? true : false;
    }

    @Override
    public String generateReport(String fileName, List<ReportEntity> reportEntityList) {
        List<SubmitCollectionReportRecord> submitCollectionReportRecordList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(reportEntityList)){
            for (ReportEntity reportEntity : reportEntityList) {
                SubmitCollectionReportRecord submitCollectionReportRecord = getSubmitCollectionReportRecord(reportEntity);
                submitCollectionReportRecordList.add(submitCollectionReportRecord);
            }
        }
        if(CollectionUtils.isNotEmpty(submitCollectionReportRecordList)){
            fileName = RecapConstants.SUBMIT_COLLECTION+submitCollectionReportRecordList.get(0).getOwningInstitution();
            producerTemplate.sendBodyAndHeader(RecapConstants.FTP_SUBMIT_COLLECTION_REPORT_Q,submitCollectionReportRecordList,"fileName",fileName);
            return RecapConstants.SUCCESS;
        }
        else {
            return RecapConstants.ERROR;
        }
    }

    private SubmitCollectionReportRecord getSubmitCollectionReportRecord(ReportEntity reportEntity) {
        SubmitCollectionReportRecord submitCollectionReportRecord = new SubmitCollectionReportRecord();
        for(ReportDataEntity reportDataEntity :reportEntity.getReportDataEntities()){
            if (RecapConstants.ITEM_BARCODE.equalsIgnoreCase(reportDataEntity.getHeaderName())){
                submitCollectionReportRecord.setItemBarcode(reportDataEntity.getHeaderValue());
            }
            else if(RecapConstants.CUSTOMER_CODE.equalsIgnoreCase(reportDataEntity.getHeaderName())){
                submitCollectionReportRecord.setCustomerCode(reportDataEntity.getHeaderValue());
            }
            else if(RecapConstants.OWNING_INSTITUTION.equalsIgnoreCase(reportDataEntity.getHeaderName())){
                submitCollectionReportRecord.setOwningInstitution(reportDataEntity.getHeaderValue());
            }else if(RecapConstants.MESSAGE.equalsIgnoreCase(reportDataEntity.getHeaderName())){
                submitCollectionReportRecord.setMessage(reportDataEntity.getHeaderValue());
            }
        }
        return submitCollectionReportRecord;
    }

}
