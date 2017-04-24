package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.recap.RecapConstants;
import org.recap.model.solr.SolrIndexRequest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
public class OngoingMatchingAlgorithmJobRestControllerUT extends BaseControllerUT {

    @Test
    public void testStartMatchingAlgorithmJob() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setProcessType(RecapConstants.ONGOING_MATCHING_ALGORITHM_JOB);
        solrIndexRequest.setCreatedDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/ongoingMatchingAlgorithmService/ongoingMatchingAlgorithmJob")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(solrIndexRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }
}
