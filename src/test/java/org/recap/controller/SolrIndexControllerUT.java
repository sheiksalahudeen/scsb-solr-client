package org.recap.controller;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by premkb on 2/8/16.
 */
public class SolrIndexControllerUT extends BaseControllerUT{

    @InjectMocks
    SolrIndexController solrIndexController=new SolrIndexController();

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @Mock
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Mock
    BibSolrCrudRepository bibSolrCrudRepository;

    @Mock
    ItemCrudRepository itemCrudRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Mock
    SolrAdmin solrAdmin;

    @Before
    public void setUp()throws Exception {
        MockitoAnnotations.initMocks(this);
        doNothing().when(bibSolrCrudRepository).deleteAll();
        doNothing().when(itemCrudRepository).deleteAll();
        doNothing().when(solrAdmin).unloadTempCores();
        doNothing().when(bibItemIndexExecutorService).index(getSolrIndexRequest());
    }

    @Test
    public void solrIndexer()throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/")
                .param("model",String.valueOf(model)))
                .andReturn();
        String reponse = mvcResult.getResponse().getContentAsString();
        assertNotNull(reponse);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void fullIndex()throws Exception{
        String response =solrIndexController.fullIndex(getSolrIndexRequest(),bindingResult,model);
        assertNotNull(response);
        assertEquals("solrIndexer",response);
    }

    @Test
    public void report()throws Exception{
        String response =solrIndexController.report("");
        assertNotNull(response);
        assertTrue(response.contains("Index process initiated!"));
    }

    private SolrIndexRequest getSolrIndexRequest(){
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setDocType("");
        return solrIndexRequest;
    }

    @Test
    public void indexByBibliographicId() throws Exception {
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        Integer bibliographicId = savedBibliographicEntity.getBibliographicId();
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());

        this.mockMvc.perform(post("/solrIndexer/indexByBibliographicId")
                .contentType(contentType)
                .content(String.valueOf(bibliographicId)));

        MvcResult mvcResult = this.mockMvc.perform(get("/bibSolr/search/findByBibId")
                .param("bibId", String.valueOf(bibliographicId)))
                .andExpect(status().isOk())
                .andReturn();
        Bib bib = (Bib) jsonToObject(mvcResult.getResponse().getContentAsString(), Bib.class);
        assertNotNull(bib);
    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem() throws Exception {
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
        String barcode = String.valueOf(random.nextInt());
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
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