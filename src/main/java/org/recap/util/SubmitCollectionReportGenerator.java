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

    /**
     * This method prepares submit collection rejection report based on the given report entity.
     *
     * @param reportEntity the report entity
     * @return the SubmitCollectionReportRecord list
     */
    public List<SubmitCollectionReportRecord> prepareSubmitCollectionRejectionRecord(ReportEntity reportEntity) {

        List<SubmitCollectionReportRecord> submitCollectionReportRecordList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        SubmitCollectionReportRecord submitCollectionReportRecord = null;
        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            if((null == submitCollectionReportRecord && submitCollectionReportRecordList.isEmpty())
                    || (isReportRecordFullyUpdated(submitCollectionReportRecord))){
                submitCollectionReportRecord = new SubmitCollectionReportRecord();
                submitCollectionReportRecord.setReportType(getReportType(reportEntity.getType()));
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

    private String getReportType(String type){
        if(type.equals(RecapConstants.SUBMIT_COLLECTION_SUCCESS_REPORT)){
            return RecapConstants.SUCCESS;
        } else if(type.equals(RecapConstants.SUBMIT_COLLECTION_FAILURE_REPORT)){
            return RecapConstants.FAIL;
        } else if(type.equals(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_REPORT)){
            return RecapConstants.SC_EXCEPTION;
        } else if(type.equals(RecapConstants.SUBMIT_COLLECTION_REJECTION_REPORT)){
            return RecapConstants.REJECTION;
        }
        return null;
    }
    /**
     * This method is used to get the setter method for the given one of the instance variable name in SubmitCollectionReportRecord class.
     *
     * @param propertyName the property name
     * @return the setter method
     */
    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SubmitCollectionReportRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }

    /**
     * This method is used to get the getter method for the given one of the instance variable name in SubmitCollectionReportRecord class.
     *
     * @param propertyName the property name
     * @return the getter method
     */
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
