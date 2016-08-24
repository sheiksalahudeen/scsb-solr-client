package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.matchingAlgorithm.MatchingAlgorithmSaveReport;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController= new MatchingAlgorithmController();

    @Mock
    MatchingAlgorithmSaveReport mockMatchingAlgorithmSaveReport;

    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception{

        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReports();
        String response = matchingAlgorithmController.matchingAlgorithmFull();
        assertTrue(response.contains("Status  : Done"));
    }

   @Test
    public void testMatchingAlgorithmBasedOnOCLC() throws Exception{
       doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForOclc();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnOCLC();
        assertTrue(response.contains("Status  : Done"));assertTrue(response.contains("Status  : Done"));
    }


    @Test
    public void testMatchingAlgorithmBasedOnISSN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForIssn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnISSN();
        assertTrue(response.contains("Status  : Done"));;
    }


    @Test
    public void testMatchingAlgorithmBasedOnLCCN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForLccn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnLCCN();
        assertTrue(response.contains("Status  : Done"));
    }
    @Test
    public void testMatchingAlgorithmBasedOnISBN() throws Exception{
        doNothing().when(mockMatchingAlgorithmSaveReport).saveMatchingAlgorithmReportForIsbn();
        String response = matchingAlgorithmController.matchingAlgorithmBasedOnISBN();
        assertTrue(response.contains("Status  : Done"));
    }
}