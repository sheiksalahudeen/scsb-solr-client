package org.recap.matchingAlgorithm.report;

import org.recap.model.jpa.ReportEntity;

import java.util.List;

/**
 * Created by angelind on 23/8/16.
 */
public interface ReportGeneratorInterface {

    boolean isInterested(String reportType);

    boolean isTransmitted(String transmissionType);

    String generateReport(String fileName, List<ReportEntity> reportEntityList);

}
