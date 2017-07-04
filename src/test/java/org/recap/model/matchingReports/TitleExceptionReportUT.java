package org.recap.model.matchingReports;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 4/7/17.
 */
public class TitleExceptionReportUT extends BaseTestCase{

    @Test
    public void testTitleExceptionReport(){
        TitleExceptionReport titleExceptionReport = new TitleExceptionReport();
        titleExceptionReport.setOwningInstitution("PUL");
        titleExceptionReport.setBibId("1235");
        titleExceptionReport.setOwningInstitutionBibId("AD4526523563");
        titleExceptionReport.setMaterialType("Monograph");
        titleExceptionReport.setOCLC("1225");
        titleExceptionReport.setISBN("4565");
        titleExceptionReport.setISSN("1236");
        titleExceptionReport.setLCCN("7412");
        titleExceptionReport.setTitleList(Arrays.asList("Test"));

        assertNotNull(titleExceptionReport.getOwningInstitution());
        assertNotNull(titleExceptionReport.getBibId());
        assertNotNull(titleExceptionReport.getOwningInstitutionBibId());
        assertNotNull(titleExceptionReport.getMaterialType());
        assertNotNull(titleExceptionReport.getOCLC());
        assertNotNull(titleExceptionReport.getISBN());
        assertNotNull(titleExceptionReport.getISSN());
        assertNotNull(titleExceptionReport.getLCCN());
        assertNotNull(titleExceptionReport.getTitleList());
    }

}