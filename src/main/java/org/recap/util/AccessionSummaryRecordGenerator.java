package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.model.csv.AccessionSummaryRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hemalathas on 22/11/16.
 */
public class AccessionSummaryRecordGenerator {

    public AccessionSummaryRecord prepareAccessionSummaryReportRecord(ReportEntity reportEntity){
        AccessionSummaryRecord accessionSummaryRecord = new AccessionSummaryRecord();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> reportDataEntityIterator = reportDataEntities.iterator(); reportDataEntityIterator.hasNext(); ) {
            ReportDataEntity reportDataEntity = reportDataEntityIterator.next();
            String headerValue = reportDataEntity.getHeaderValue();
            String headerName = reportDataEntity.getHeaderName();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(accessionSummaryRecord, headerValue);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return accessionSummaryRecord;
    }

    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            Method writeMethod = propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, AccessionSummaryRecord.class));
            return writeMethod;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            Method writeMethod = propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, AccessionSummaryRecord.class));
            return writeMethod;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
