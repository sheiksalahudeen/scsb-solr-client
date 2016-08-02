package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.recap.MatchingAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    @Autowired
    MatchingAlgorithmController mockMatchingAlgorithmController;

    @Autowired
    MatchingAlgorithm matchingAlgorithm;

    @Before
    public void setup()throws Exception{
        mockMatchingAlgorithmController = mock(MatchingAlgorithmController.class);
        matchingAlgorithm = mock(MatchingAlgorithm.class);
        StringBuilder responseString = new StringBuilder();
        responseString.append("Status  : Done");
        responseString.append("Total Time Taken  : 8.568");
        when(mockMatchingAlgorithmController.matchingAlgorithmFull()).thenReturn(responseString.toString());
        when(mockMatchingAlgorithmController.matchingAlgorithmBasedOnISBN()).thenReturn(responseString.toString());
        when(mockMatchingAlgorithmController.matchingAlgorithmBasedOnLCCN()).thenReturn(responseString.toString());
        when(mockMatchingAlgorithmController.matchingAlgorithmBasedOnISSN()).thenReturn(responseString.toString());
        when(mockMatchingAlgorithmController.matchingAlgorithmBasedOnOCLC()).thenReturn(responseString.toString());
    }
    @Test
    public void matchingAlgorithmFullTest() throws Exception{
        String response = mockMatchingAlgorithmController.matchingAlgorithmFull();
        assertTrue(response.contains("Status  : Done"));
    }

    @Test
    public void testMatchingAlgorithmBasedOnOCLC() throws Exception{
        String response = mockMatchingAlgorithmController.matchingAlgorithmBasedOnOCLC();
        assertTrue(response.contains("Status  : Done"));
    }


    @Test
    public void testMatchingAlgorithmBasedOnISSN() throws Exception{
        String response = mockMatchingAlgorithmController.matchingAlgorithmBasedOnISSN();
        assertTrue(response.contains("Status  : Done"));;
    }


    @Test
    public void testMatchingAlgorithmBasedOnLCCN() throws Exception{
        String response = mockMatchingAlgorithmController.matchingAlgorithmBasedOnLCCN();
        assertTrue(response.contains("Status  : Done"));
    }

    @Test
    public void testMatchingAlgorithmBasedOnISBN() throws Exception{
        String response = mockMatchingAlgorithmController.matchingAlgorithmBasedOnISBN();
        assertTrue(response.contains("Status  : Done"));
    }
}