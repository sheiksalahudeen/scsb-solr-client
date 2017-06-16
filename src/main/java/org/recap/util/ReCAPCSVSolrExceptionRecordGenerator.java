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

    private static final Logger logger= LoggerFactory.getLogger(ReCAPCSVSolrExceptionRecordGenerator.class);

    /**
     * This method prepares SolrExceptionReportReCAPCSVRecord which is used to generate csv report based on the given report entity
     *
     * @param reportEntity                      the report entity
     * @param solrExceptionReportReCAPCSVRecord the solr exception report re capcsv record
     * @return the solr exception report re capcsv record
     */
    public SolrExceptionReportReCAPCSVRecord prepareSolrExceptionReportReCAPCSVRecord(ReportEntity reportEntity, SolrExceptionReportReCAPCSVRecord solrExceptionReportReCAPCSVRecord) {

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

    /**
     * This method is used to get the setter method for the given one of the instance variable name in SolrExceptionReportReCAPCSVRecord class.
     *
     * @param propertyName the property name
     * @return the setter method
     */
    public Method getSetterMethod(String propertyName) {
        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        try {
            return propertyUtilsBean.getWriteMethod(new PropertyDescriptor(propertyName, SolrExceptionReportReCAPCSVRecord.class));
        } catch (IntrospectionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return null;
    }

    /**
     * This method is used to get the getter method for the given one of the instance variable name in SolrExceptionReportReCAPCSVRecord class.
     *
     * @param propertyName the property name
     * @return the getter method
     */
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
