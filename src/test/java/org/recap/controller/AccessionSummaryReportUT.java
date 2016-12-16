package org.recap.controller;

import org.junit.Test;

import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.service.accession.AccessionService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 23/11/16.
 */
public class AccessionSummaryReportUT extends BaseTestCase{

    @Autowired
    AccessionService accessionService;

    @Test
    public void testFailureRecords() throws Exception{
        String owningInstitution = "PUL";
        String barcode = "32101058378587";
        String response = accessionService.processRequest(barcode,owningInstitution);
        assertNotNull(response);
        assertEquals(response, RecapConstants.SUCCESS);
    }


}
