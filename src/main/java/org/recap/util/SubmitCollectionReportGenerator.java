package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.model.csv.SubmitCollectionRejectionRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hemalathas on 20/12/16.
 */
public class SubmitCollectionReportGenerator {

    public SubmitCollectionRejectionRecord prepareSubmitCollectionRejectionRecord(ReportEntity reportEntity) {

        SubmitCollectionRejectionRecord submitCollectionRejectionRecord = new SubmitCollectionRejectionRecord();
        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerValue = report.getHeaderValue();
            String headerName = report.getHeaderName();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(submitCollectionRejectionRecord, headerValue);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return submitCollectionRejectionRecord;
    }

    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            Method writeMethod = propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SubmitCollectionRejectionRecord.class));
            return writeMethod;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            Method writeMethod = propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, SubmitCollectionRejectionRecord.class));
            return writeMethod;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
