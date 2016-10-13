package org.recap.matchingAlgorithm;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.executors.MatchingAlgorithmCallable;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by angelind on 12/7/16.
 */
@Component
public class MatchingAlgorithmSaveReport {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmSaveReport.class);

    @Value("${solr.server.protocol}")
    String solrServerProtocol;

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrParentCore;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Autowired
    public BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    public ItemDetailsRepository itemDetailsRepository;

    private ExecutorService executorService;

    public void saveMatchingAlgorithmReports() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> duplicateOCLCMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_OCLC);
        Map<String, Integer> duplicateISBNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_ISBN);
        Map<String, Integer> duplicateISSNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_ISSN);
        Map<String, Integer> duplicateLCCNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_LCCN);
        Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
        Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
        Map<String, Integer> oclcCountMap = new HashMap<>();
        Map<String, Integer> isbnCountMap = new HashMap<>();
        Map<String, Integer> issnCountMap = new HashMap<>();
        Map<String, Integer> lccnCountMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(duplicateOCLCMap)) {
            oclcCountMap = getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        if(!CollectionUtils.isEmpty(duplicateISBNMap)) {
            isbnCountMap = getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        if(!CollectionUtils.isEmpty(duplicateISSNMap)) {
            issnCountMap = getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        if(!CollectionUtils.isEmpty(duplicateLCCNMap)) {
            lccnCountMap = getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        stopWatch.stop();
        logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        matchingAlgorithmHelperService.saveSummaryReportEntity(oclcCountMap, isbnCountMap, issnCountMap, lccnCountMap);
        matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
        matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
        stopWatch.stop();
        logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
    }

    public void saveMatchingAlgorithmReportForOclc() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> duplicateOCLCMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_OCLC);
        Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
        Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
        Map<String, Integer> oclcCountMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(duplicateOCLCMap)) {
            oclcCountMap = getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        stopWatch.stop();
        logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        long bibCount = bibliographicDetailsRepository.count();
        long itemCount = itemDetailsRepository.count();
        ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(oclcCountMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_OCLC_FILE_NAME, bibCount, itemCount);
        matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
        matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
        matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
        stopWatch.stop();
        logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
    }

    public void saveMatchingAlgorithmReportForIsbn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> duplicateISBNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_ISBN);
        Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
        Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
        Map<String, Integer> isbnMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(duplicateISBNMap)) {
            isbnMap = getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        stopWatch.stop();
        logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        long bibCount = bibliographicDetailsRepository.count();
        long itemCount = itemDetailsRepository.count();
        ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(isbnMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.SUMMARY_REPORT_ISBN_FILE_NAME, bibCount, itemCount);
        matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
        matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
        matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
        stopWatch.stop();
        logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
    }

    public void saveMatchingAlgorithmReportForIssn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> duplicateISSNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_ISSN);
        Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
        Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
        Map<String, Integer> issnCountMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(duplicateISSNMap)) {
            issnCountMap = getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        stopWatch.stop();
        logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        long bibCount = bibliographicDetailsRepository.count();
        long itemCount = itemDetailsRepository.count();
        ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(issnCountMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.SUMMARY_REPORT_ISSN_FILE_NAME, bibCount, itemCount);
        matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
        matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
        matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
        stopWatch.stop();
        logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
    }

    public void saveMatchingAlgorithmReportForLccn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<String, Integer> duplicateLCCNMap = getMatchesByFacetQuery(RecapConstants.MATCH_POINT_FIELD_LCCN);
        Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
        Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
        Map<String, Integer> lccnCountMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(duplicateLCCNMap)) {
            lccnCountMap = getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
        }
        stopWatch.stop();
        logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
        stopWatch = new StopWatch();
        stopWatch.start();
        long bibCount = bibliographicDetailsRepository.count();
        long itemCount = itemDetailsRepository.count();
        ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(lccnCountMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.SUMMARY_REPORT_LCCN_FILE_NAME, bibCount, itemCount);
        matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
        matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
        matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
        stopWatch.stop();
        logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
    }

    public Map<String, Integer> getMatchingList(Map<String, Integer> matchingFieldList, String fieldName, String matchingFileName, String exceptionReportFileName, Map<Integer, Map<String, ReportEntity>> matchingReportEntityMap, Map<Integer, Map<String, ReportEntity>> exceptionReportEntityMap) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        logger.info("Total Num of Duplicate " + fieldName + " Found : " + matchingFieldList.size());

        Integer itemCount = 0;
        Map<String, Integer> countMap = new HashMap<>();
        Map<Integer, Integer> bibItemCountMap = new HashMap<>();

        ExecutorService executorService = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        for (String fieldValue : matchingFieldList.keySet()) {
            Callable callable = new MatchingAlgorithmCallable(fieldValue, fieldName, matchingFileName, exceptionReportFileName, matchingAlgorithmHelperService, matchingReportEntityMap, exceptionReportEntityMap);
            callables.add(callable);
        }

        List<Future<Integer>> futures = null;
        try {
            futures = executorService.invokeAll(callables);
            futures
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            try {
                Map<String, Object> responseMap = (Map<String, Object>) future.get();
                matchingReportEntityMap.putAll((Map<? extends Integer, ? extends Map<String, ReportEntity>>) responseMap.get(RecapConstants.MATCHING_REPORT_ENTITY_MAP));
                exceptionReportEntityMap.putAll((Map<? extends Integer, ? extends Map<String, ReportEntity>>) responseMap.get(RecapConstants.EXCEPTION_REPORT_ENTITY_MAP));
                bibItemCountMap.putAll((Map<? extends Integer, ? extends Integer>) responseMap.get(RecapConstants.BIB_ITEM_COUNT));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        countMap.put(RecapConstants.BIB_COUNT, bibItemCountMap.size());
        for(Integer bibId : bibItemCountMap.keySet()) {
            itemCount = itemCount + bibItemCountMap.get(bibId);
        }
        countMap.put(RecapConstants.ITEM_COUNT, itemCount);
        stopWatch.stop();
        logger.info("Total Time Taken to Match and Populate " + fieldName + " Report : " + stopWatch.getTotalTimeSeconds());
        return countMap;
    }

    public Map<String, Integer> getMatchesByFacetQuery(String fieldName) throws JSONException {
        Map<String, Integer> MatchingValuesMap = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field="+ fieldName +"&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            MatchingValuesMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, fieldName);
        }
        return MatchingValuesMap;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }
}
