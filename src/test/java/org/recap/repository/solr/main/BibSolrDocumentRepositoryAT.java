package org.recap.repository.solr.main;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Ignore;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.solr.Bib;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.solr.impl.BibSolrDocumentRepositoryImpl;
import org.recap.util.BibJSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 14/7/16.
 */
@Ignore
public class BibSolrDocumentRepositoryAT extends BaseTestCase {

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Autowired
    BibSolrDocumentRepository bibSolrDocumentRepository;

    @Autowired
    BibSolrDocumentRepositoryImpl bibSolrDocumentRepositoryImpl;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Test
    public void search() throws Exception {
        Random random = new Random();
        File bibContentFile = getUnicodeContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(1, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity.getContent());

        SolrInputDocument solrInputDocument = new BibJSONUtil().generateBibAndItemsForIndex(fetchedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        assertNotNull(solrInputDocument);

        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.softCommit();

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("BibId");
        searchRecordsRequest.setFieldValue(String.valueOf(fetchedBibliographicEntity.getBibliographicId()));

        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertTrue(bibItems.size() > 0);
        assertEquals(bibliographicEntity.getOwningInstitutionBibId(), bibItems.get(0).getOwningInstitutionBibId());
        solrTemplate.rollback();

        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    public void deleteByDocId(String docIdParam, String docIdValue) throws IOException, SolrServerException {
        UpdateResponse updateResponse = solrTemplate.getSolrClient().deleteByQuery(docIdParam+":"+docIdValue);
        solrTemplate.commit();
    }

    public File getUnicodeContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    @Test
    public void searchBibItemsBasedOnTitleStartsWithField() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName(RecapConstants.TITLE_STARTS_WITH);
        searchRecordsRequest.setFieldValue("Semiznachnye tabli︠t︡sy");
        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
    }

    @Test
    public void searchByAllFieldsWithBarcodeValue() throws Exception {
        Random random = new Random();
        File bibContentFile = getUnicodeContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        String owningInstitutionHoldingsId = String.valueOf(random.nextInt());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        String barcode = String.valueOf(random.nextInt());

        BibliographicEntity bibliographicEntity = getBibliographicEntity(sourceBibContent, owningInstitutionBibId);
        HoldingsEntity holdingsEntity = getHoldingsEntity(owningInstitutionHoldingsId);
        ItemEntity itemEntity = getItemEntity(owningInstitutionItemId, barcode, holdingsEntity);
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(1, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertEquals(owningInstitutionBibId, fetchedBibliographicEntity.getOwningInstitutionBibId());

        SolrInputDocument solrInputDocument = new BibJSONUtil().generateBibAndItemsForIndex(fetchedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
        assertNotNull(solrInputDocument);

        solrTemplate.saveDocument(solrInputDocument);

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName(null); // All fields.
        searchRecordsRequest.setFieldValue(barcode);

        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertTrue(bibItems.size() > 0);
        assertEquals(bibliographicEntity.getOwningInstitutionBibId(), bibItems.get(0).getOwningInstitutionBibId());
        assertTrue(bibliographicEntity.getItemEntities().size() > 0);
        assertTrue(bibItems.get(0).getItems().size() > 0);
        assertEquals(bibliographicEntity.getItemEntities().get(0).getBarcode(), bibItems.get(0).getItems().get(0).getBarcode());
        solrTemplate.rollback();

        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    @Test
    public void searchByAllFieldsWithTitleValue() throws Exception {
        Random random = new Random();
        File bibContentFile = getUnicodeContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        String owningInstitutionHoldingsId = String.valueOf(random.nextInt());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        String barcode = String.valueOf(random.nextInt());

        BibliographicEntity bibliographicEntity = getBibliographicEntity(sourceBibContent, owningInstitutionBibId);
        HoldingsEntity holdingsEntity = getHoldingsEntity(owningInstitutionHoldingsId);
        ItemEntity itemEntity = getItemEntity(owningInstitutionItemId, barcode, holdingsEntity);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(1, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertEquals(owningInstitutionBibId, fetchedBibliographicEntity.getOwningInstitutionBibId());

        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(fetchedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
        assertNotNull(solrInputDocument);

        solrTemplate.saveDocument(solrInputDocument);

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName(null); // All fields.
        searchRecordsRequest.setFieldValue("al-Ḥuṭayʼah :");

        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertTrue(bibItems.size() > 0);
        assertTrue(bibliographicEntity.getItemEntities().size() > 0);

        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(new String(bibliographicEntity.getContent(), Charset.forName("UTF-8")));
        assertNotNull(records);
        assertTrue(records.size() > 0);

        String sourceTitle245a = bibJSONUtil.getTitleDisplay(records.get(0));
        assertNotNull(sourceTitle245a);
        assertEquals(sourceTitle245a, "al-Ḥuṭayʼah : fī sīratihi wa-nafsīyatihi wa-shiʻrihi /");
        solrTemplate.rollback();

        deleteByDocId("BibId",savedBibliographicEntity.getBibliographicId().toString());
        deleteByDocId("HoldingId",savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId().toString());
        deleteByDocId("ItemId",savedBibliographicEntity.getItemEntities().get(0).getItemId().toString());
    }

    private BibliographicEntity getBibliographicEntity(String sourceBibContent, String owningInstitutionBibId) {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");
        return bibliographicEntity;
    }

    private HoldingsEntity getHoldingsEntity(String owningInstitutionHoldingsId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        return holdingsEntity;
    }

    private ItemEntity getItemEntity(String owningInstitutionItemId, String barcode, HoldingsEntity holdingsEntity) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        return itemEntity;
    }

    @Test(expected = Exception.class)
    public void testSolrQueryMaxParameters() {
        List<Integer> itemIds = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) {
            itemIds.add(i);
        }
        SimpleQuery query = new SimpleQuery(new Criteria(RecapConstants.ITEM_ID).in(itemIds));
        query.setRows(itemIds.size());
        solrTemplate.queryForPage(query, Item.class, RequestMethod.POST);
    }

    @Test
    public void testSolrQueryMaxParametersByPartitioning() {
        Random random = new Random();
        List<Integer> itemIds = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) {
            itemIds.add(random.nextInt());
        }
        List<List<Integer>> partitions = Lists.partition(new ArrayList<Integer>(itemIds), 1000);
        for (List<Integer> partitionItemIds : partitions) {
            SimpleQuery query = new SimpleQuery(new Criteria(RecapConstants.ITEM_ID).in(partitionItemIds));
            query.setRows(partitionItemIds.size());
            ScoredPage<Item> itemsPage = solrTemplate.queryForPage(query, Item.class, RequestMethod.POST);
            assertNotNull(itemsPage);
        }
    }

    /*@Test
    public void testGetModifiedText() throws Exception {
        String searchText = "Test-Title.";
        String modifiedText = solrQueryBuilder.getModifiedText(searchText);
        assertEquals(modifiedText, "Test\\-Title\\.");
    }*/

    @Test
    public void searchBoundWithDocuments() throws Exception {
        Map<String, List<Object>> stringListMap = saveBoundWithSolrDocs();
        assertNotNull(stringListMap);

        List<Object> bibs =  stringListMap.get("Bibs");
        assertNotNull(bibs);

        List<Object> items =  stringListMap.get("Items");
        assertNotNull(items);
        Item item1 = (Item) items.get(0);

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Barcode");
        searchRecordsRequest.setFieldValue(item1.getBarcode());
        searchRecordsRequest.getOwningInstitutions().add("CUL");

        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertEquals(bibItems.size(), 2);
    }

    private Map<String, List<Object>> saveBoundWithSolrDocs() throws InterruptedException {
        Map<String, List<Object>> map = new HashMap<>();
        Random random = new Random();
        List<Bib> bibs = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        List<String> issnList = new ArrayList<>();
        List<String>isbnList = new ArrayList<>();
        List<String> oclcNumberList = new ArrayList<>();
        issnList.add("0394469756");
        isbnList.add("0394469755");
        oclcNumberList.add("00133182");
        oclcNumberList.add("00440790");

        Integer bibId1 = random.nextInt();
        Integer holdingsId1 = random.nextInt();
        Integer holdingsId2 = random.nextInt();
        Integer itemId1 = random.nextInt();
        Integer barcode = random.nextInt();

        Bib bib1 = new Bib();
        bib1.setBibId(bibId1);
        bib1.setDocType("Bib");
        bib1.setTitle("Test Bib Doc 1");
        String[] titleTokened1 = bib1.getTitle().split(" ");
        bib1.setAuthorDisplay("Nancy L");
        bib1.setPublisher("McClelland & Stewart, limited");
        bib1.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib1.setIssn(issnList);
        bib1.setIsbn(isbnList);
        bib1.setOclcNumber(oclcNumberList);
        bib1.setPublicationDate("1960");
        bib1.setMaterialType("Material Type 1");
        bib1.setNotes("Bibliographical footnotes 1");
        bib1.setOwningInstitution("CUL");
        bib1.setSubject("Arab countries Politics and government.");
        bib1.setPublicationPlace("Paris");
        bib1.setLccn("71448228");
        bib1.setHoldingsIdList(Arrays.asList(holdingsId1));
        bib1.setBibItemIdList(Arrays.asList(itemId1));

        Integer bibId2 = random.nextInt();

        Bib bib2 = new Bib();
        bib2.setBibId(bibId2);
        bib2.setDocType("Bib");
        bib2.setTitle("Test Bib Doc 2");
        String[] titleTokened2 = bib2.getTitle().split(" ");
        bib2.setAuthorDisplay("Nancy L");
        bib2.setPublisher("McClelland & Stewart, limited");
        bib2.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib2.setIssn(issnList);
        bib2.setIsbn(isbnList);
        bib2.setOclcNumber(oclcNumberList);
        bib2.setPublicationDate("1960");
        bib2.setMaterialType("Material Type 2");
        bib2.setNotes("Bibliographical footnotes 2");
        bib2.setOwningInstitution("CUL");
        bib2.setSubject("Arab countries Politics and government.");
        bib2.setPublicationPlace("Paris");
        bib2.setLccn("71448229");
        bib2.setHoldingsIdList(Arrays.asList(holdingsId2));
        bib2.setBibItemIdList(Arrays.asList(itemId1));

        Item item1 = new Item();
        item1.setItemId(itemId1);
        item1.setDocType("Item");
        item1.setBarcode(String.valueOf(barcode));
        item1.setAvailability("Available");
        item1.setCollectionGroupDesignation("Shared");
        item1.setCallNumberSearch("H3");
        item1.setCustomerCode("Test Cust Code");
        item1.setUseRestriction("In Library Use");
        item1.setVolumePartYear("V.1 1982");
        item1.setItemBibIdList(Arrays.asList(bibId1, bibId2));
        item1.setHoldingsIdList(Arrays.asList(holdingsId1, holdingsId2));

        bibs.add(bib1);
        bibs.add(bib2);
        items.add(item1);

        bibSolrCrudRepository.save(bib1);
        itemCrudRepository.save(item1);
        bibSolrCrudRepository.save(bib2);
        solrTemplate.commit();
        Thread.sleep(2000);

        map.put("Bibs", Arrays.asList(bib1, bib2));
        map.put("Items", Arrays.asList(item1));
        return map;
    }

    @Test
    public void testAllFieldsSearchAndDiacriticsFields() throws Exception {
        Bib bib = saveBib();
        assertNotNull(bib);

        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Title_search");
        searchRecordsRequest.setFieldValue("lincoln inn");
        searchRecordsRequest.getOwningInstitutions().add("CUL");

        Map searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertEquals(bibItems.size(), 1);

        searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName(null);
        searchRecordsRequest.setFieldValue("lincoln inn");
        searchRecordsRequest.getOwningInstitutions().add("CUL");

        searchRecordMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        bibItems = (List<BibItem>) searchRecordMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
        assertEquals(bibItems.size(), 1);

    }

    private Bib saveBib() throws Exception {
        Random random = new Random();
        Integer bibId = random.nextInt();
        Integer holdingsId = random.nextInt();
        Integer itemId = random.nextInt();

        Bib bib = new Bib();
        bib.setBibId(bibId);
        bib.setDocType("Bib");
        bib.setTitle("Lincoln's Inn Title");
        bib.setAuthorDisplay("Nancy L");
        bib.setPublisher("McClelland & Stewart, limited");
        bib.setImprint("Toronto, McClelland & Stewart, limited [c1926]");
        bib.setPublicationDate("1960");
        bib.setMaterialType("Material Type 1");
        bib.setNotes("Bibliographical footnotes 1");
        bib.setOwningInstitution("CUL");
        bib.setSubject("Arab countries Politics and government.");
        bib.setPublicationPlace("Paris");
        bib.setLccn("71448228");
        bib.setHoldingsIdList(Arrays.asList(holdingsId));
        bib.setBibItemIdList(Arrays.asList(itemId));

        bibSolrCrudRepository.save(bib);
        solrTemplate.commit();
        Thread.sleep(2000);
        return bib;
    }

    @Test
    public void testMultipleBarcodeSearch() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Barcode");
        searchRecordsRequest.setFieldValue("33433009087036, 33433021391382");

        Map<String, Object> searchMap = bibSolrDocumentRepository.search(searchRecordsRequest);
        List<BibItem> bibItems = (List<BibItem>) searchMap.get(RecapConstants.SEARCH_SUCCESS_RESPONSE);
        assertNotNull(bibItems);
    }

}