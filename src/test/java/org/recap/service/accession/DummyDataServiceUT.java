package org.recap.service.accession;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 28/4/17.
 */
public class DummyDataServiceUT extends BaseTestCase{

    @Autowired
    private DummyDataService dummyDataService;

    @Test
    public void createDummyDataAsIncomplete(){
        BibliographicEntity bibliographicEntity = dummyDataService.createDummyDataAsIncomplete(1,"3245678232","PA");
        assertNotNull(bibliographicEntity);
        assertEquals(RecapConstants.INCOMPLETE_STATUS,bibliographicEntity.getCatalogingStatus());
        assertEquals(RecapConstants.DUMMY_CALL_NUMBER_TYPE,bibliographicEntity.getItemEntities().get(0).getCallNumberType());
        assertEquals(RecapConstants.DUMMYCALLNUMBER,bibliographicEntity.getItemEntities().get(0).getCallNumber());
        assertEquals(RecapConstants.INCOMPLETE_STATUS,bibliographicEntity.getItemEntities().get(0).getCatalogingStatus());
    }
}
