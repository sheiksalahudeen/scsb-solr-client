package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 5/1/17.
 */
public class UpdateCgdUtilUT extends BaseTestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    @Test
    public void updateCGDForItemInDB() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        Integer itemId = savedBibliographicEntity.getItemEntities().get(0).getItemId();
        updateCgdUtil.updateCGDForItemInDB(itemId, "Private", "guest", new Date());

        ItemEntity fetchedItemEntity = itemDetailsRepository.findByItemId(itemId);
        entityManager.refresh(fetchedItemEntity);
        assertNotNull(fetchedItemEntity);
        assertNotNull(fetchedItemEntity.getItemId());
        assertEquals(itemId, fetchedItemEntity.getItemId());
        assertEquals("Private", fetchedItemEntity.getCollectionGroupEntity().getCollectionGroupCode());
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
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();

        Integer itemId = savedBibliographicEntity.getItemEntities().get(0).getItemId();
        Item fetchedItemSolr = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolr);
        assertNotNull(fetchedItemSolr.getItemId());
        assertEquals(itemId, fetchedItemSolr.getItemId());
        assertEquals("Shared", fetchedItemSolr.getCollectionGroupDesignation());

        updateCgdUtil.updateCGDForItemInSolr(itemId, "Open");

        Item fetchedItemSolrAfterUpdate = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolrAfterUpdate);
        assertNotNull(fetchedItemSolrAfterUpdate.getItemId());
        assertEquals(itemId, fetchedItemSolrAfterUpdate.getItemId());
        assertEquals("Open", fetchedItemSolrAfterUpdate.getCollectionGroupDesignation());
    }

    @Test
    public void updateCGDForItem() throws Exception {
        long beforeCountForChangeLog = itemChangeLogDetailsRepository.count();

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
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();

        Integer itemId = savedBibliographicEntity.getItemEntities().get(0).getItemId();
        Item fetchedItemSolr = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolr);
        assertNotNull(fetchedItemSolr.getItemId());
        assertEquals(itemId, fetchedItemSolr.getItemId());
        assertEquals("Shared", fetchedItemSolr.getCollectionGroupDesignation());

        updateCgdUtil.updateCGDForItem(itemId, "Private", "Notes for updating CGD");

        ItemEntity fetchedItemEntity = itemDetailsRepository.findByItemId(itemId);
        entityManager.refresh(fetchedItemEntity);
        assertNotNull(fetchedItemEntity);
        assertNotNull(fetchedItemEntity.getItemId());
        assertEquals(itemId, fetchedItemEntity.getItemId());
        assertEquals("Private", fetchedItemEntity.getCollectionGroupEntity().getCollectionGroupCode());

        Item fetchedItemSolrAfterUpdate = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolrAfterUpdate);
        assertNotNull(fetchedItemSolrAfterUpdate.getItemId());
        assertEquals(itemId, fetchedItemSolrAfterUpdate.getItemId());
        assertEquals("Private", fetchedItemSolrAfterUpdate.getCollectionGroupDesignation());

        long afterCountForChangeLog = itemChangeLogDetailsRepository.count();

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
