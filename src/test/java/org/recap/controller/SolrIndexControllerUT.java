package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.admin.SolrAdmin;
import org.recap.executors.BibItemIndexExecutorService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
        doNothing().when(bibSolrCrudRepository).deleteAll();
        doNothing().when(itemCrudRepository).deleteAll();
        doNothing().when(solrAdmin).unloadTempCores();
        doNothing().when(bibItemIndexExecutorService).index(getSolrIndexRequest());
        when(bibItemIndexExecutorService.getStopWatch()).thenReturn(new StopWatch());
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
        String response =solrIndexController.report();
        assertNotNull(response);
        assertTrue(response.contains("Status  : Done"));
    }

    private SolrIndexRequest getSolrIndexRequest(){
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        return solrIndexRequest;
    }

}