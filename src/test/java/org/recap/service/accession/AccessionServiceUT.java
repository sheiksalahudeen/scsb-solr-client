package org.recap.service.accession;

import junit.framework.TestCase;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.service.authorization.NyplOauthTokenApiService;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
public class AccessionServiceUT extends BaseTestCase {

    private Logger logger = LoggerFactory.getLogger(AccessionServiceUT.class);

    @Autowired
    private AccessionService accessionService;

    @Value("${ils.nypl.bibdata}")
    String ilsNYPLBibData;

    @Autowired
    NyplOauthTokenApiService nyplOauthTokenApiService;

    @Test
    public void processForPUL(){
        accessionService.processRequest("32101062128309", null, "PUL");
        List<BibliographicEntity> fetchedBibliographicEntityList = bibliographicDetailsRepository.findByOwningInstitutionBibId("202304");
        String updatedBibMarcXML = new String(fetchedBibliographicEntityList.get(0).getContent(), StandardCharsets.UTF_8);
        List<Record> bibRecordList = readMarcXml(updatedBibMarcXML);
        assertNotNull(bibRecordList);
        DataField field912 = (DataField)bibRecordList.get(0).getVariableField("912");
        assertEquals("19970731060735.0", field912.getSubfield('a').getData());
        HoldingsEntity holdingsEntity = fetchedBibliographicEntityList.get(0).getHoldingsEntities().get(0);
        String updatedHoldingMarcXML = new String(holdingsEntity.getContent(),StandardCharsets.UTF_8);
        List<Record> holdingRecordList = readMarcXml(updatedHoldingMarcXML);
        logger.info("updatedHoldingMarcXML-->"+updatedHoldingMarcXML);
        TestCase.assertNotNull(holdingRecordList);
        DataField field852 = (DataField)holdingRecordList.get(0).getVariableField("852");
        assertEquals("K25 .xN5", field852.getSubfield('h').getData());
    }

    @Test
    public void processForNYPL(){
        accessionService.processRequest("33433002031718", "NA","NYPL");
        List<BibliographicEntity> fetchedBibliographicEntityList = bibliographicDetailsRepository.findByOwningInstitutionBibId(".b100000186");
        String updatedBibMarcXML = new String(fetchedBibliographicEntityList.get(0).getContent(), StandardCharsets.UTF_8);
        List<Record> bibRecordList = readMarcXml(updatedBibMarcXML);
        assertNotNull(bibRecordList);
        DataField field912 = (DataField)bibRecordList.get(0).getVariableField("650");
        assertEquals("Women", field912.getSubfield('a').getData());
        HoldingsEntity holdingsEntity = fetchedBibliographicEntityList.get(0).getHoldingsEntities().get(0);
        String updatedHoldingMarcXML = new String(holdingsEntity.getContent(),StandardCharsets.UTF_8);
        List<Record> holdingRecordList = readMarcXml(updatedHoldingMarcXML);
        logger.info("updatedHoldingMarcXML-->"+updatedHoldingMarcXML);
        TestCase.assertNotNull(holdingRecordList);
        DataField field852 = (DataField)holdingRecordList.get(0).getVariableField("852");
        assertEquals("*OFX 84-1995", field852.getSubfield('h').getData());
    }

    private List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
            logger.info(record.toString());
        }
        return recordList;
    }

    @Test
    public void getOwningInstitution() throws Exception {
        String customerCode = "PB";
        String owningInstitution = accessionService.getOwningInstitution(customerCode);
        assertNotNull(owningInstitution);
        assertTrue(owningInstitution.equalsIgnoreCase(RecapConstants.PRINCETON));
    }


}