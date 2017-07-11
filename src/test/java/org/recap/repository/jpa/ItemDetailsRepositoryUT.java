package org.recap.repository.jpa;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 13/7/16.
 */
public class ItemDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(bibliographicDetailsRepository);
        assertNotNull(itemDetailsRepository);
        assertNotNull(entityManager);

        Random random = new Random();
        int owningInstitutionId = 2;

        Long count = itemDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        Date date = new Date();
        bibliographicEntity.setCreatedDate(date);
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(date);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(date);
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(date);
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCreatedDate(date);
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(date);
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("123178");
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(owningInstitutionId);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedEntity);

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getBibliographicId());
        assertNotNull(savedEntity.getHoldingsEntities().get(0).getHoldingsId());
        ItemEntity savedItemEntity = savedEntity.getItemEntities().get(0);
        assertNotNull(savedItemEntity);
        assertNotNull(savedItemEntity.getItemId());

        Long countAfterAdd = itemDetailsRepository.countByOwningInstitutionIdAndIsDeletedFalse(owningInstitutionId);
        assertTrue(countAfterAdd > count);

        List<ItemEntity> byOwningInstitutionId = itemDetailsRepository.findByOwningInstitutionId(owningInstitutionId);
        assertNotNull(byOwningInstitutionId);
        assertTrue(byOwningInstitutionId.size() > 0);

        ItemEntity byOwningInstitutionItemId = itemDetailsRepository.findByOwningInstitutionItemId(owningInstitutionItemId);
        assertNotNull(byOwningInstitutionItemId);

        Page<ItemEntity> pageByOwningInstitutionId = itemDetailsRepository.findByOwningInstitutionIdAndIsDeletedFalse(new PageRequest(0, 10), owningInstitutionId);
        assertNotNull(pageByOwningInstitutionId);
        assertTrue(countAfterAdd == pageByOwningInstitutionId.getTotalElements());

        assertEquals(savedItemEntity.getCallNumberType(), "0");
        assertEquals(savedItemEntity.getCallNumber(), "callNum");
        assertEquals(savedItemEntity.getCreatedBy(), "etl");
        assertEquals(savedItemEntity.getLastUpdatedBy(), "etl");
        assertEquals(savedItemEntity.getBarcode(), "123178");
        assertEquals(savedItemEntity.getOwningInstitutionItemId(), owningInstitutionItemId);
        assertEquals(savedItemEntity.getCustomerCode(), "PA");
        assertNotNull(savedItemEntity.getHoldingsEntities());
        assertTrue(savedItemEntity.getOwningInstitutionId() == owningInstitutionId);
        assertTrue(savedItemEntity.getCollectionGroupId() == 1);
    }

    @Test
    public void updateCollectionGroupIdByItemId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        String itemBarcode = savedBibliographicEntity.getItemEntities().get(0).getBarcode();
        int updatedItem = itemDetailsRepository.updateCollectionGroupIdByItemBarcode(2, itemBarcode, "guest", new Date());
        assertTrue(updatedItem > 0);

        List<ItemEntity> fetchedItemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemEntities);
        assertTrue(fetchedItemEntities.size() > 0);
        for (ItemEntity itemEntity : fetchedItemEntities) {
            entityManager.refresh(itemEntity);
            assertNotNull(itemEntity);
            assertNotNull(itemEntity.getBarcode());
            assertEquals(itemBarcode, itemEntity.getBarcode());
            assertEquals("Open", itemEntity.getCollectionGroupEntity().getCollectionGroupCode());
        }
    }

    public BibliographicEntity getBibEntityWithHoldingsAndItem() throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("12398");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    @Test
    public void findByBarcodeAndCustomerCode(){
        saveSingleBibHoldingsItem();
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcodeAndCustomerCode("12316433","PA");
        assertNotNull(itemEntityList);
        assertEquals("12316433",itemEntityList.get(0).getBarcode());
        assertEquals("PA",itemEntityList.get(0).getCustomerCode());
    }


    public File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    public File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }

    @Test
    public void getItemStatusByBarcodeAndIsDeletedFalse() throws Exception {
        saveSingleBibHoldingsItem();
        String itemStatus = itemDetailsRepository.getItemStatusByBarcodeAndIsDeletedFalse("12316433");
        assertEquals(itemStatus, "Available");
    }

    private BibliographicEntity saveSingleBibHoldingsItem() {
        Random random = new Random();
        Date today = new Date();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(today);
        bibliographicEntity.setCreatedBy("etl");
        bibliographicEntity.setLastUpdatedBy("etl");
        bibliographicEntity.setLastUpdatedDate(today);
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(today);
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(today);
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setCallNumberType("0");
        itemEntity.setCallNumber("callNum");
        itemEntity.setCopyNumber(1);
        itemEntity.setCreatedDate(today);
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(today);
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode("12316433");
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCustomerCode("PA");
        itemEntity.setItemAvailabilityStatusId(1);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    @Test
    public void findByLastUpdatedDateTest() throws Exception {
        BibliographicEntity bibliographicEntity = saveSingleBibHoldingsItem();
        Calendar cal = Calendar.getInstance();
        cal.setTime(bibliographicEntity.getItemEntities().get(0).getLastUpdatedDate());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Page<ItemEntity> byLastUpdatedDate = itemDetailsRepository.findByLastUpdatedDate(new PageRequest(0, 10), cal.getTime(), new Date());
        assertNotNull(byLastUpdatedDate.getContent());
    }

    @Test
    public void findByOwningInstitutionItemIdAndOwningInstitutionId() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem("32456723441256","PA","24252","PUL","9919400","7453441");
        ItemEntity itemEntity = itemDetailsRepository.findByOwningInstitutionItemIdAndOwningInstitutionId("7453441",1);
        assertNotEquals(null,itemEntity);
        assertEquals("7453441",itemEntity.getOwningInstitutionItemId());
        assertEquals(new Integer(1),itemEntity.getOwningInstitutionId());

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