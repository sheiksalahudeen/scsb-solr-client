package org.recap.model.search;

import org.junit.Test;
import org.recap.BaseTestCase;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 3/7/17.
 */
public class DeaccessionItemResultsRowUT extends BaseTestCase {

    @Test
    public void testDeaccessionItemResultsRow(){
        DeaccessionItemResultsRow deaccessionItemResultsRow = new DeaccessionItemResultsRow();
        deaccessionItemResultsRow.setItemId(1);
        deaccessionItemResultsRow.setDeaccessionDate(new Date().toString());
        deaccessionItemResultsRow.setTitle("test");
        deaccessionItemResultsRow.setDeaccessionOwnInst("PUL");
        deaccessionItemResultsRow.setCgd("Open");
        deaccessionItemResultsRow.setDeaccessionCreatedBy("test");
        deaccessionItemResultsRow.setDeaccessionNotes("test");
        deaccessionItemResultsRow.setItemBarcode("33456745455785848");

        assertNotNull(deaccessionItemResultsRow.getItemId());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionDate());
        assertNotNull(deaccessionItemResultsRow.getTitle());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionOwnInst());
        assertNotNull(deaccessionItemResultsRow.getItemBarcode());
        assertNotNull(deaccessionItemResultsRow.getCgd());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionNotes());
        assertNotNull(deaccessionItemResultsRow.getDeaccessionCreatedBy());
    }

}