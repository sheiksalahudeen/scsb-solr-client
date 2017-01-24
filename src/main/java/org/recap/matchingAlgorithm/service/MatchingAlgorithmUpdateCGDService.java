package org.recap.matchingAlgorithm.service;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.executors.MatchingAlgorithmCGDCallable;
import org.recap.matchingAlgorithm.MatchingCounter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.*;
import org.recap.util.BibJSONUtil;
import org.recap.util.MatchingAlgorithmUtil;
import org.recap.util.UpdateCgdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by angelind on 11/1/17.
 */
@Component
public class MatchingAlgorithmUpdateCGDService {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUpdateCGDService.class);

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    ReportDataDetailsRepository reportDataDetailsRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    JmxHelper jmxHelper;

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    private ExecutorService executorService;
    private Map collectionGroupMap;
    private Map institutionMap;

    public void updateCGDProcessForMonographs(Integer batchSize) throws IOException, SolrServerException {
        logger.info("Start");

        MatchingCounter.reset();

        MatchingCounter.setPulSharedCount(matchingAlgorithmUtil.getCGDCountBasedOnInst(RecapConstants.PRINCETON));
        MatchingCounter.setCulSharedCount(matchingAlgorithmUtil.getCGDCountBasedOnInst(RecapConstants.COLUMBIA));
        MatchingCounter.setNyplSharedCount(matchingAlgorithmUtil.getCGDCountBasedOnInst(RecapConstants.NYPL));

        logger.info("PUL Initial Counter Value: " + MatchingCounter.getPulSharedCount());
        logger.info("CUL Initial Counter Value: " + MatchingCounter.getCulSharedCount());
        logger.info("NYPL Initial Counter Value: " + MatchingCounter.getNyplSharedCount());

        ExecutorService executorService = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        long countOfRecordNum = reportDataDetailsRepository.getCountOfRecordNumForMatchingMonograph(RecapConstants.BIB_ID);
        logger.info("Total Records : " + countOfRecordNum);
        int totalPagesCount = (int) Math.ceil(countOfRecordNum / batchSize);
        logger.info("Total Pages : " + totalPagesCount);
        for(int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new MatchingAlgorithmCGDCallable(reportDataDetailsRepository, bibliographicDetailsRepository, pageNum, batchSize, producerTemplate,
                    getCollectionGroupMap(), getInstitutionEntityMap(), itemChangeLogDetailsRepository);
            callables.add(callable);
        }
        Map<String, List<Integer>> unProcessedRecordNumberMap = executeCallables(executorService, callables);

        List<Integer> nonMonographRecordNums = unProcessedRecordNumberMap.get("NonMonographRecordNums");
        List<Integer> exceptionRecordNums = unProcessedRecordNumberMap.get("ExceptionRecordNums");

        matchingAlgorithmUtil.updateMonographicSetRecords(nonMonographRecordNums, batchSize);

        matchingAlgorithmUtil.updateExceptionRecords(exceptionRecordNums, batchSize);

        matchingAlgorithmUtil.saveCGDUpdatedSummaryReport();

        logger.info("PUL Final Counter Value: " + MatchingCounter.getPulSharedCount());
        logger.info("CUL Final Counter Value: " + MatchingCounter.getCulSharedCount());
        logger.info("NYPL Final Counter Value: " + MatchingCounter.getNyplSharedCount());

        DestinationViewMBean updateItemsQ = jmxHelper.getBeanForQueueName("updateItemsQ");

        while (updateItemsQ.getQueueSize() != 0) {
            //Waiting for the updateItemQ messages finish processing
        }

        executorService.shutdown();
    }

    public void updateCGDForItemsInSolr(Date lastUpdatedDate, Integer batchSize) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastUpdatedDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date lastUpdatedFromDate = cal.getTime();
        cal.setTime(lastUpdatedDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date lastUpdatedToDate = cal.getTime();
        Page<ItemEntity> itemEntityPage = itemDetailsRepository.findByLastUpdatedDate(new PageRequest(0, batchSize), lastUpdatedFromDate, lastUpdatedToDate);
        int totalPages = itemEntityPage.getTotalPages();
        logger.info("Total Elements : " + itemEntityPage.getTotalElements());
        logger.info("Total Pages : " + totalPages);
        List<ItemEntity> itemEntities = itemEntityPage.getContent();
        List<SolrInputDocument> bibSolrInputDocuments = prepareSolrInputDocument(itemEntities);
        saveSolrInputDocuments(bibSolrInputDocuments);
        for(int i=1; i < totalPages; i++) {
            itemEntityPage = itemDetailsRepository.findByLastUpdatedDate(new PageRequest(i, batchSize), lastUpdatedFromDate, lastUpdatedDate);
            itemEntities = itemEntityPage.getContent();
            bibSolrInputDocuments = prepareSolrInputDocument(itemEntities);
            saveSolrInputDocuments(bibSolrInputDocuments);
        }
    }

    private void saveSolrInputDocuments(List<SolrInputDocument> bibSolrInputDocuments) {
        if (!org.springframework.util.CollectionUtils.isEmpty(bibSolrInputDocuments)) {
            solrTemplate.saveDocuments(bibSolrInputDocuments);
            solrTemplate.commit();
        }
    }

    private List<SolrInputDocument> prepareSolrInputDocument(List<ItemEntity> itemEntities) {
        List<SolrInputDocument> bibSolrInputDocuments = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(itemEntities)) {
            for(ItemEntity itemEntity : itemEntities) {
                if (itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                    for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                        try {
                            BibJSONUtil bibJSONUtil = new BibJSONUtil();
                            SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate,
                                    bibliographicDetailsRepository, holdingsDetailsRepository);
                            bibSolrInputDocuments.add(bibSolrInputDocument);
                        } catch (Exception ex) {
                            logger.error("Exception in Callable : " + ex.getMessage());
                        }
                    }
                }
            }
        }
        return bibSolrInputDocuments;
    }

    private Map<String, List<Integer>> executeCallables(ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Integer> nonMonographRecordNumbers = new ArrayList<>();
        List<Integer> exceptionRecordNumbers = new ArrayList<>();
        Map<String, List<Integer>> unProcessedRecordNumberMap = new HashMap<>();
        List<Future<Integer>> futures = getFutures(executorService, callables);

        for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            try {
                Map<String, List<Integer>> recordNumberMap = (Map<String, List<Integer>>) future.get();
                if(recordNumberMap != null) {
                    if(CollectionUtils.isNotEmpty(recordNumberMap.get("NonMonographRecordNums"))) {
                        nonMonographRecordNumbers.addAll(recordNumberMap.get("NonMonographRecordNums"));
                    }
                    if(CollectionUtils.isNotEmpty(recordNumberMap.get("ExceptionRecordNums"))) {
                        exceptionRecordNumbers.addAll(recordNumberMap.get("ExceptionRecordNums"));
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        unProcessedRecordNumberMap.put("NonMonographRecordNums", nonMonographRecordNumbers);
        unProcessedRecordNumberMap.put("ExceptionRecordNums", exceptionRecordNumbers);
        return unProcessedRecordNumberMap;
    }

    private List<Future<Integer>> getFutures(ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Future<Integer>> futures = null;
        try {
            futures = executorService.invokeAll(callables);
            futures
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        } catch (ExecutionException e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return futures;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }

    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
            for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                CollectionGroupEntity collectionGroupEntity = iterator.next();
                collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
            }
        }
        return collectionGroupMap;
    }

    public Map getInstitutionEntityMap() {
        if (null == institutionMap) {
            institutionMap = new HashMap();
            Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
            for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                InstitutionEntity institutionEntity = iterator.next();
                institutionMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionId());
            }
        }
        return institutionMap;
    }
}
