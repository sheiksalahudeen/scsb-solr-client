package org.recap.matchingAlgorithm.report;

import java.util.Date;

/**
 * Created by angelind on 23/8/16.
 */
public interface ReportGeneratorInterface {

    boolean isInterested(String reportType);

    boolean isTransmitted(String transmissionType);

    String generateReport(String fileName, String type, Date from, Date to);

}
