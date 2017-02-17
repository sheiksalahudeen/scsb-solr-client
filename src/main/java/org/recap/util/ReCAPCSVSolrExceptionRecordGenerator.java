package org.recap.util;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.recap.RecapConstants;
import org.recap.model.csv.SolrExceptionReportReCAPCSVRecord;
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
 * Created by angelind on 22/8/16.
 */
public class ReCAPCSVSolrExceptionRecordGenerator {

    Logger logger= LoggerFactory.getLogger(ReCAPCSVSolrExceptionRecordGenerator.class);

    public SolrExceptionReportReCAPCSVRecord prepareMatchingReportReCAPCSVRecord(ReportEntity reportEntity, SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord) {

        List<ReportDataEntity> reportDataEntities = reportEntity.getReportDataEntities();

        for (Iterator<ReportDataEntity> iterator = reportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity report =  iterator.next();
            String headerName = report.getHeaderName();
            String headerValue = report.getHeaderValue();
            Method setterMethod = getSetterMethod(headerName);
            if(null != setterMethod){
                try {
                    setterMethod.invoke(solrExceptionReportReCAPCSVRecord, headerValue);
                } catch (Exception e) {
                    logger.error(RecapConstants.LOG_ERROR,e);
                }
            }
        }
        return solrExceptionReportReCAPCSVRecord;
    }

    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SolrExceptionReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }

    public Method getGetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getReadMethod(new PropertyDescriptor(propertyName, SolrExceptionReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }
}
