package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by rajeshbabuk on 5/1/17.
 */
public class UpdateCgdUtilUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    ItemChangeLogDetailsRepository mockedItemChangeLogDetailsRepository;

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    @Mock
    UpdateCgdUtil mockedUpdateCgdUtil;

    @Mock
    SolrTemplate mockedSolrTemplate;

    @Mock
    ItemCrudRepository MockedItemCrudRepository;

    @Test
    public void updateCGDForItemInDB() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        String itemBarcode = savedBibliographicEntity.getItemEntities().get(0).getBarcode();
        updateCgdUtil.updateCGDForItemInDB(itemBarcode, "Private", "guest", new Date());

        List<ItemEntity> fetchedItemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemEntities);
        assertTrue(fetchedItemEntities.size() > 0);
        for (ItemEntity fetchedItemEntity : fetchedItemEntities) {
            entityManager.refresh(fetchedItemEntity);
            assertNotNull(fetchedItemEntity.getItemId());
            assertEquals(itemBarcode, fetchedItemEntity.getBarcode());
            assertEquals("Private", fetchedItemEntity.getCollectionGroupEntity().getCollectionGroupCode());
        }
    }

    @Test
    public void updateCGDForItemInSolr() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0));
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getItemId());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(savedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        mockedSolrTemplate.saveDocument(solrInputDocument);
        mockedSolrTemplate.commit();

        String itemBarcode = savedBibliographicEntity.getItemEntities().get(0).getBarcode();
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setBarcode(itemBarcode);
        item.setCollectionGroupDesignation("Shared");
        itemList.add(item);
        Mockito.when(MockedItemCrudRepository.findByBarcode(itemBarcode)).thenReturn(itemList);
        List<Item> fetchedItemsSolr = MockedItemCrudRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemsSolr);
        assertTrue(fetchedItemsSolr.size() > 0);
        for (Item fetchedItemSolr : fetchedItemsSolr) {
            assertNotNull(fetchedItemSolr.getItemId());
            assertEquals(itemBarcode, fetchedItemSolr.getBarcode());
            assertEquals("Shared", fetchedItemSolr.getCollectionGroupDesignation());
        }

        updateCgdUtil.updateCGDForItemInDB(itemBarcode, "Open", "guest", new Date());
        List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
        mockedUpdateCgdUtil.updateCGDForItemInSolr(itemEntities);
        mockedSolrTemplate.commit();
        Mockito.when(MockedItemCrudRepository.findByBarcode(itemBarcode)).thenReturn(itemList);
        List<Item> fetchedItemsSolrAfterUpdate = MockedItemCrudRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemsSolrAfterUpdate);
        assertTrue(fetchedItemsSolrAfterUpdate.size() > 0);
        for (Item fetchedItemSolrAfterUpdate : fetchedItemsSolrAfterUpdate) {
            assertNotNull(fetchedItemSolrAfterUpdate.getItemId());
            assertEquals(itemBarcode, fetchedItemSolrAfterUpdate.getBarcode());
            assertEquals("Shared", fetchedItemSolrAfterUpdate.getCollectionGroupDesignation());
        }

    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }

    @Test
    public void updateCGDForItem() throws Exception {
        Mockito.when(mockedItemChangeLogDetailsRepository.count()).thenReturn(new Long(0));
        long beforeCountForChangeLog = mockedItemChangeLogDetailsRepository.count();

        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getBibliographicId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0));
        assertNotNull(savedBibliographicEntity.getItemEntities().get(0).getItemId());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(savedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        mockedSolrTemplate.saveDocument(solrInputDocument);
        mockedSolrTemplate.commit();

        String itemBarcode = savedBibliographicEntity.getItemEntities().get(0).getBarcode();
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setBarcode(itemBarcode);
        item.setCollectionGroupDesignation("Shared");
        itemList.add(item);
        Mockito.when(MockedItemCrudRepository.findByBarcode(itemBarcode)).thenReturn(itemList);
        List<Item> fetchedItemsSolr = MockedItemCrudRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemsSolr);
        assertTrue(fetchedItemsSolr.size() > 0);
        for (Item fetchedItemSolr : fetchedItemsSolr) {
            assertNotNull(fetchedItemSolr.getItemId());
            assertEquals(itemBarcode, fetchedItemSolr.getBarcode());
            assertEquals("Shared", fetchedItemSolr.getCollectionGroupDesignation());
        }

        updateCgdUtil.updateCGDForItem(itemBarcode, "PUL", "Shared", "Private", "Notes for updating CGD");
        List<Item> itemList1 = new ArrayList<>();
        Item item1 = new Item();
        item.setItemId(1);
        item.setBarcode(itemBarcode);
        item.setCollectionGroupDesignation("Private");
        itemList.add(item1);
        Mockito.when(MockedItemCrudRepository.findByBarcode(itemBarcode)).thenReturn(itemList1);
        List<ItemEntity> fetchedItemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
        assertNotNull(fetchedItemEntities);
        assertTrue(fetchedItemEntities.size() > 0);
        for (ItemEntity fetchedItemEntity : fetchedItemEntities) {
            entityManager.refresh(fetchedItemEntity);
            assertNotNull(fetchedItemEntity.getItemId());
            assertEquals(itemBarcode, fetchedItemEntity.getBarcode());
            assertEquals("Private", fetchedItemEntity.getCollectionGroupEntity().getCollectionGroupCode());
        }
        Mockito.when(mockedItemChangeLogDetailsRepository.count()).thenReturn(new Long(1));
        long afterCountForChangeLog = mockedItemChangeLogDetailsRepository.count();

        assertEquals(afterCountForChangeLog, beforeCountForChangeLog + 1);

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
        itemEntity.setBarcode(String.valueOf(random.nextInt()));
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        //itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    public File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    public File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
