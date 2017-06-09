package org.recap.report;

import org.recap.model.jpa.ReportEntity;

import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */
public interface ReportGeneratorInterface {

    /**
     * Is interested boolean.
     *
     * @param reportType the report type
     * @return the boolean
     */
    boolean isInterested(String reportType);

    /**
     * Is transmitted boolean.
     *
     * @param transmissionType the transmission type
     * @return the boolean
     */
    boolean isTransmitted(String transmissionType);

    /**
     * Generate report string.
     *
     * @param fileName         the file name
     * @param reportEntityList the report entity list
     * @return the string
     */
    String generateReport(String fileName, List<ReportEntity> reportEntityList);

}
