package org.recap.service.partnerservice;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 18/12/16.
 */
public class NYPLServiceUT extends BaseTestCase {

    @Autowired
    private NYPLService nyplService;

    @Test
    public void getBibData() throws Exception{
        String bibDataResponse = nyplService.getBibData("33433002031718", "NA");
        assertNotNull(bibDataResponse);
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(bibDataResponse, BibRecords.class);
        assertNotNull(bibRecords);
    }
}
