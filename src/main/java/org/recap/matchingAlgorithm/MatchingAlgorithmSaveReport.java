package org.recap.matchingAlgorithm;

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
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.field=ISBN&facet.field=ISSN&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
            Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_OCLC);
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_ISBN);
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_ISSN);
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, RecapConstants.MATCH_POINT_FIELD_LCCN);
            Map<String, Integer> oclcCountMap = getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            Map<String, Integer> isbnCountMap = getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            Map<String, Integer> issnCountMap = getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            Map<String, Integer> lccnCountMap = getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_FULL_FILE_NAME, RecapConstants.EXCEPTION_REPORT_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
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
    }

    public void saveMatchingAlgorithmReportForOclc() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=OCLCNumber&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
            Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
            Map<String, Integer> duplicateISBNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "OCLCNumber");
            Map<String, Integer> oclcCountMap = getMatchingList(duplicateISBNMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.MATCHING_ALGO_OCLC_FILE_NAME, RecapConstants.EXCEPTION_REPORT_OCLC_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            stopWatch.stop();
            logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
            stopWatch = new StopWatch();
            stopWatch.start();
            long bibCount = bibliographicDetailsRepository.countByIsDeletedFalse();
            long itemCount = itemDetailsRepository.countByIsDeletedFalse();
            ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(oclcCountMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_OCLC_FILE_NAME, bibCount, itemCount);
            matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
            matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
            matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
            stopWatch.stop();
            logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
        }
    }

    public void saveMatchingAlgorithmReportForIsbn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISBN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
            Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
            Map<String, Integer> duplicateISSNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISBN");
            Map<String, Integer> isbnMap = getMatchingList(duplicateISSNMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.MATCHING_ALGO_ISBN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISBN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            stopWatch.stop();
            logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
            stopWatch = new StopWatch();
            stopWatch.start();
            long bibCount = bibliographicDetailsRepository.countByIsDeletedFalse();
            long itemCount = itemDetailsRepository.countByIsDeletedFalse();
            matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
            matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
            ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(isbnMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_ISBN_FILE_NAME, bibCount, itemCount);
            matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
            stopWatch.stop();
            logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
        }
    }

    public void saveMatchingAlgorithmReportForIssn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=ISSN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap();
            Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
            Map<String, Integer> duplicateLCCNMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "ISSN");
            Map<String, Integer> issnMap = getMatchingList(duplicateLCCNMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.MATCHING_ALGO_ISSN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_ISSN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            stopWatch.stop();
            logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
            stopWatch = new StopWatch();
            stopWatch.start();
            long bibCount = bibliographicDetailsRepository.countByIsDeletedFalse();
            long itemCount = itemDetailsRepository.countByIsDeletedFalse();
            matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
            matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
            ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(issnMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_ISSN_FILE_NAME, bibCount, itemCount);
            matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
            stopWatch.stop();
            logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
        }
    }

    public void saveMatchingAlgorithmReportForLccn() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RestTemplate restTemplate = new RestTemplate();
        String url = solrServerProtocol + solrUrl + "/" + solrParentCore + "/" + "query?q=DocType:Bib&wt=json&facet=true&facet.field=LCCN&facet.mincount=2&facet.limit=-1";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if(responseEntity.getStatusCode().getReasonPhrase().equalsIgnoreCase("OK")) {
            JSONObject responseJsonObject = new JSONObject(responseEntity.getBody());
            JSONObject facetJsonObject = responseJsonObject.getJSONObject("facet_counts");
            JSONObject resultJsonObject = facetJsonObject.getJSONObject("facet_fields");
            Map<Integer, Map<String,ReportEntity>> matchingReportEntityMap = new HashMap<>();
            Map<Integer, Map<String,ReportEntity>> exceptionReportEntityMap = new HashMap<>();
            Map<String, Integer> duplicateOCLCMap = matchingAlgorithmHelperService.getBibListUsingFacet(resultJsonObject, "LCCN");
            Map<String, Integer> lccnMap = getMatchingList(duplicateOCLCMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.MATCHING_ALGO_LCCN_FILE_NAME, RecapConstants.EXCEPTION_REPORT_LCCN_FILE_NAME, matchingReportEntityMap, exceptionReportEntityMap);
            stopWatch.stop();
            logger.info("Total Time Taken to match : " + stopWatch.getTotalTimeSeconds());
            stopWatch = new StopWatch();
            stopWatch.start();
            long bibCount = bibliographicDetailsRepository.countByIsDeletedFalse();
            long itemCount = itemDetailsRepository.countByIsDeletedFalse();
            matchingAlgorithmHelperService.saveMatchingReportEntity(matchingReportEntityMap);
            matchingAlgorithmHelperService.saveExceptionReportEntity(exceptionReportEntityMap);
            ReportEntity summaryReportEntity = matchingAlgorithmHelperService.getSummaryReportEntity(lccnMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_LCCN_FILE_NAME, bibCount, itemCount);
            matchingAlgorithmHelperService.saveReportEntities(Arrays.asList(summaryReportEntity));
            stopWatch.stop();
            logger.info("Total Time Taken to save Reports : " + stopWatch.getTotalTimeSeconds());
        }
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

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }
}
