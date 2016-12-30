package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.model.search.CollectionForm;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.util.ReportsUtil;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by rajeshbabuk on 21/10/16.
 */
public class ReportsControllerUT extends BaseControllerUT {

    @Mock
    Model model;

    @Mock
    BindingResult bindingResult;

    @InjectMocks
    ReportsController reportsController;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    ReportsUtil reportsUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(reportsController).build();
    }

    @Test
    public void reports() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/reports")
                .param("model", String.valueOf(model)))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    @Test
    public void reportCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType("Request");
        reportsForm.setRequestFromDate("11/01/2016");
        reportsForm.setRequestToDate("12/01/2016");
        reportsForm.setShowBy("IL_Bd");
        ModelAndView modelAndView = reportsController.reportCounts(reportsForm,model);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.Simple_Date_Format_REPORTS);
        String fromDate = reportsForm.getRequestFromDate();
        String toDate = reportsForm.getRequestToDate();
        Date requestFromDate = reportsUtil.getFromDate(simpleDateFormat.parse(fromDate));
        Date requestToDate = reportsUtil.getToDate(simpleDateFormat.parse(toDate));
        reportsUtil.populateILBDCountsForRequest(reportsForm, requestFromDate, requestToDate);
        assertNotNull(modelAndView);
        assertEquals("searchRecords", modelAndView.getViewName());
    }

    @Test
    public void cgdCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setOwningInstitutions(Arrays.asList("PUL"));
        reportsForm.setCollectionGroupDesignations(Arrays.asList("Shared"));
        ModelAndView modelAndView = reportsController.cgdCounts(reportsForm,model);
        reportsUtil.populateCGDItemCounts(reportsForm);
        assertNotNull(modelAndView);
        assertEquals("reports :: #cgdTable",modelAndView.getViewName());
    }

    @Test
    public void deaccessionInformation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestFromDate("11/01/2016");
        reportsForm.setRequestToDate("12/01/2016");
        ModelAndView modelAndView = reportsController.deaccessionInformation(reportsForm.getRequestFromDate(),reportsForm.getRequestToDate(),"PUL",model);
        List<DeaccessionItemResultsRow> deaccessionItemResultsRowList = reportsUtil.deaccessionReportFieldsInformation(reportsForm.getRequestFromDate(), reportsForm.getRequestToDate(), "PUL");
        assertNotNull(deaccessionItemResultsRowList);
        assertNotNull(modelAndView);
        assertEquals("reports :: #deaccessionInformation",modelAndView.getViewName());

    }

}
