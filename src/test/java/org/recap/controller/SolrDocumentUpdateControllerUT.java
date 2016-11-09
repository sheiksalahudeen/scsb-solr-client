package org.recap.controller;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import static org.junit.Assert.*;

/**
 * Created by hemalathas on 8/11/16.
 */
public class SolrDocumentUpdateControllerUT extends BaseTestCase{

    @Autowired
    SolrDocumentUpdateController solrDocumentUpdateController;

    @Test
    public void testUpdateIsDeletedBibByBibId(){
        String updateIsDeletedBibByBibId = solrDocumentUpdateController.updateIsDeletedBibByBibId(Arrays.asList(578665, 578673));
        assertNotNull(updateIsDeletedBibByBibId);
        assertEquals(updateIsDeletedBibByBibId,"Bib documents updated successfully.");
    }

    @Test
    public void testUpdateIsDeletedHoldingsByHoldingsId() throws Exception {
        String response = solrDocumentUpdateController.updateIsDeletedHoldingsByHoldingsId(Arrays.asList(596095));
        assertNotNull(response);
        assertEquals(response, "Holdings documents updated successfully.");
    }

    @Test
    public void testUpdateIsDeletedItemByItemIds(){
        String response = solrDocumentUpdateController.updateIsDeletedItemByItemIds(Arrays.asList(825172));
        assertNotNull(response);
        assertNotNull(response,"Item documents updated successfully.");

    }

}