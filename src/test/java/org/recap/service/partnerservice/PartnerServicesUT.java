package org.recap.service.partnerservice;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by peris on 12/23/16.
 */
public class PartnerServicesUT extends BaseTestCase {

    @Autowired
    private PrincetonService princetonService;

    @Autowired
    private NYPLService nyplService;

    @Autowired
    private MarcUtil marcUtil;

    @Test
    public void getBibData() throws JAXBException {
        String bibDataResponse = princetonService.getBibData("32101062128309");
        assertNotNull(bibDataResponse);
        List<Record> records = marcUtil.readMarcXml(bibDataResponse);
        assertNotNull(records);

        String bibDataResponseFoNYPL = nyplService.getBibData("33433002031718", "NA");
        assertNotNull(bibDataResponseFoNYPL);
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(bibDataResponseFoNYPL, BibRecords.class);
        assertNotNull(bibRecords);
    }
}
