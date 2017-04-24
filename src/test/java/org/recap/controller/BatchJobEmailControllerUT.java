package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
public class BatchJobEmailControllerUT extends BaseControllerUT {

    @Test
    public void testBatchJobSendEmail() throws Exception {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setJobName(RecapConstants.PURGE_EXCEPTION_REQUESTS);
        emailPayLoad.setStartDate(new Date());
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/batchJobEmailService/batchJobEmail")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(emailPayLoad)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
    }
}
