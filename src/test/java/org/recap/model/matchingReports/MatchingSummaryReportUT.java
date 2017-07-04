package org.recap.model.matchingReports;

import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 4/7/17.
 */
public class MatchingSummaryReportUT extends BaseTestCase{

    @Test
    public void testMatchingSummaryReport(){
        MatchingSummaryReport matchingSummaryReport = new MatchingSummaryReport();
        matchingSummaryReport.setInstitution("PUL");
        matchingSummaryReport.setTotalBibs("12");
        matchingSummaryReport.setTotalItems("23");
        matchingSummaryReport.setSharedItemsBeforeMatching("5");
        matchingSummaryReport.setOpenItemsBeforeMatching("1");
        matchingSummaryReport.setSharedItemsAfterMatching("2");
        matchingSummaryReport.setOpenItemsAfterMatching("1");

        assertNotNull(matchingSummaryReport.getInstitution());
        assertNotNull(matchingSummaryReport.getTotalBibs());
        assertNotNull(matchingSummaryReport.getTotalItems());
        assertNotNull(matchingSummaryReport.getSharedItemsBeforeMatching());
        assertNotNull(matchingSummaryReport.getOpenItemsBeforeMatching());
        assertNotNull(matchingSummaryReport.getSharedItemsAfterMatching());
        assertNotNull(matchingSummaryReport.getOpenItemsAfterMatching());


    }

}