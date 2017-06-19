package org.recap.converter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
public class MarcToBibEntityConverterUT extends BaseTestCase {

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Test
    public void convert() throws Exception {
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        Map map = marcToBibEntityConverter.convert(records.get(0), "PUL",accessionRequest);
        assertNotNull(map);
        BibliographicEntity bibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        assertNotNull(bibliographicEntity);
        List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
        assertNotNull(holdingsEntities);
        assertTrue(holdingsEntities.size() == 1);
        List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
        assertNotNull(itemEntities);
        assertTrue(itemEntities.size() == 1);
    }

    private List<Record> getRecords() throws Exception {
        URL resource = getClass().getResource("sampleRecord.xml");
        File file = new File(resource.toURI());
        String marcXmlString = FileUtils.readFileToString(file, "UTF-8");
        MarcUtil marcUtil = new MarcUtil();
        return marcUtil.readMarcXml(marcXmlString);
    }

}