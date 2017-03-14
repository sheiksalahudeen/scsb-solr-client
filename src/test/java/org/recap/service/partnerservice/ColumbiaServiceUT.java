package org.recap.service.partnerservice;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 14/3/17.
 */
public class ColumbiaServiceUT extends BaseTestCase{

    @Autowired
    private ColumbiaService columbiaService;

    @Autowired
    private MarcUtil marcUtil;

    @Test
    public void getBibData() {
        String bibDataResponse = columbiaService.getBibData("CU54175534");
        assertNotNull(bibDataResponse);
        List<Record> records = marcUtil.readMarcXml(bibDataResponse);
        assertNotNull(records);
    }
}
