package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.executors.MatchingBibItemIndexExecutorService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmControllerUT.class);

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController = new MatchingAlgorithmController();

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

    @Mock
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Mock
    MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService;

    @Mock
    MatchingBibInfoDetailService matchingBibInfoDetailService;

    private Integer batchSize = 10000;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(matchingAlgoController.getLogger()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenReturn(String.valueOf(batchSize));
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception {
        Map<String, Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount", 1);
        matchingAlgoMap.put("culMatchingCount", 2);
        matchingAlgoMap.put("nyplMatchingCount", 3);
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenReturn(matchingAlgorithmHelperService);
        Mockito.when(matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateMatchingBibEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmFull()).thenCallRealMethod();
        String response = matchingAlgoController.matchingAlgorithmFull();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void matchingAlgorithmOnlyReports() throws Exception {
        Map<String, Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount", 1);
        matchingAlgoMap.put("culMatchingCount", 2);
        matchingAlgoMap.put("nyplMatchingCount", 3);
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenReturn(matchingAlgorithmHelperService);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmOnlyReports()).thenCallRealMethod();
        String response = matchingAlgoController.matchingAlgorithmOnlyReports();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateMonographCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMonographs(batchSize);
        Mockito.when(matchingAlgoController.updateMonographCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMonographCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateSerialCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForSerials(batchSize);
        Mockito.when(matchingAlgoController.updateSerialCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateSerialCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateMvmCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMVMs(batchSize);
        Mockito.when(matchingAlgoController.updateMvmCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMvmCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateCGDInSolr() throws Exception {
        Date matchingAlgoDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String matchingAlgoDateString = sdf.format(matchingAlgoDate);
        Date updatedDate = new Date();
        try {
            updatedDate = sdf.parse(matchingAlgoDateString);
        } catch (ParseException e) {
            logger.error("Exception while parsing Date : " + e.getMessage());
        }
        Mockito.when(matchingAlgoController.getMatchingBibItemIndexExecutorService()).thenReturn(matchingBibItemIndexExecutorService);
        Mockito.when(matchingBibItemIndexExecutorService.indexingForMatchingAlgorithm(RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, updatedDate)).thenReturn(1);
        Mockito.when(matchingAlgoController.updateCGDInSolr(matchingAlgoDateString)).thenCallRealMethod();
        String response = matchingAlgoController.updateCGDInSolr(matchingAlgoDateString);
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void populateDataForDataDump() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingBibInfoDetailService()).thenReturn(matchingBibInfoDetailService);
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo()).thenReturn(RecapConstants.SUCCESS);
        Mockito.when(matchingAlgoController.populateDataForDataDump()).thenCallRealMethod();
        String response = matchingAlgoController.populateDataForDataDump();
        assertTrue(response.contains(RecapConstants.SUCCESS));
    }

    @Test
    public void itemCountForSerials() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).getItemsCountForSerialsMatching(batchSize);
        Mockito.when(matchingAlgoController.itemCountForSerials()).thenCallRealMethod();
        String response = matchingAlgoController.itemCountForSerials();
        assertTrue(response.contains("Items Count"));
    }

    @Test
    public void checkGetterServices() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingBibInfoDetailService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingBibItemIndexExecutorService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenCallRealMethod();
        assertNotEquals(matchingAlgorithmUpdateCGDService, matchingAlgoController.getMatchingAlgorithmUpdateCGDService());
        assertNotEquals(matchingAlgorithmHelperService, matchingAlgoController.getMatchingAlgorithmHelperService());
        assertNotEquals(matchingBibInfoDetailService, matchingAlgoController.getMatchingBibInfoDetailService());
        assertNotEquals(String.valueOf(batchSize), matchingAlgoController.getMatchingAlgoBatchSize());
        assertNotEquals(matchingBibItemIndexExecutorService, matchingAlgoController.getMatchingBibItemIndexExecutorService());
        assertNotEquals(reportGenerator, matchingAlgoController.getReportGenerator());
    }
}