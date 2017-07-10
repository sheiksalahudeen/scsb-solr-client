package org.recap.controller;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.util.DateUtil;
import org.recap.util.OngoingMatchingAlgorithmUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by angelind on 13/6/17.
 */
public class OngoingMatchingAlgorithmJobControllerUT extends BaseControllerUT{

    @InjectMocks
    OngoingMatchingAlgorithmJobController ongoingMatchingAlgorithmJobController = new OngoingMatchingAlgorithmJobController();

    @Mock
    OngoingMatchingAlgorithmJobController ongoingMatchingAlgoJobController;

    @Mock
    OngoingMatchingAlgorithmUtil ongoingMatchingAlgorithmUtil;

    @Mock
    MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Autowired
    DateUtil dateUtil;

    @Mock
    QueryResponse queryResponse;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    private String batchSize;

    @Test
    public void startOngoingMatchingAlgorithmJob() throws Exception {
        Date processDate = new Date();
        Date fromDate = dateUtil.getFromDate(processDate);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(processDate);
        SolrDocument solrDocument = new SolrDocument();
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        solrDocumentList.add(solrDocument);
        List<Integer> bibIds = new ArrayList<>();
        Integer rows = Integer.valueOf(batchSize);
        Date date = solrIndexRequest.getCreatedDate();
        solrIndexRequest.setProcessType(RecapConstants.ONGOING_MATCHING_ALGORITHM_JOB);
        Mockito.when(ongoingMatchingAlgoJobController.getOngoingMatchingAlgorithmUtil()).thenReturn(ongoingMatchingAlgorithmUtil);
        Mockito.when(ongoingMatchingAlgoJobController.getDateUtil()).thenReturn(dateUtil);
        Mockito.when(ongoingMatchingAlgoJobController.getBatchSize()).thenReturn(batchSize);
        Mockito.when(ongoingMatchingAlgoJobController.getLogger()).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgorithmUtil.getFormattedDateString(fromDate)).thenReturn(processDate.toString());
        Mockito.when(ongoingMatchingAlgorithmUtil.fetchUpdatedRecordsAndStartProcess(dateUtil.getFromDate(date), rows)).thenReturn("Success");
        Mockito.when(ongoingMatchingAlgorithmUtil.fetchDataForOngoingMatchingBasedOnDate(processDate.toString(), Integer.valueOf(100), Integer.valueOf(0))).thenReturn(queryResponse);
        Mockito.when(queryResponse.getResults()).thenReturn(solrDocumentList);
        Mockito.when(ongoingMatchingAlgorithmUtil.processMatchingForBib(solrDocument, bibIds)).thenReturn(RecapConstants.SUCCESS);
        Mockito.when(ongoingMatchingAlgoJobController.startMatchingAlgorithmJob(solrIndexRequest)).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgorithmUtil.processOngoingMatchingAlgorithm(solrDocumentList, new ArrayList<>())).thenCallRealMethod();
        String status = ongoingMatchingAlgoJobController.startMatchingAlgorithmJob(solrIndexRequest);
        assertTrue(status.contains(RecapConstants.SUCCESS));
        Mockito.when(ongoingMatchingAlgoJobController.getOngoingMatchingAlgorithmUtil()).thenCallRealMethod();
        assertNotEquals(ongoingMatchingAlgorithmUtil, ongoingMatchingAlgoJobController.getOngoingMatchingAlgorithmUtil());
        Mockito.when(ongoingMatchingAlgoJobController.getDateUtil()).thenCallRealMethod();
        assertNotEquals(dateUtil, ongoingMatchingAlgoJobController.getDateUtil());
    }

    @Test
    public void startJobToPopulateDataDumpMatchingBibs() throws Exception {
        Date processDate = new Date();
        Date fromDate = dateUtil.getFromDate(processDate);
        Date toDate = dateUtil.getToDate(processDate);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setCreatedDate(processDate);
        solrIndexRequest.setProcessType(RecapConstants.POPULATE_DATA_FOR_DATA_DUMP_JOB);
        Mockito.when(ongoingMatchingAlgoJobController.getMatchingBibInfoDetailService()).thenReturn(matchingBibInfoDetailService);
        Mockito.when(ongoingMatchingAlgoJobController.getDateUtil()).thenReturn(dateUtil);
        Mockito.when(ongoingMatchingAlgoJobController.getLogger()).thenCallRealMethod();
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo(fromDate, toDate)).thenReturn(RecapConstants.SUCCESS);
        Mockito.when(ongoingMatchingAlgoJobController.getBatchSize()).thenReturn(batchSize);
        Mockito.when(ongoingMatchingAlgoJobController.startMatchingAlgorithmJob(solrIndexRequest)).thenCallRealMethod();
        String status = ongoingMatchingAlgoJobController.startMatchingAlgorithmJob(solrIndexRequest);
        assertTrue(status.contains(RecapConstants.SUCCESS));
        Mockito.when(ongoingMatchingAlgoJobController.getMatchingBibInfoDetailService()).thenCallRealMethod();
        assertNotEquals(matchingBibInfoDetailService, ongoingMatchingAlgoJobController.getMatchingBibInfoDetailService());
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(ongoingMatchingAlgoJobController.getMatchingBibInfoDetailService()).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgoJobController.getOngoingMatchingAlgorithmUtil()).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgoJobController.getBatchSize()).thenCallRealMethod();
        Mockito.when(ongoingMatchingAlgoJobController.getDateUtil()).thenCallRealMethod();
        assertNotEquals(matchingBibInfoDetailService,ongoingMatchingAlgoJobController.getMatchingBibInfoDetailService());
        assertNotEquals(ongoingMatchingAlgorithmUtil,ongoingMatchingAlgoJobController.getOngoingMatchingAlgorithmUtil());
        assertNotEquals(batchSize,ongoingMatchingAlgoJobController.getBatchSize());
        assertNotEquals(dateUtil,ongoingMatchingAlgoJobController.getDateUtil());
    }

}