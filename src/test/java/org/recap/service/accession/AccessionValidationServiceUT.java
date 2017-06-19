package org.recap.service.accession;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.BibRecord;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 3/6/17.
 */
public class AccessionValidationServiceUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(AccessionValidationServiceUT.class);
    @Autowired
    private AccessionValidationService accessionValidationService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private MarcToBibEntityConverter marcToBibEntityConverter;
    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Test
    public void validateBoundWithValidMarcRecordFromIls() throws URISyntaxException, IOException {
        File bibContentFile = getXmlContent("ValidBoundWithMarc.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithMarcRecordFromIls(records);
        assertEquals(true,isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithInvalidMarcRecordFromIls() throws URISyntaxException, IOException {
        File bibContentFile = getXmlContent("InvalidBoundWithMarc.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithMarcRecordFromIls(records);
        assertEquals(false,isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithValidScsbRecordFromIls() throws URISyntaxException, IOException, JAXBException {
        File bibContentFile = getXmlContent("ValidBoundWithSCSB.xml");
        String scsbXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<BibRecord> bibRecordList = getBibRecordList(scsbXmlString);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithScsbRecordFromIls(bibRecordList);
        assertEquals(true,isValidBoundWithRecord);
    }

    @Test
    public void validateBoundWithInvalidScsbRecordFromIls() throws URISyntaxException, IOException, JAXBException {
        File bibContentFile = getXmlContent("InvalidBoundWithSCSB.xml");
        String scsbXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<BibRecord> bibRecordList = getBibRecordList(scsbXmlString);
        boolean isValidBoundWithRecord = accessionValidationService.validateBoundWithScsbRecordFromIls(bibRecordList);
        assertEquals(false,isValidBoundWithRecord);
    }

    @Test
    public void validateValidItemRecord() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","9919400","74534419");
        File bibContentFile = getXmlContent("MarcRecord.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        Map map = marcToBibEntityConverter.convert(records.get(0), "PUL",accessionRequest);
        assertNotNull(map);
        BibliographicEntity convertedBibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = accessionValidationService.validateItem(convertedBibliographicEntity,false,false,errorMessage);
        assertEquals(true,isValid);
    }

    @Test
    public void validateInvalidItemRecord() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","9919400","7453441");
        File bibContentFile = getXmlContent("MarcRecord.xml");
        String marcXmlString = FileUtils.readFileToString(bibContentFile, "UTF-8");
        List<Record> records = readMarcXml(marcXmlString);
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PA");
        accessionRequest.setItemBarcode("32101095533293");
        Map map = marcToBibEntityConverter.convert(records.get(0), "PUL",accessionRequest);
        assertNotNull(map);
        BibliographicEntity convertedBibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = accessionValidationService.validateItem(convertedBibliographicEntity,false,false,errorMessage);
        assertEquals(false,isValid);
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

    private File getXmlContent(String fileName) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource(fileName);
        return new File(resource.toURI());
    }

    private List<BibRecord> getBibRecordList(String scsbXmlString) throws JAXBException {
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(scsbXmlString, BibRecords.class);
        return bibRecords.getBibRecordList();
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem(String itemBarcode, String customerCode, String callnumber, String institution,String owningInstBibId, String owningInstItemId) throws Exception {
        File bibContentFile = getBibContentFile(institution);
        File holdingsContentFile = getHoldingsContentFile(institution);
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstBibId);
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = getItemEntity(itemBarcode,customerCode,callnumber,owningInstItemId);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;

    }

    public ItemEntity getItemEntity(String itemBarcode,String customerCode,String callnumber,String owningInstItemId){
        Random random = new Random();
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(owningInstItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber(callnumber);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode(customerCode);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        return itemEntity;
    }

    private File getBibContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile(String institution) throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
