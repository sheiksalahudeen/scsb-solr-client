package org.recap.controller;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 25/1/17.
 */
public class UpdateItemStatusControllerUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(UpdateItemStatusControllerUT.class);

    @Mock
    UpdateItemStatusController updateItemStatusController;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    UpdateCgdUtil updateCgdUtil;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateCgdForItem() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        String itemBarcode = bibliographicEntity.getItemEntities().get(0).getBarcode();
        Mockito.when(updateItemStatusController.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(updateItemStatusController.getUpdateCgdUtil()).thenReturn(updateCgdUtil);
        Mockito.when(updateItemStatusController.getItemDetailsRepository().findByBarcode(itemBarcode)).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(updateItemStatusController.updateCgdForItem(itemBarcode)).thenCallRealMethod();
        Mockito.when(updateItemStatusController.getLogger()).thenCallRealMethod();
        updateItemStatusController.getLogger();
        String status = updateItemStatusController.updateCgdForItem(itemBarcode);
        assertNotNull(status);
        assertEquals(status,"Solr Indexing Successful");
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(updateItemStatusController.getItemDetailsRepository()).thenCallRealMethod();
        Mockito.when(updateItemStatusController.getUpdateCgdUtil()).thenCallRealMethod();
        assertNotEquals(itemDetailsRepository,updateItemStatusController.getItemDetailsRepository());
        assertNotEquals(updateCgdUtil,updateItemStatusController.getUpdateCgdUtil());
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        File holdingsContentFile = getHoldingsContentFile();
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("3894123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        return savedBibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }
    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }


}