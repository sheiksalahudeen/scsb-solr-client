package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.model.solr.Bib;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Item;
import org.recap.repository.solr.main.BibSolrDocumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by premkb on 2/8/16.
 */
public class SearchRecordsControllerUT extends BaseControllerUT{

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    private SearchRecordsController searchRecordsController=new SearchRecordsController();

    @Mock
    private BibSolrDocumentRepository bibSolrDocumentRepository;

    SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchRecordsController).build();
        List<BibItem> bibItems = new ArrayList<>();
        when(bibSolrDocumentRepository.search(getSearchRecordsRequest(),new PageRequest(0, 10))).thenReturn(bibItems);
        when(bibSolrDocumentRepository.search(getSearchRecordsRequest(), new PageRequest(getSearchRecordsRequest().getPageNumber(), getSearchRecordsRequest().getPageSize()))).thenReturn(bibItems);
    }


    @Test
    public void searchRecords() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(post("/search")
                .param("model",String.valueOf(model)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void search() throws Exception{
        ModelAndView modelAndView = searchRecordsController.search(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void searchPrevious() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchPrevious(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void searchNext() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchNext(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void searchFirst() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchFirst(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void searchLast() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchLast(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void clear() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchLast(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void newSearch() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchLast(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    @Test
    public void requestRecords() throws Exception{
        ModelAndView modelAndView = searchRecordsController.searchLast(getSearchRecordsRequest(),bindingResult,model);
        assertNotNull(modelAndView);
        assertEquals("searchRecords",modelAndView.getViewName());
    }

    private SearchRecordsRequest getSearchRecordsRequest(){
        searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setShowResults(true);
        List<SearchResultRow> searchResultRows = new ArrayList<>();
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title1");
        searchResultRow.setBibId(1);
        searchResultRows.add(searchResultRow);
        searchRecordsRequest.setSearchResultRows(searchResultRows);
        List<String> availability = new ArrayList<>();
        availability.add("Available");
        searchRecordsRequest.setAvailability(availability);
        searchRecordsRequest.setFieldName("245");
        searchRecordsRequest.setFieldValue("Stay Hungry");
        List<String> owningInstitutions = new ArrayList<>();
        owningInstitutions.add("NYPL");
        searchRecordsRequest.setOwningInstitutions(owningInstitutions);
        List<String> collectionGroupDesignations = new ArrayList<>();
        collectionGroupDesignations.add("Shared");
        searchRecordsRequest.setCollectionGroupDesignations(collectionGroupDesignations);
        List<String> materialTypes = new ArrayList<>();
        materialTypes.add("Monograph");
        searchRecordsRequest.setMaterialTypes(materialTypes);
        searchRecordsRequest.setTotalPageCount(1);
        searchRecordsRequest.setTotalRecordsCount(new Long(1));
        searchRecordsRequest.setSelectAll(false);
        searchRecordsRequest.setIndex(1);
        searchRecordsRequest.setPageNumber(1);
        searchRecordsRequest.setPageSize(1);
        return searchRecordsRequest;
    }





}