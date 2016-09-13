package org.recap.executors;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.seda.SedaEndpoint;
import org.apache.camel.component.solr.SolrConstants;
import org.recap.admin.SolrAdmin;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by pvsubrah on 6/13/16.
 */

public abstract class IndexExecutorService {

    Logger logger = LoggerFactory.getLogger(IndexExecutorService.class);

    @Autowired
    SolrAdmin solrAdmin;

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.parent.core}")
    String solrCore;

    @Value("${bib.rest.url}")
    public String bibResourceURL;

    @Value("${item.rest.url}")
    public String itemResourceURL;

    @Value("${solr.url}")
    String solrUri;

    @Value("${solr.router.uri.type}")
    String solrRouterURI;

    private ExecutorService executorService;
    private Integer loopCount;
    private Integer callableCountByCommitInterval;
    private long startTime;
    private StopWatch stopWatch;

    public void indexByOwningInstitutionId(SolrIndexRequest solrIndexRequest) {
        startProcess();

        Integer numThreads = solrIndexRequest.getNumberOfThreads();
        Integer docsPerThread = solrIndexRequest.getNumberOfDocs();
        Integer commitIndexesInterval = solrIndexRequest.getCommitInterval();
        Integer owningInstitutionId = solrIndexRequest.getOwningInstitutionId();

        try {
            ExecutorService executorService = getExecutorService(numThreads);

            Integer totalDocCount = (null == owningInstitutionId ? getTotalDocCount(null) : getTotalDocCount(owningInstitutionId));

            logger.info("Total Document Count From DB : " + totalDocCount);

            if (totalDocCount > 0) {
                int quotient = totalDocCount / (docsPerThread);
                int remainder = totalDocCount % (docsPerThread);
                loopCount = remainder == 0 ? quotient : quotient + 1;
                logger.info("Loop Count Value : " + loopCount);
                logger.info("Commit Indexes Interval : " + commitIndexesInterval);

                callableCountByCommitInterval = commitIndexesInterval / (docsPerThread);
                if (callableCountByCommitInterval == 0) {
                    callableCountByCommitInterval = 1;
                }
                logger.info("Number of callables to execute to commit indexes : " + callableCountByCommitInterval);

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                int coreNum = 0;
                List<Callable<Integer>> callables = new ArrayList<>();
                for (int pageNum = 0; pageNum < loopCount; pageNum++) {
                    Callable callable = getCallable(solrCore, pageNum, docsPerThread, owningInstitutionId);
                    callables.add(callable);
                    coreNum = coreNum < numThreads - 1 ? coreNum + 1 : 0;
                }

                int futureCount = 0;
                int totalBibsProcessed = 0;
                List<List<Callable<Integer>>> partitions = Lists.partition(new ArrayList<Callable<Integer>>(callables), callableCountByCommitInterval);
                for (List<Callable<Integer>> partitionCallables : partitions) {
                    List<Future<Integer>> futures = executorService.invokeAll(partitionCallables);
                    futures
                            .stream()
                            .map(future -> {
                                try {
                                    return future.get();
                                } catch (Exception e) {
                                    throw new IllegalStateException(e);
                                }
                            });
                    logger.info("No of Futures Added : " + futures.size());

                    int numOfBibsProcessed = 0;
                    for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
                        Future future = iterator.next();
                        try {
                            Integer entitiesCount = (Integer) future.get();
                            numOfBibsProcessed += entitiesCount;
                            totalBibsProcessed += entitiesCount;
                            logger.info("Num of bibs fetched by thread : " + entitiesCount);
                            futureCount++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    SedaEndpoint solrQSedaEndPoint = (SedaEndpoint) producerTemplate.getCamelContext().getEndpoint("seda:solrQ");
                    Integer solrQSize = solrQSedaEndPoint.getExchanges().size();
                    while (solrQSize != 0) {
                        solrQSize = solrQSedaEndPoint.getExchanges().size();
                    }
                    Future<Object> future = producerTemplate.asyncRequestBodyAndHeader(solrRouterURI + "://" + solrUri + "/" + solrCore, "", SolrConstants.OPERATION, SolrConstants.OPERATION_COMMIT);
                    logger.info("Commit future done : " + future.isDone());
                    while (!future.isDone()) {
                        //NoOp.
                    }
                    logger.info("Num of Bibs Processed and indexed to core on commit interval : " + numOfBibsProcessed);
                    logger.info("Total Num of Bibs Processed and indexed to core : " + totalBibsProcessed);
                }
                logger.info("Total futures executed: " + futureCount);
                stopWatch.stop();
                logger.info("Time taken to fetch " + totalBibsProcessed + " Bib Records and index : " + stopWatch.getTotalTimeSeconds() + " seconds");
                executorService.shutdown();

                //Final commit
                producerTemplate.asyncRequestBodyAndHeader(solrRouterURI + "://" + solrUri + "/" + solrCore, "", SolrConstants.OPERATION, SolrConstants.OPERATION_COMMIT);
            } else {
                logger.info("No records found to index for the criteria");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        endProcess();
    }

    public void index(SolrIndexRequest solrIndexRequest) {
        indexByOwningInstitutionId(solrIndexRequest);
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(numThreads);
        }
        return executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public long getStartTime() {
        return startTime;
    }

    public StopWatch getStopWatch() {
        if (null == stopWatch) {
            stopWatch = new StopWatch();
        }
        return stopWatch;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private void deleteTempIndexes(List<String> coreNames, String solrUrl) {
        for (Iterator<String> iterator = coreNames.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport = getBibCrudRepositoryMultiCoreSupport(solrUrl, coreName);
            bibCrudRepositoryMultiCoreSupport.deleteAll();
        }
    }

    protected BibCrudRepositoryMultiCoreSupport getBibCrudRepositoryMultiCoreSupport(String solrUrl, String coreName) {
        return new BibCrudRepositoryMultiCoreSupport(coreName, solrUrl);
    }

    private void setupCoreNames(Integer numThreads, List<String> coreNames) {
        for (int i = 0; i < numThreads; i++) {
            coreNames.add("temp" + i);
        }
    }

    private void startProcess() {
        startTime = System.currentTimeMillis();
        getStopWatch().start();
    }

    private void endProcess() {
        getStopWatch().stop();
    }

    public void setSolrAdmin(SolrAdmin solrAdmin) {
        this.solrAdmin = solrAdmin;
    }

    public abstract Callable getCallable(String coreName, int pageNum, int docsPerpage, Integer owningInstitutionId);

    protected abstract Integer getTotalDocCount(Integer owningInstitutionId);

    protected abstract String getResourceURL();
}
