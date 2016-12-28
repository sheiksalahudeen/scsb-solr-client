package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.userManagement.UserDetailsForm;
import org.recap.util.MarcRecordViewUtil;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by rajeshbabuk on 17/10/16.
 */
public class CollectionUpdateControllerUT extends BaseControllerUT {

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    CollectionUpdateController collectionUpdateController;

    @Mock
    MarcRecordViewUtil marcRecordViewUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(collectionUpdateController).build();
        UserDetailsForm userDetailsForm= new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(2);
        userDetailsForm.setRecapUser(false);
        when(marcRecordViewUtil.buildBibliographicMarcForm(1,2,userDetailsForm)).thenReturn(new BibliographicMarcForm());
    }

    @Test
    public void openMarcRecord() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/collectionUpdate?bibId=1&itemId=2")
                .param("model", String.valueOf(model)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void collectionUpdate() throws Exception {
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        ModelAndView modelAndView = collectionUpdateController.collectionUpdate(bibliographicMarcForm, bindingResult, model);
        assertNotNull(modelAndView);
        assertEquals("collectionUpdateView", modelAndView.getViewName());
    }
}
