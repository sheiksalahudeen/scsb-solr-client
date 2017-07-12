package org.recap.model.request;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 10/7/17.
 */
public class ItemCheckInRequestUT extends BaseTestCase{

    @Test
    public void testItemCheckInRequest(){
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setItemOwningInstitution("PUL");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("3354794575685675"));
        itemCheckInRequest.setPatronIdentifier("00000000");
        assertNotNull(itemCheckInRequest.getItemOwningInstitution());
        assertNotNull(itemCheckInRequest.getItemBarcodes());
        assertNotNull(itemCheckInRequest.getPatronIdentifier());
    }

}