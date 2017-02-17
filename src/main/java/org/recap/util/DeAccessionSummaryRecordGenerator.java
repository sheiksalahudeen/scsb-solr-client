package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.DeAccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
public class DeAccessionSummaryRecordGenerator {

    Logger logger = LoggerFactory.getLogger(DeAccessionSummaryRecordGenerator.class);

    public DeAccessionSummaryRecord prepareDeAccessionSummaryReportRecord(ReportEntity reportEntity) {

        DeAccessionSummaryRecord deAccessionSummaryRecord = new DeAccessionSummaryRecord();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerValue = report.getHeaderValue();
            String headerName = report.getHeaderName();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(deAccessionSummaryRecord, headerValue);
                } catch (Exception e) {
                    logger.error(RecapConstants.LOG_ERROR,e);
                }
            }
        }
        return deAccessionSummaryRecord;
    }

    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, DeAccessionSummaryRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }

    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, DeAccessionSummaryRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }
}
