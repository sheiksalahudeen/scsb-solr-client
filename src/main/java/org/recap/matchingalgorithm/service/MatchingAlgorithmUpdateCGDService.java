package org.recap.matchingalgorithm.service;

import com.google.common.collect.Lists;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.executors.MatchingAlgorithmMVMsCGDCallable;
import org.recap.executors.MatchingAlgorithmMonographCGDCallable;
import org.recap.executors.MatchingAlgorithmSerialsCGDCallable;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.util.MatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by angelind on 11/1/17.
 */
@Component
public class MatchingAlgorithmUpdateCGDService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUpdateCGDService.class);

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ReportDataDetailsRepository reportDataDetailsRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private JmxHelper jmxHelper;

    @Autowired
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    /**
     * Gets report data details repository.
     *
     * @return the report data details repository
     */
    public ReportDataDetailsRepository getReportDataDetailsRepository() {
        return reportDataDetailsRepository;
    }

    public MatchingAlgorithmUtil getMatchingAlgorithmUtil() {
        return matchingAlgorithmUtil;
    }

    public BibliographicDetailsRepository getBibliographicDetailsRepository() {
        return bibliographicDetailsRepository;
    }

    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public CollectionGroupDetailsRepository getCollectionGroupDetailsRepository() {
        return collectionGroupDetailsRepository;
    }

    public InstitutionDetailsRepository getInstitutionDetailsRepository() {
        return institutionDetailsRepository;
    }

    public ItemChangeLogDetailsRepository getItemChangeLogDetailsRepository() {
        return itemChangeLogDetailsRepository;
    }

    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    public JmxHelper getJmxHelper() {
        return jmxHelper;
    }

    public static Logger getLogger() {
        return logger;
    }

    private ExecutorService executorService;
    private Map collectionGroupMap;
    private Map institutionMap;

    /**
     * This method is used to update cgd process for monographs.
     *
     * @param batchSize the batch size
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public void updateCGDProcessForMonographs(Integer batchSize) throws IOException, SolrServerException {
        getLogger().info("Start CGD Process For Monographs.");

        getMatchingAlgorithmUtil().populateMatchingCounter();
        ExecutorService executor = getExecutorService(50);

        processCallablesForMonographs(batchSize, executor, false);

        DestinationViewMBean updateItemsQ = getJmxHelper().getBeanForQueueName("updateItemsQ");

        if(updateItemsQ != null) {
            while (updateItemsQ.getQueueSize() != 0) {
                //Waiting for the updateItemQ messages finish processing
            }
        }

        processCallablesForMonographs(batchSize, executor, true);

        getMatchingAlgorithmUtil().saveCGDUpdatedSummaryReport(RecapConstants.MATCHING_SUMMARY_MONOGRAPH);

        logger.info("PUL Final Counter Value:{} " , MatchingCounter.getPulSharedCount());
        logger.info("CUL Final Counter Value: {}" , MatchingCounter.getCulSharedCount());
        logger.info("NYPL Final Counter Value: {}" , MatchingCounter.getNyplSharedCount());

        if(updateItemsQ != null){
            while (updateItemsQ.getQueueSize() != 0) {
                //Waiting for the updateItemQ messages finish processing
            }
        }
        executor.shutdown();
    }

    private void processCallablesForMonographs(Integer batchSize, ExecutorService executor, boolean isPendingMatch) {
        List<Callable<Integer>> callables = new ArrayList<>();
        long countOfRecordNum;
        if(isPendingMatch) {
            countOfRecordNum = getReportDataDetailsRepository().getCountOfRecordNumForMatchingPendingMonograph(RecapConstants.BIB_ID);
        } else {
            countOfRecordNum = getReportDataDetailsRepository().getCountOfRecordNumForMatchingMonograph(RecapConstants.BIB_ID);
        }
        logger.info("Total Records : {}", countOfRecordNum);
        int totalPagesCount = (int) (countOfRecordNum / batchSize);
        logger.info("Total Pages : {}" , totalPagesCount);
        for(int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new MatchingAlgorithmMonographCGDCallable(getReportDataDetailsRepository(), getBibliographicDetailsRepository(), pageNum, batchSize, getProducerTemplate(),
                    getCollectionGroupMap(), getInstitutionEntityMap(), getItemChangeLogDetailsRepository(), getCollectionGroupDetailsRepository(), getItemDetailsRepository(),isPendingMatch);
            callables.add(callable);
        }
        Map<String, List<Integer>> unProcessedRecordNumberMap = executeCallables(executor, callables);

        List<Integer> nonMonographRecordNums = unProcessedRecordNumberMap.get(RecapConstants.NON_MONOGRAPH_RECORD_NUMS);
        List<Integer> exceptionRecordNums = unProcessedRecordNumberMap.get(RecapConstants.EXCEPTION_RECORD_NUMS);

        getMatchingAlgorithmUtil().updateMonographicSetRecords(nonMonographRecordNums, batchSize);

        getMatchingAlgorithmUtil().updateExceptionRecords(exceptionRecordNums, batchSize);
    }

    /**
     * Update cgd process for serials.
     *
     * @param batchSize the batch size
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public void updateCGDProcessForSerials(Integer batchSize) throws IOException, SolrServerException {
        logger.info("Start CGD Process For Serials.");

        getMatchingAlgorithmUtil().populateMatchingCounter();

        ExecutorService executor = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        long countOfRecordNum = getReportDataDetailsRepository().getCountOfRecordNumForMatchingSerials(RecapConstants.BIB_ID);
        logger.info("Total Records : {}", countOfRecordNum);
        int totalPagesCount = (int) (countOfRecordNum / batchSize);
        logger.info("Total Pages : {}" , totalPagesCount);
        for(int pageNum=0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new MatchingAlgorithmSerialsCGDCallable(getReportDataDetailsRepository(), getBibliographicDetailsRepository(), pageNum, batchSize, getProducerTemplate(), getCollectionGroupMap(),
                    getInstitutionEntityMap(), getItemChangeLogDetailsRepository(), getCollectionGroupDetailsRepository(), getItemDetailsRepository());
            callables.add(callable);
        }
        getFutures(executor, callables);

        getMatchingAlgorithmUtil().saveCGDUpdatedSummaryReport(RecapConstants.MATCHING_SUMMARY_SERIAL);

        logger.info("PUL Final Counter Value:{} " , MatchingCounter.getPulSharedCount());
        logger.info("CUL Final Counter Value: {}" , MatchingCounter.getCulSharedCount());
        logger.info("NYPL Final Counter Value: {}" , MatchingCounter.getNyplSharedCount());

        DestinationViewMBean updateItemsQ = getJmxHelper().getBeanForQueueName("updateItemsQ");
        if(updateItemsQ != null){
            while (updateItemsQ.getQueueSize() != 0) {
                //Waiting for the updateItemQ messages finish processing
            }
        }

        executor.shutdown();
    }

    /**
     * Update cgd process for mv ms.
     *
     * @param batchSize the batch size
     * @throws IOException         the io exception
     * @throws SolrServerException the solr server exception
     */
    public void updateCGDProcessForMVMs(Integer batchSize) throws IOException, SolrServerException {
        logger.info("Start CGD Process For MVMs.");

        getMatchingAlgorithmUtil().populateMatchingCounter();

        ExecutorService executor = getExecutorService(50);
        List<Callable<Integer>> callables = new ArrayList<>();
        long countOfRecordNum = getReportDataDetailsRepository().getCountOfRecordNumForMatchingMVMs(RecapConstants.BIB_ID);
        logger.info("Total Records : {}", countOfRecordNum);
        int totalPagesCount = (int) (countOfRecordNum / batchSize);
        logger.info("Total Pages : {}" , totalPagesCount);
        for(int pageNum=0; pageNum < totalPagesCount + 1; pageNum++) {
            Callable callable = new MatchingAlgorithmMVMsCGDCallable(getReportDataDetailsRepository(), getBibliographicDetailsRepository(), pageNum, batchSize, getProducerTemplate(), getCollectionGroupMap(),
                    getInstitutionEntityMap(), getItemChangeLogDetailsRepository(), getCollectionGroupDetailsRepository(), getItemDetailsRepository());
            callables.add(callable);
        }
        getFutures(executor, callables);

        getMatchingAlgorithmUtil().saveCGDUpdatedSummaryReport(RecapConstants.MATCHING_SUMMARY_MVM);

        logger.info("PUL Final Counter Value:{} " , MatchingCounter.getPulSharedCount());
        logger.info("CUL Final Counter Value: {}" , MatchingCounter.getCulSharedCount());
        logger.info("NYPL Final Counter Value: {}" , MatchingCounter.getNyplSharedCount());

        DestinationViewMBean updateItemsQ = getJmxHelper().getBeanForQueueName("updateItemsQ");

        if(updateItemsQ != null) {
            while (updateItemsQ.getQueueSize() != 0) {
                //Waiting for the updateItemQ messages finish processing
            }
        }

        executor.shutdown();
    }

    private Map<String, List<Integer>> executeCallables(ExecutorService executorService, List<Callable<Integer>> callables) {
        List<Integer> nonMonographRecordNumbers = new ArrayList<>();
        List<Integer> exceptionRecordNumbers = new ArrayList<>();
        Map<String, List<Integer>> unProcessedRecordNumberMap = new HashMap<>();
        List<Future<Integer>> futures = getFutures(executorService, callables);

        if(futures != null) {
            for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
                Future future = iterator.next();
                fetchAndPopulateRecordNumbers(nonMonographRecordNumbers, exceptionRecordNumbers, future);
            }
        }
        unProcessedRecordNumberMap.put(RecapConstants.NON_MONOGRAPH_RECORD_NUMS, nonMonographRecordNumbers);
        unProcessedRecordNumberMap.put(RecapConstants.EXCEPTION_RECORD_NUMS, exceptionRecordNumbers);
        return unProcessedRecordNumberMap;
    }

    private void fetchAndPopulateRecordNumbers(List<Integer> nonMonographRecordNumbers, List<Integer> exceptionRecordNumbers, Future future) {
        try {
            Map<String, List<Integer>> recordNumberMap = (Map<String, List<Integer>>) future.get();
            if(recordNumberMap != null) {
                if(CollectionUtils.isNotEmpty(recordNumberMap.get(RecapConstants.NON_MONOGRAPH_RECORD_NUMS))) {
                    nonMonographRecordNumbers.addAll(recordNumberMap.get(RecapConstants.NON_MONOGRAPH_RECORD_NUMS));
                }
                if(CollectionUtils.isNotEmpty(recordNumberMap.get(RecapConstants.EXCEPTION_RECORD_NUMS))) {
                    exceptionRecordNumbers.addAll(recordNumberMap.get(RecapConstants.EXCEPTION_RECORD_NUMS));
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
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
                        } catch (InterruptedException | ExecutionException e) {
                            throw new IllegalStateException(e);
                        }
                    });
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        return futures;
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }

    /**
     * This method gets items count for serials matching.
     *
     * @param batchSize the batch size
     */
    public void getItemsCountForSerialsMatching(Integer batchSize) {
        long countOfRecordNum = getReportDataDetailsRepository().getCountOfRecordNumForMatchingSerials(RecapConstants.BIB_ID);
        logger.info("Total Records : {}", countOfRecordNum);
        int totalPagesCount = (int) (countOfRecordNum / batchSize);
        logger.info("Total Pages : {}" , totalPagesCount);
        for(int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            long from = pageNum * Long.valueOf(batchSize);
            List<ReportDataEntity> reportDataEntities =  getReportDataDetailsRepository().getReportDataEntityForMatchingSerials(RecapConstants.BIB_ID, from, batchSize);
            List<List<ReportDataEntity>> reportDataEntityList = Lists.partition(reportDataEntities, 1000);
            for(List<ReportDataEntity> dataEntityList : reportDataEntityList) {
                updateMatchingCounter(pageNum, dataEntityList);
            }
        }
    }

    private void updateMatchingCounter(int pageNum, List<ReportDataEntity> dataEntityList) {
        List<Integer> bibIds = new ArrayList<>();
        for(ReportDataEntity reportDataEntity : dataEntityList) {
            List<String> bibIdList = Arrays.asList(reportDataEntity.getHeaderValue().split(","));
            bibIds.addAll(bibIdList.stream().map(Integer::parseInt).collect(Collectors.toList()));
        }
        logger.info("Bibs count in Page {} : {} " ,pageNum,bibIds.size());
        List<BibliographicEntity> bibliographicEntities = getBibliographicDetailsRepository().findByBibliographicIdIn(bibIds);
        for(BibliographicEntity bibliographicEntity : bibliographicEntities) {
            for(ItemEntity itemEntity : bibliographicEntity.getItemEntities()) {
                if(itemEntity.getCollectionGroupId().equals(getCollectionGroupMap().get(RecapConstants.SHARED_CGD))) {
                    MatchingCounter.updateCounter(itemEntity.getOwningInstitutionId(), false);
                }
            }
        }
    }

    /**
     * This method gets all collection group and puts it in a map.
     *
     * @return the collection group map
     */
    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            Iterable<CollectionGroupEntity> collectionGroupEntities = getCollectionGroupDetailsRepository().findAll();
            for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                CollectionGroupEntity collectionGroupEntity = iterator.next();
                collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
            }
        }
        return collectionGroupMap;
    }

    /**
     * This method gets all institution entity and puts it in a map.
     *
     * @return the institution entity map
     */
    public Map getInstitutionEntityMap() {
        if (null == institutionMap) {
            institutionMap = new HashMap();
            Iterable<InstitutionEntity> institutionEntities = getInstitutionDetailsRepository().findAll();
            for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                InstitutionEntity institutionEntity = iterator.next();
                institutionMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionId());
            }
        }
        return institutionMap;
    }
}
