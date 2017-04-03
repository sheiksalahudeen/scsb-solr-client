package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.SubmitCollectionReportRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hemalathas on 20/12/16.
 */
public class SubmitCollectionReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SubmitCollectionReportGenerator.class);

    public List<SubmitCollectionReportRecord> prepareSubmitCollectionRejectionRecord(ReportEntity reportEntity) {

        List<SubmitCollectionReportRecord> submitCollectionReportRecordList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        SubmitCollectionReportRecord submitCollectionReportRecord = null;
        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            if((null == submitCollectionReportRecord && submitCollectionReportRecordList.isEmpty())
                    || (isReportRecordFullyUpdated(submitCollectionReportRecord))){
                submitCollectionReportRecord = new SubmitCollectionReportRecord();
            }
            ReportDataEntity report =  iterator.next();
            String headerValue = report.getHeaderValue();
            String headerName = report.getHeaderName();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(submitCollectionReportRecord, headerValue);
                } catch (Exception e) {
                    logger.error(RecapConstants.LOG_ERROR,e);
                }
                if(isReportRecordFullyUpdated(submitCollectionReportRecord) ) {
                    submitCollectionReportRecordList.add(submitCollectionReportRecord);
                }
            }
        }
        return submitCollectionReportRecordList;
    }

    private boolean isReportRecordFullyUpdated(SubmitCollectionReportRecord submitCollectionReportRecord){
        boolean newReportObject = true;
        newReportObject &= (null != submitCollectionReportRecord);
        newReportObject &= (null != submitCollectionReportRecord.getCustomerCode());
        newReportObject &= (null != submitCollectionReportRecord.getItemBarcode());
        newReportObject &= (null != submitCollectionReportRecord.getOwningInstitution());
        newReportObject &= (null != submitCollectionReportRecord.getMessage());
        return newReportObject;
    }
    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SubmitCollectionReportRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }

    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, SubmitCollectionReportRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }
}
