package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.*;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * Created by akulak on 30/12/16.
 */
public class ReportsUtilUT extends BaseTestCase {

    @Autowired
    ReportsUtil reportsUtil;

    @PersistenceContext
    EntityManager entityManager;


    @Autowired
    ItemCrudRepository itemSolrCrudRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Test
    public void populateILBDCountsForRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");

        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity( 1, 1,false);
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity.getItemId(),4,2,2);

        BibliographicEntity bibliographicEntity1 = saveBibHoldingItemEntity( 2, 1,false);
        ItemEntity itemEntity1 = bibliographicEntity1.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity1.getItemId(),3,3,3);

        BibliographicEntity bibliographicEntity2 = saveBibHoldingItemEntity(3, 1,false);
        ItemEntity itemEntity2 = bibliographicEntity2.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity2.getItemId(),2,2,2);


        BibliographicEntity bibliographicEntity3 = saveBibHoldingItemEntity(1, 1,false);
        ItemEntity itemEntity3 = bibliographicEntity3.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity3.getItemId(),5,2,2);

        BibliographicEntity bibliographicEntity4 = saveBibHoldingItemEntity( 2, 1,false);
        ItemEntity itemEntity4 = bibliographicEntity4.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity4.getItemId(),5,3,3);

        BibliographicEntity bibliographicEntity5 = saveBibHoldingItemEntity( 3, 1,false);
        ItemEntity itemEntity5 = bibliographicEntity5.getItemEntities().get(0);
        savePatronEntity(1,1);
        saveRequestEntity(itemEntity5.getItemId(),5,1,1);


        reportsUtil.populateILBDCountsForRequest(reportsForm,fromDate,toDate);
        assertEquals(1,reportsForm.getIlRequestPulCount());
        assertEquals(1,reportsForm.getIlRequestCulCount());
        assertEquals(1,reportsForm.getIlRequestNyplCount());
        assertEquals(1,reportsForm.getBdRequestPulCount());
        assertEquals(1,reportsForm.getBdRequestCulCount());
        assertEquals(1,reportsForm.getBdRequestNyplCount());
    }

    @Test
    public void populatePartnersCountForRequest() throws Exception{
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");

        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity(1, 3,false);
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity.getItemId(),5,2,2);

        BibliographicEntity bibliographicEntity1 = saveBibHoldingItemEntity( 1, 1,false);
        ItemEntity itemEntity1 = bibliographicEntity1.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity1.getItemId(),5,2,2);

        BibliographicEntity bibliographicEntity2 = saveBibHoldingItemEntity( 2, 3,false);
        ItemEntity itemEntity2 = bibliographicEntity2.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity2.getItemId(),5,3,3);

        BibliographicEntity bibliographicEntity3 = saveBibHoldingItemEntity( 2, 1,false);
        ItemEntity itemEntity3 = bibliographicEntity3.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity3.getItemId(),5,3,3);

        BibliographicEntity bibliographicEntity4 = saveBibHoldingItemEntity(3, 3,false);
        ItemEntity itemEntity4 = bibliographicEntity4.getItemEntities().get(0);
        savePatronEntity(1,1);
        saveRequestEntity(itemEntity4.getItemId(),5,1,1);

        BibliographicEntity bibliographicEntity5 = saveBibHoldingItemEntity( 3, 1,false);
        ItemEntity itemEntity5 = bibliographicEntity5.getItemEntities().get(0);
        savePatronEntity(1,1);
        saveRequestEntity(itemEntity5.getItemId(),5,1,1);

        BibliographicEntity bibliographicEntity6 = saveBibHoldingItemEntity(1, 3,false);
        ItemEntity itemEntity6 = bibliographicEntity6.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity6.getItemId(),4,2,2);

        BibliographicEntity bibliographicEntity7 = saveBibHoldingItemEntity(1, 1,false);
        ItemEntity itemEntity7 = bibliographicEntity7.getItemEntities().get(0);
        savePatronEntity(2,2);
        saveRequestEntity(itemEntity7.getItemId(),4,2,2);

        BibliographicEntity bibliographicEntity8 = saveBibHoldingItemEntity( 2, 3,false);
        ItemEntity itemEntity8 = bibliographicEntity8.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity8.getItemId(),4,3,3);

        BibliographicEntity bibliographicEntity9 = saveBibHoldingItemEntity(2, 1,false);
        ItemEntity itemEntity9 = bibliographicEntity9.getItemEntities().get(0);
        savePatronEntity(3,3);
        saveRequestEntity(itemEntity9.getItemId(),4,3,3);

        BibliographicEntity bibliographicEntity10 = saveBibHoldingItemEntity(3, 3,false);
        ItemEntity itemEntity10 = bibliographicEntity10.getItemEntities().get(0);
        savePatronEntity(1,1);
        saveRequestEntity(itemEntity10.getItemId(),4,1,1);

        BibliographicEntity bibliographicEntity11 = saveBibHoldingItemEntity( 3, 1,false);
        ItemEntity itemEntity11 = bibliographicEntity11.getItemEntities().get(0);
        savePatronEntity(1,1);
        saveRequestEntity(itemEntity11.getItemId(),4,1,1);

        reportsUtil.populatePartnersCountForRequest(reportsForm,fromDate,toDate);
        assertEquals(1,reportsForm.getPhysicalPrivatePulCount());
        assertEquals(1,reportsForm.getPhysicalPrivateCulCount());
        assertEquals(1,reportsForm.getPhysicalPrivateNyplCount());
        assertEquals(1,reportsForm.getPhysicalSharedPulCount());
        assertEquals(1,reportsForm.getPhysicalSharedCulCount());
        assertEquals(1,reportsForm.getEddPrivatePulCount());
        assertEquals(1,reportsForm.getEddPrivateCulCount());
        assertEquals(1,reportsForm.getEddPrivateNyplCount());
        assertEquals(1,reportsForm.getEddSharedOpenPulCount());
        assertEquals(1,reportsForm.getEddSharedOpenCulCount());
        assertEquals(1,reportsForm.getEddSharedOpenNyplCount());

    }

    @Test
    public void populateRequestTypeInformation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = simpleDateFormat.parse("2016-12-30 00:00:00");
        Date toDate = simpleDateFormat.parse("2020-12-31 23:59:59");

        reportsForm.setReportRequestType(Arrays.asList("Retrieval"));
        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity( 1, 1,false);
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        savePatronEntity(3, 3);
        saveRequestEntity(itemEntity.getItemId(), 2, 3, 3);

        BibliographicEntity bibliographicEntity1 = saveBibHoldingItemEntity( 2, 1,false);
        ItemEntity itemEntity1 = bibliographicEntity1.getItemEntities().get(0);
        savePatronEntity(1, 1);
        saveRequestEntity(itemEntity1.getItemId(), 2, 1, 1);

        BibliographicEntity bibliographicEntity2 = saveBibHoldingItemEntity( 3, 1,false);
        ItemEntity itemEntity2 = bibliographicEntity2.getItemEntities().get(0);
        savePatronEntity(2, 2);
        saveRequestEntity(itemEntity2.getItemId(), 2, 2, 2);

        reportsUtil.populateRequestTypeInformation(reportsForm, fromDate, toDate);
        assertEquals(1, reportsForm.getRetrievalRequestPulCount());
        assertEquals(1, reportsForm.getRetrievalRequestCulCount());
        assertEquals(1, reportsForm.getRetrievalRequestNyplCount());

        reportsForm.setReportRequestType(Arrays.asList("Recall"));
        BibliographicEntity bibliographicEntity3 = saveBibHoldingItemEntity(1, 1,false);
        ItemEntity itemEntity3 = bibliographicEntity3.getItemEntities().get(0);
        savePatronEntity(2, 2);
        saveRequestEntity(itemEntity3.getItemId(), 3, 2, 2);

        BibliographicEntity bibliographicEntity4 = saveBibHoldingItemEntity(2, 1,false);
        ItemEntity itemEntity4 = bibliographicEntity4.getItemEntities().get(0);
        savePatronEntity(1, 1);
        saveRequestEntity(itemEntity4.getItemId(), 3, 1, 1);

        BibliographicEntity bibliographicEntity5 = saveBibHoldingItemEntity(3, 1,false);
        ItemEntity itemEntity5 = bibliographicEntity5.getItemEntities().get(0);
        savePatronEntity(2, 2);
        saveRequestEntity(itemEntity5.getItemId(), 3, 2, 2);
        reportsUtil.populateRequestTypeInformation(reportsForm, fromDate, toDate);

        assertEquals(1, reportsForm.getRecallRequestPulCount());
        assertEquals(1, reportsForm.getRecallRequestCulCount());
        assertEquals(1, reportsForm.getRecallRequestNyplCount());
    }


    @Test
    public void populateAccessionDeaccessionItemCounts() throws Exception{
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String requestedFromDate=simpleDateFormat.format(new Date());
        String requestedToDate= simpleDateFormat.format(new Date());

        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity(1, 2,false);
        indexBibHoldingItem(bibliographicEntity);

        BibliographicEntity bibliographicEntity1 = saveBibHoldingItemEntity(2, 2,false);
        indexBibHoldingItem(bibliographicEntity1);

        BibliographicEntity bibliographicEntity2 = saveBibHoldingItemEntity(3, 2,false);
        indexBibHoldingItem(bibliographicEntity2);

        BibliographicEntity bibliographicEntity3 = saveBibHoldingItemEntity(1, 1,false);
        indexBibHoldingItem(bibliographicEntity3);

        BibliographicEntity bibliographicEntity4 = saveBibHoldingItemEntity(2, 1,false);
        indexBibHoldingItem(bibliographicEntity4);

        BibliographicEntity bibliographicEntity5 = saveBibHoldingItemEntity( 3, 1,false);
        indexBibHoldingItem(bibliographicEntity5);

        BibliographicEntity bibliographicEntity6 = saveBibHoldingItemEntity(1, 3,false);
        indexBibHoldingItem(bibliographicEntity6);

        BibliographicEntity bibliographicEntity7 = saveBibHoldingItemEntity( 2, 3,false);
        indexBibHoldingItem(bibliographicEntity7);

        BibliographicEntity bibliographicEntity8 = saveBibHoldingItemEntity( 3, 3,false);
        indexBibHoldingItem(bibliographicEntity8);

        BibliographicEntity bibliographicEntity9 = saveBibHoldingItemEntity( 1, 2,true);
        indexBibHoldingItem(bibliographicEntity9);

        BibliographicEntity bibliographicEntity10 = saveBibHoldingItemEntity( 2, 2,true);
        indexBibHoldingItem(bibliographicEntity10);

        BibliographicEntity bibliographicEntity11 = saveBibHoldingItemEntity( 3, 2,true);
        indexBibHoldingItem(bibliographicEntity11);

        BibliographicEntity bibliographicEntity12 = saveBibHoldingItemEntity( 1, 1,true);
        indexBibHoldingItem(bibliographicEntity12);

        BibliographicEntity bibliographicEntity13 = saveBibHoldingItemEntity(2, 1,true);
        indexBibHoldingItem(bibliographicEntity13);

        BibliographicEntity bibliographicEntity14 = saveBibHoldingItemEntity( 3, 1,true);
        indexBibHoldingItem(bibliographicEntity14);

        BibliographicEntity bibliographicEntity15 = saveBibHoldingItemEntity(1, 3,true);
        indexBibHoldingItem(bibliographicEntity15);

        BibliographicEntity bibliographicEntity16 = saveBibHoldingItemEntity(2, 3,true);
        indexBibHoldingItem(bibliographicEntity16);

        BibliographicEntity bibliographicEntity17 = saveBibHoldingItemEntity( 3, 3,true);
        indexBibHoldingItem(bibliographicEntity17);

        reportsUtil.populateAccessionDeaccessionItemCounts(reportsForm,requestedFromDate,requestedToDate);
        assertEquals(1,reportsForm.getAccessionOpenPulCount());
        assertEquals(1,reportsForm.getAccessionOpenCulCount());
        assertEquals(1,reportsForm.getAccessionOpenNyplCount());
        assertEquals(1,reportsForm.getAccessionSharedPulCount());
        assertEquals(1,reportsForm.getAccessionSharedCulCount());
        assertEquals(1,reportsForm.getAccessionSharedNyplCount());
        assertEquals(1,reportsForm.getAccessionPrivatePulCount());
        assertEquals(1,reportsForm.getAccessionPrivateCulCount());
        assertEquals(1,reportsForm.getAccessionPrivateNyplCount());
        assertEquals(1,reportsForm.getDeaccessionOpenPulCount());
        assertEquals(1,reportsForm.getDeaccessionOpenCulCount());
        assertEquals(1,reportsForm.getDeaccessionOpenNyplCount());
        assertEquals(1,reportsForm.getDeaccessionSharedPulCount());
        assertEquals(1,reportsForm.getDeaccessionSharedCulCount());
        assertEquals(1,reportsForm.getDeaccessionSharedNyplCount());
        assertEquals(1,reportsForm.getDeaccessionPrivatePulCount());
        assertEquals(1,reportsForm.getDeaccessionPrivateCulCount());
        assertEquals(1,reportsForm.getDeaccessionPrivateNyplCount());
    }

    @Test
    public void populateCGDItemCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();

        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity(1, 2,false);
        indexBibHoldingItem(bibliographicEntity);

        BibliographicEntity bibliographicEntity1 = saveBibHoldingItemEntity(2, 2,false);
        indexBibHoldingItem(bibliographicEntity1);

        BibliographicEntity bibliographicEntity2 = saveBibHoldingItemEntity(3, 2,false);
        indexBibHoldingItem(bibliographicEntity2);

        BibliographicEntity bibliographicEntity3 = saveBibHoldingItemEntity(1, 1,false);
        indexBibHoldingItem(bibliographicEntity3);

        BibliographicEntity bibliographicEntity4 = saveBibHoldingItemEntity(2, 1,false);
        indexBibHoldingItem(bibliographicEntity4);

        BibliographicEntity bibliographicEntity5 = saveBibHoldingItemEntity(3, 1,false);
        indexBibHoldingItem(bibliographicEntity5);

        BibliographicEntity bibliographicEntity6 = saveBibHoldingItemEntity(1, 3,false);
        indexBibHoldingItem(bibliographicEntity6);

        BibliographicEntity bibliographicEntity7 = saveBibHoldingItemEntity( 2, 3,false);
        indexBibHoldingItem(bibliographicEntity7);

        BibliographicEntity bibliographicEntity8 = saveBibHoldingItemEntity( 3, 3,false);
        indexBibHoldingItem(bibliographicEntity8);


        reportsUtil.populateCGDItemCounts(reportsForm);
        assertEquals(1,reportsForm.getOpenPulCgdCount());
        assertEquals(1,reportsForm.getOpenCulCgdCount());
        assertEquals(1,reportsForm.getOpenNyplCgdCount());
        assertEquals(1,reportsForm.getSharedPulCgdCount());
        assertEquals(1,reportsForm.getSharedCulCgdCount());
        assertEquals(1,reportsForm.getSharedNyplCgdCount());
        assertEquals(1,reportsForm.getPrivatePulCgdCount());
        assertEquals(1,reportsForm.getPrivateCulCgdCount());
        assertEquals(1,reportsForm.getPrivateNyplCgdCount());
    }

    @Test
    public void deaccessionReportFieldsInformation() throws Exception{
        ReportsForm reportsForm = new ReportsForm();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        reportsForm.setAccessionDeaccessionFromDate(simpleDateFormat.format(new Date()));
        reportsForm.setAccessionDeaccessionToDate(simpleDateFormat.format(new Date()));
        BibliographicEntity bibliographicEntity = saveBibHoldingItemEntity(1, 2, true);
        indexBibHoldingItem(bibliographicEntity);
            List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = reportsUtil.deaccessionReportFieldsInformation(reportsForm);
            for (DeaccessionItemResultsRow deaccessionItemResultsRow : deaccessionItemResultsRowList) {
                assertEquals("Shared",deaccessionItemResultsRow.getCgd());
                assertEquals("b3",deaccessionItemResultsRow.getItemBarcode());
                assertEquals("PUL",deaccessionItemResultsRow.getDeaccessionOwnInst());
                ItemChangeLogEntity byRecordId = itemChangeLogDetailsRepository.findByRecordIdAndOperationType(deaccessionItemResultsRow.getItemId(),"Deaccession");
                if(byRecordId.getOperationType().equalsIgnoreCase("Deaccession")){
                    assertEquals("test notes",deaccessionItemResultsRow.getDeaccessionNotes());
                }
            }
    }

    private BibliographicEntity saveBibHoldingItemEntity(Integer owningInstitutionId, Integer collectionGroupId,boolean isDeleted) throws Exception {
        Random random = new Random();

        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        String owningInstitutionBibId= String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(new Random().nextInt());
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("ut");
        bibliographicEntity.setLastUpdatedBy("ut");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("ut");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("ut");
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(new Random().nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(new Random().nextInt());
        itemEntity.setBarcode("b3");
        itemEntity.setCustomerCode("c1");
        itemEntity.setCallNumber("cn1");
        itemEntity.setCallNumberType("ct1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCopyNumber(1);
        itemEntity.setOwningInstitutionId(owningInstitutionId);
        itemEntity.setCollectionGroupId(collectionGroupId);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("ut");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("ut");
        itemEntity.setUseRestrictions("no");
        itemEntity.setVolumePartYear("v3");
        itemEntity.setOwningInstitutionItemId(String.valueOf(new Random().nextInt()));
        itemEntity.setDeleted(isDeleted);

        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    private void indexBibHoldingItem(BibliographicEntity savedBibliographicEntity) {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument solrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(savedBibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
        solrTemplate.saveDocument(solrInputDocument);
        solrTemplate.commit();
    }

    private void savePatronEntity(Integer patronId, Integer patronInstitutionID) throws Exception {
        PatronEntity patronEntity = new PatronEntity();
        patronEntity.setPatronId(patronId);
        patronEntity.setInstitutionId(patronInstitutionID);
        patronEntity.setInstitutionIdentifier("test");
        patronEntity.setEmailId("testmail");
        patronDetailsRepository.save(patronEntity);
    }

    private void saveRequestEntity(Integer itemId, Integer requestTypeId, Integer requestingInstID, Integer patronID) throws Exception {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(itemId);
        requestItemEntity.setRequestTypeId(requestTypeId);
        requestItemEntity.setRequestingInstitutionId(requestingInstID);
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setPatronId(patronID);
        requestItemEntity.setRequestPosition(new Random().nextInt());
        requestItemEntity.setStopCode("s1");
        requestItemDetailsRepository.save(requestItemEntity);
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
