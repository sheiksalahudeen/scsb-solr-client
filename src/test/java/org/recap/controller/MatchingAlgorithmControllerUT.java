package org.recap.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    @Test
    public void matchingAlgorithmFullTest() throws Exception{

        MvcResult savedResult = this.mockMvc.perform(post("/matchingAlgorithm/full"))
                .andReturn();
        String response = savedResult.getResponse().getContentAsString();
        assertTrue(response.contains("Done"));

    }

    @Test
    public void testMatchingAlgorithmBasedOnOCLC() throws Exception{
        MvcResult savedResult = this.mockMvc.perform(post("/matchingAlgorithm/isbn"))
                .andReturn();
        String response = savedResult.getResponse().getContentAsString();
        assertTrue(response.contains("Done"));
    }


    @Test
    public void testMatchingAlgorithmBasedOnISSN() throws Exception{
        MvcResult savedResult = this.mockMvc.perform(post("/matchingAlgorithm/issn"))
                .andReturn();
        String response = savedResult.getResponse().getContentAsString();
        assertTrue(response.contains("Done"));
    }


    @Test
    public void testMatchingAlgorithmBasedOnLCCN() throws Exception{
        MvcResult savedResult = this.mockMvc.perform(post("/matchingAlgorithm/lccn"))
                .andReturn();
        String response = savedResult.getResponse().getContentAsString();
        assertTrue(response.contains("Done"));
    }


}