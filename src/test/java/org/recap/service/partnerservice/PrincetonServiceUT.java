package org.recap.service.partnerservice;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 18/12/16.
 */
public class PrincetonServiceUT extends BaseTestCase{

    @Autowired
    private PrincetonService princetonService;

    @Autowired
    private MarcUtil marcUtil;

    @Test
    public void getBibData() {
        String bibDataResponse = princetonService.getBibData("32101062128309", "PB");
        assertNotNull(bibDataResponse);
        List<Record> records = marcUtil.readMarcXml(bibDataResponse);
        assertNotNull(records);
    }
}
