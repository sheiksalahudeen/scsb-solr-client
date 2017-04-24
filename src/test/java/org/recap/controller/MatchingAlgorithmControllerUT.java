package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT{

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmController.class);

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController= new MatchingAlgorithmController();

    @Mock
    MatchingAlgorithmController matchingAlgoController;

    @Mock
    ReportGenerator reportGenerator;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Mock
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    private Integer batchSize = 10000;



    @Before
    public void setup()throws Exception{
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception{
        Map<String,Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount",1);
        matchingAlgoMap.put("culMatchingCount",2);
        matchingAlgoMap.put("nyplMatchingCount",3);
        Mockito.when(matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateMatchingBibEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmFull()).thenReturn("Status  : Done");
        String response = matchingAlgoController.matchingAlgorithmFull();
        assertTrue(response.contains("Status  : Done"));
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}