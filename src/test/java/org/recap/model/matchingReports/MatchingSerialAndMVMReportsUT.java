package org.recap.model.matchingReports;

import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 4/7/17.
 */
public class MatchingSerialAndMVMReportsUT extends BaseTestCase{

    @Test
    public void testMatchingSerialAndMVMReports(){
        MatchingSerialAndMVMReports matchingSerialAndMVMReports = new MatchingSerialAndMVMReports();
        matchingSerialAndMVMReports.setOwningInstitutionId("1");
        matchingSerialAndMVMReports.setTitle("MatchingSerialAndMVMReports");
        matchingSerialAndMVMReports.setSummaryHoldings("Test");
        matchingSerialAndMVMReports.setVolumePartYear("test");
        matchingSerialAndMVMReports.setUseRestriction("No Restriction");
        matchingSerialAndMVMReports.setBibId("15255");
        matchingSerialAndMVMReports.setOwningInstitutionBibId("45454564");
        matchingSerialAndMVMReports.setBarcode("33145454558247856");
        assertNotNull(matchingSerialAndMVMReports.getOwningInstitutionId());
        assertNotNull(matchingSerialAndMVMReports.getTitle());
        assertNotNull(matchingSerialAndMVMReports.getSummaryHoldings());
        assertNotNull(matchingSerialAndMVMReports.getVolumePartYear());
        assertNotNull(matchingSerialAndMVMReports.getUseRestriction());
        assertNotNull(matchingSerialAndMVMReports.getBibId());
        assertNotNull(matchingSerialAndMVMReports.getOwningInstitutionBibId());
        assertNotNull(matchingSerialAndMVMReports.getBarcode());
    }

}