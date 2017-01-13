package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.recap.RecapConstants;
import org.recap.model.reports.ReportsRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
public class ReportsRestControllerUT extends BaseControllerUT {

    @Test
    public void accessionDeaccessionCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/accessionDeaccessionCounts")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void cgdItemCounts() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setOwningInstitutions(Arrays.asList("CUL", "PUL", "NYPL"));
        reportsRequest.setCollectionGroupDesignations(Arrays.asList("Private", "Open", "Shared"));
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/cgdItemCounts")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    @Test
    public void deaccessionResults() throws Exception {
        ReportsRequest reportsRequest = new ReportsRequest();
        reportsRequest.setAccessionDeaccessionFromDate("09/27/2016");
        reportsRequest.setAccessionDeaccessionToDate("01/27/2017");
        reportsRequest.setDeaccessionOwningInstitution("PUL");
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/reportsService/deaccessionResults")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(reportsRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
        return headers;
    }
}
