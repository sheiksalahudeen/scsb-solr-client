package org.recap.model.accession;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 29/6/17.
 */
public class BatchAccessionResponseUT extends BaseTestCase {


    @Test
    public void testBatchAccessionResponse(){
        BatchAccessionResponse batchAccessionResponse = new BatchAccessionResponse();
        batchAccessionResponse.setRequestedRecords(1);
        batchAccessionResponse.setSuccessRecords(1);
        batchAccessionResponse.setDummyRecords(1);
        batchAccessionResponse.setDuplicateRecords(1);
        batchAccessionResponse.setEmptyBarcodes(0);
        batchAccessionResponse.setEmptyOwningInst(0);
        batchAccessionResponse.setAlreadyAccessioned(1);
        batchAccessionResponse.setException(0);
        batchAccessionResponse.setFailure(0);
        batchAccessionResponse.setInvalidLenghBarcode(0);
        batchAccessionResponse.setTimeElapsed(new Date().toString());
        batchAccessionResponse.addDummyRecords(1);
        batchAccessionResponse.addEmptyBarcodes(1);
        batchAccessionResponse.addEmptyOwningInst(3);
        batchAccessionResponse.addAlreadyAccessioned(0);
        batchAccessionResponse.addException(0);
        batchAccessionResponse.addInvalidLenghBarcode(0);
        batchAccessionResponse.addSuccessRecord(1);
        batchAccessionResponse.addFailure(0);
        assertNotNull(batchAccessionResponse.getRequestedRecords());
        assertNotNull(batchAccessionResponse.getSuccessRecords());
        assertNotNull(batchAccessionResponse.getDummyRecords());
        assertNotNull(batchAccessionResponse.getDuplicateRecords());
        assertNotNull(batchAccessionResponse.getFailure());
        assertNotNull(batchAccessionResponse.getEmptyBarcodes());
        assertNotNull(batchAccessionResponse.getEmptyOwningInst());
        assertNotNull(batchAccessionResponse.getAlreadyAccessioned());
        assertNotNull(batchAccessionResponse.getException());
        assertNotNull(batchAccessionResponse.getInvalidLenghBarcode());
        assertNotNull(batchAccessionResponse.getTimeElapsed());
    }
}