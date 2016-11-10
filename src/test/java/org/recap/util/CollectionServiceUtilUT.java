package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.service.accession.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by rajeshbabuk on 19/10/16.
 */
public class CollectionServiceUtilUT extends BaseTestCase {

    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ItemCrudRepository itemSolrCrudRepository;

    @Autowired
    CollectionServiceUtil collectionServiceUtil;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    SolrIndexService solrIndexService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void updateCGDForItemInDB() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        assertEquals("Shared", savedBibliographicEntity.getItemEntities().get(0).getCollectionGroupEntity().getCollectionGroupCode());

        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        Integer itemId = savedBibliographicEntity.getItemEntities().get(0).getItemId();
        bibliographicMarcForm.setItemId(itemId);
        bibliographicMarcForm.setNewCollectionGroupDesignation("Private");
        bibliographicMarcForm.setCgdChangeNotes("Notes for updating CGD");

        collectionServiceUtil.updateCGDForItemInDB(bibliographicMarcForm, "guest", new Date());

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

        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setBibId(savedBibliographicEntity.getBibliographicId());
        bibliographicMarcForm.setItemId(itemId);
        bibliographicMarcForm.setNewCollectionGroupDesignation("Open");

        collectionServiceUtil.updateCGDForItemInSolr(bibliographicMarcForm);

        Item fetchedItemSolrAfterUpdate = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolrAfterUpdate);
        assertNotNull(fetchedItemSolrAfterUpdate.getItemId());
        assertEquals(itemId, fetchedItemSolrAfterUpdate.getItemId());
        assertEquals("Open", fetchedItemSolrAfterUpdate.getCollectionGroupDesignation());

        /*BibliographicEntity bibEntity = bibliographicDetailsRepository.findByBibliographicId(bibliographicMarcForm.getBibId());
        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        for (SolrInputDocument holdingsSolrInputDocument : bibSolrInputDocument.getChildDocuments()) {
            for (SolrInputDocument itemSolrInputDocument : holdingsSolrInputDocument.getChildDocuments()) {
                if (bibliographicMarcForm.getItemId().equals(itemSolrInputDocument.get("ItemId").getValue())) {
                    itemSolrInputDocument.setField("CollectionGroupDesignation", bibliographicMarcForm.getCollectionGroupDesignation());;
                }
            }
        }
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();*/

        /*SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("ItemId:" + bibliographicMarcForm.getItemId());
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        List<Item> beans = queryResponse.getBeans(Item.class);
        Item item = beans.get(0);
        item.setCollectionGroupDesignation(bibliographicMarcForm.getCollectionGroupDesignation());
        SolrInputDocument solrInputDocument = solrTemplate.convertBeanToSolrInputDocument(item);
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();*/

        /*Item fetchedItem = itemCrudRepository.findByItemId(bibliographicMarcForm.getItemId());
        fetchedItem.setCollectionGroupDesignation(bibliographicMarcForm.getCollectionGroupDesignation());
        itemCrudRepository.save(fetchedItem);*/

        /*PartialUpdate partialUpdate = new PartialUpdate("id", "17308926");
        partialUpdate.setValueOfField("CollectionGroupDesignation", bibliographicMarcForm.getCollectionGroupDesignation());
        solrTemplate.saveBean(partialUpdate);
        solrTemplate.commit();*/

        //Bib byBibId = bibSolrCrudRepository.findByBibId(bibliographicMarcForm.getBibId());

        //itemSolrCrudRepository.updateCollectionGroupDesignationByItemId(bibliographicMarcForm.getCollectionGroupDesignation(), bibliographicMarcForm.getItemId());
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

        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setBibId(savedBibliographicEntity.getBibliographicId());
        bibliographicMarcForm.setItemId(itemId);
        bibliographicMarcForm.setNewCollectionGroupDesignation("Private");
        bibliographicMarcForm.setCgdChangeNotes("Notes for updating CGD");

        collectionServiceUtil.updateCGDForItem(bibliographicMarcForm);

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

    @Test
    public void deaccessionItem() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(bibliographicEntity);
        Integer bibliographicId = bibliographicEntity.getBibliographicId();
        assertNotNull(bibliographicId);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibliographicId);
        entityManager.refresh(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity.getBibliographicId());
        assertEquals(bibliographicId, fetchedBibliographicEntity.getBibliographicId());
        assertNotNull(fetchedBibliographicEntity.getItemEntities());
        assertTrue(fetchedBibliographicEntity.getItemEntities().size() > 0);
        assertNotNull(fetchedBibliographicEntity.getItemEntities().get(0));
        assertNotNull(fetchedBibliographicEntity.getItemEntities().get(0).getItemId());

        Integer itemId = fetchedBibliographicEntity.getItemEntities().get(0).getItemId();
        ItemEntity fetchedItemEntity = itemDetailsRepository.findByItemId(itemId);
        entityManager.refresh(fetchedItemEntity);
        assertNotNull(fetchedItemEntity);
        assertNotNull(fetchedItemEntity.getItemId());
        assertEquals(itemId, fetchedItemEntity.getItemId());
        assertEquals(Boolean.FALSE, fetchedItemEntity.isDeleted());

        solrIndexService.indexByBibliographicId(bibliographicId);

        Item fetchedItemSolr = itemCrudRepository.findByItemId(itemId);
        assertNotNull(fetchedItemSolr);
        assertNotNull(fetchedItemSolr.getItemId());
        assertEquals(itemId, fetchedItemSolr.getItemId());

        String barcode = fetchedBibliographicEntity.getItemEntities().get(0).getBarcode();
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setItemId(itemId);
        bibliographicMarcForm.setBarcode(barcode);
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");

        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);

        ItemEntity fetchedItemEntityAfterDeaccession = itemDetailsRepository.findByItemId(itemId);
        entityManager.refresh(fetchedItemEntityAfterDeaccession);
        assertNotNull(fetchedItemEntityAfterDeaccession);
        assertNotNull(fetchedItemEntityAfterDeaccession.getItemId());
        assertEquals(itemId, fetchedItemEntityAfterDeaccession.getItemId());
        assertEquals(Boolean.TRUE, fetchedItemEntityAfterDeaccession.isDeleted());

        Item fetchedItemSolrAfterDeaccession = itemCrudRepository.findByItemId(itemId);
        assertNull(fetchedItemSolrAfterDeaccession);
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