package org.recap.controller;

import org.junit.Test;
import org.recap.RecapConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rajeshbabuk on 3/1/17.
 */
public class UpdateCgdRestControllerUT extends BaseControllerUT {

    @Test
    public void updateCgdForItem() throws Exception {
        Integer itemId = 1;
        String newCollectionGroupDesignation = "Private";
        String cgdChangeNotes = "Notes";
        MvcResult savedResult = this.mockMvc.perform(get("/updateCgdService/updateCgd")
                .headers(getHttpHeaders())
                .param("itemId", String.valueOf(itemId))
                .param("newCollectionGroupDesignation", newCollectionGroupDesignation)
                .param("cgdChangeNotes", cgdChangeNotes))
                .andExpect(status().isOk())
                .andReturn();

        String statusResponse = savedResult.getResponse().getContentAsString();
        assertNotNull(statusResponse);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
        return headers;
    }
}
