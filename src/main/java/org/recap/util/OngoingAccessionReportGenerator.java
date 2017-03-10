package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.OngoingAccessionReportRecord;
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
 * Created by premkb on 07/02/17.
 */
public class OngoingAccessionReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OngoingAccessionReportGenerator.class);

    public List<OngoingAccessionReportRecord> prepareOngoingAccessionReportRecord(ReportEntity reportEntity) {

        List<OngoingAccessionReportRecord> ongoingAccessionReportRecordList = new ArrayList<>();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        OngoingAccessionReportRecord ongoingAccessionReportRecord = null;
        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            if (ongoingAccessionReportRecordList.isEmpty() && ongoingAccessionReportRecord == null) {
                ongoingAccessionReportRecord = new OngoingAccessionReportRecord();
            } else if(ongoingAccessionReportRecord.getCustomerCode()!=null && ongoingAccessionReportRecord.getItemBarcode()!=null && ongoingAccessionReportRecord.getMessage()!=null){
                ongoingAccessionReportRecord = new OngoingAccessionReportRecord();
            }
            ReportDataEntity report =  iterator.next();
            String headerValue = report.getHeaderValue();
            String headerName = report.getHeaderName();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(ongoingAccessionReportRecord, headerValue);
                } catch (Exception e) {
                    logger.error(RecapConstants.EXCEPTION,e);
                }
                if(ongoingAccessionReportRecord.getCustomerCode()!=null && ongoingAccessionReportRecord.getItemBarcode()!=null && ongoingAccessionReportRecord.getMessage()!=null) {
                    ongoingAccessionReportRecordList.add(ongoingAccessionReportRecord);
                }
            }
        }
        return ongoingAccessionReportRecordList;
    }

    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, OngoingAccessionReportRecord.class));
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
