package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.recap.model.deaccession.DeAccessionSolrRequest;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 15/2/17.
 */
public class DeaccessionSolrControllerUT extends BaseControllerUT {

    @Test
    public void deaccessionInSolr() throws Exception {
        DeAccessionSolrRequest deAccessionSolrRequest = new DeAccessionSolrRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/deaccessionInSolrService/deaccessionInSolr")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(deAccessionSolrRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }
}
