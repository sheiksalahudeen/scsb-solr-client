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
import static org.mockito.Mockito.*;
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

    @Mock
    SolrAdmin solrAdmin;

    @Before
    public void setUp()throws Exception {
        MockitoAnnotations.initMocks(this);
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
        assertTrue(response.contains("Total number of records processed :"));
    }

    @Test
    public void report()throws Exception{
        String response =solrIndexController.report("");
        assertNotNull(response);
        assertTrue(response.contains("Index process initiated!"));
    }

    private SolrIndexRequest getSolrIndexRequest(){
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setDocType("");
        return solrIndexRequest;
    }

}