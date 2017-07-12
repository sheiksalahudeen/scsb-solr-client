package org.recap.model.accession;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 29/6/17.
 */
public class AccessionSummaryUT extends BaseTestCase {


    @Test
    public void testAccessionSummary(){
        AccessionSummary accessionSummary = new AccessionSummary("Test");
        accessionSummary.setRequestedRecords(1);
        accessionSummary.setSuccessRecords(1);
        accessionSummary.setDummyRecords(1);
        accessionSummary.setDuplicateRecords(1);
        accessionSummary.setEmptyBarcodes(0);
        accessionSummary.setEmptyOwningInst(0);
        accessionSummary.setAlreadyAccessioned(1);
        accessionSummary.setException(0);
        accessionSummary.setFailure(0);
        accessionSummary.setInvalidLenghBarcode(0);
        accessionSummary.setTimeElapsed(new Date().toString());
        accessionSummary.setEmptyCustomerCode(1);
        accessionSummary.setCustomerCodeDoesNotExist(2);
        accessionSummary.addDummyRecords(1);
        accessionSummary.addEmptyBarcodes(1);
        accessionSummary.addEmptyOwningInst(3);
        accessionSummary.addAlreadyAccessioned(0);
        accessionSummary.addException(0);
        accessionSummary.addInvalidLenghBarcode(0);
        accessionSummary.addSuccessRecord(1);
        accessionSummary.addFailure(0);
        assertNotNull(accessionSummary.getRequestedRecords());
        assertNotNull(accessionSummary.getSuccessRecords());
        assertNotNull(accessionSummary.getDummyRecords());
        assertNotNull(accessionSummary.getDuplicateRecords());
        assertNotNull(accessionSummary.getFailure());
        assertNotNull(accessionSummary.getEmptyBarcodes());
        assertNotNull(accessionSummary.getEmptyOwningInst());
        assertNotNull(accessionSummary.getAlreadyAccessioned());
        assertNotNull(accessionSummary.getException());
        assertNotNull(accessionSummary.getInvalidLenghBarcode());
        assertNotNull(accessionSummary.getTimeElapsed());
        assertNotNull(accessionSummary.getCustomerCodeDoesNotExist());
        assertNotNull(accessionSummary.getEmptyCustomerCode());
        assertNotNull(accessionSummary.getType());
    }
}