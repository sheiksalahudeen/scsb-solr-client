package org.recap.model.solr;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.service.MatchingAlgorithmHelperService;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 2/8/16.
 */
public class MatchingRecordReportUT extends BaseTestCase{

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Test
    public void testMatchingRecordReport(){
        String fieldValue = "978-3-16-148410-0";
        String fieldName = "ISBN";
        Bib bib = new Bib();
        bib.setBibId(1);
        bib.setOwningInstitutionBibId("1");
        bib.setTitle("SampleTitle");
        bib.setOwningInstitution("PUL");

        Item item = new Item();
        item.setItemId(1);
        item.setBarcode("BA342");
        item.setUseRestriction("Allowed");
        item.setSummaryHoldings("Summary Holding");
        MatchingRecordReport matchingRecordReport = matchingAlgorithmHelperService.populateMatchingRecordReport(fieldValue,bib,item,fieldName);
        assertNotNull(matchingRecordReport);
        assertEquals("SampleTitle",matchingRecordReport.getTitle());
        assertEquals("1",matchingRecordReport.getBibId());
        assertEquals("BA342",matchingRecordReport.getBarcode());
        assertEquals("Summary Holding",matchingRecordReport.getSummaryHoldings());
        assertEquals("Allowed",matchingRecordReport.getUseRestrictions());
        assertEquals(fieldValue,matchingRecordReport.getMatchPointContent());
        assertEquals("020",matchingRecordReport.getMatchPointTag());

    }


}
