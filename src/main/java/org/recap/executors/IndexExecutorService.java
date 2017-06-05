package org.recap.executors;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.admin.SolrAdmin;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by pvsubrah on 6/13/16.
 */
public abstract class IndexExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(IndexExecutorService.class);

    @Autowired
    SolrAdmin solrAdmin;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    BibSolrCrudRepository bibSolrCrudRepository;

    @Value("${solr.server.protocol}")
    String solrServerProtocol;

    @Value("${solr.parent.core}")
    String solrCore;

    @Value("${solr.url}")
    String solrUrl;

    @Value("${solr.router.uri.type}")
    String solrRouterURI;

    /**
     * This method initiates the solr indexing based on the selected owning institution.
     * @param solrIndexRequest
     * @return
     */
    public Integer indexByOwningInstitutionId(SolrIndexRequest solrIndexRequest) {
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();

        Integer numThreads = solrIndexRequest.getNumberOfThreads();
        Integer docsPerThread = solrIndexRequest.getNumberOfDocs();
        Integer commitIndexesInterval = solrIndexRequest.getCommitInterval();
        String owningInstitutionCode = solrIndexRequest.getOwningInstitutionCode();
        String fromDate = solrIndexRequest.getDateFrom();
        Integer owningInstitutionId = null;
        Date from = null;
        String coreName = solrCore;
        Integer totalBibsProcessed = 0;
        boolean isIncremental = StringUtils.isNotBlank(fromDate) ? Boolean.TRUE : Boolean.FALSE;

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            if (StringUtils.isNotBlank(owningInstitutionCode)) {
                InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(owningInstitutionCode);
                if (null != institutionEntity) {
                    owningInstitutionId = institutionEntity.getInstitutionId();
                }
            }
            if (isIncremental) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(RecapConstants.INCREMENTAL_DATE_FORMAT);
                from = dateFormatter.parse(fromDate);
            }
            Integer totalDocCount = getTotalDocCount(owningInstitutionId, from);
            logger.info("Total Document Count From DB : {}",totalDocCount);

            if (totalDocCount > 0) {
                int quotient = totalDocCount / (docsPerThread);
                int remainder = totalDocCount % (docsPerThread);
                Integer loopCount = remainder == 0 ? quotient : quotient + 1;
                logger.info("Loop Count Value : ",loopCount);
                logger.info("Commit Indexes Interval : {}",commitIndexesInterval);

                Integer callableCountByCommitInterval = commitIndexesInterval / (docsPerThread);
                if (callableCountByCommitInterval == 0) {
                    callableCountByCommitInterval = 1;
                }
                logger.info("Number of callables to execute to commit indexes : {}",callableCountByCommitInterval);

                List<String> coreNames = new ArrayList<>();
                if (!isIncremental) {
                    setupCoreNames(numThreads, coreNames);
                    solrAdmin.createSolrCores(coreNames);
                }

                StopWatch stopWatch = new StopWatch();
                stopWatch.start();

                int coreNum = 0;
                List<Callable<Integer>> callables = new ArrayList<>();
                for (int pageNum = 0; pageNum < loopCount; pageNum++) {
                    if (!isIncremental) {
                        coreName = coreNames.get(coreNum);
                        coreNum = coreNum < numThreads - 1 ? coreNum + 1 : 0;
                    }
                    Callable callable = getCallable(coreName, pageNum, docsPerThread, owningInstitutionId, from);
                    callables.add(callable);
                }

                int futureCount = 0;
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
                    logger.info("No of Futures Added : {}",futures.size());

                    int numOfBibsProcessed = 0;
                    for (Iterator<Future<Integer>> iterator = futures.iterator(); iterator.hasNext(); ) {
                        Future future = iterator.next();
                        try {
                            Integer entitiesCount = (Integer) future.get();
                            numOfBibsProcessed += entitiesCount;
                            totalBibsProcessed += entitiesCount;
                            logger.info("Num of bibs fetched by thread : {}",entitiesCount);
                            futureCount++;
                        } catch (InterruptedException | ExecutionException e) {
                            logger.error(RecapConstants.LOG_ERROR,e);
                        }
                    }
                    if (!isIncremental) {
                        solrAdmin.mergeCores(coreNames);
                        logger.info("Solr core status : " + solrAdmin.getCoresStatus());
                        while (solrAdmin.getCoresStatus() != 0) {
                            logger.info("Solr core status : " + solrAdmin.getCoresStatus());
                        }
                        deleteTempIndexes(coreNames, solrServerProtocol + solrUrl);
                    }
                    logger.info("Num of Bibs Processed and indexed to core {} on commit interval : {} ",coreName,numOfBibsProcessed);
                    logger.info("Total Num of Bibs Processed and indexed to core {} : {}",coreName,totalBibsProcessed);
                    Long solrBibCount = bibSolrCrudRepository.countByDocType(RecapConstants.BIB);
                    logger.info("Total number of Bibs in Solr in recap core : {}",solrBibCount);
                }
                logger.info("Total futures executed: ",futureCount);
                stopWatch.stop();
                logger.info("Time taken to fetch {} Bib Records and index to recap core : {} seconds {}",totalBibsProcessed,stopWatch.getTotalTimeSeconds());
                if (!isIncremental) {
                    solrAdmin.unLoadCores(coreNames);
                }
                executorService.shutdown();
            } else {
                logger.info("No records found to index for the criteria");
            }
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        stopWatch1.stop();
        logger.info("Total time taken:{} secs",stopWatch1.getTotalTimeSeconds());
        return totalBibsProcessed;
    }

    /**
     * This method initiates solr indexing.
     * @param solrIndexRequest
     * @return
     */
    public Integer index(SolrIndexRequest solrIndexRequest) {
        return indexByOwningInstitutionId(solrIndexRequest);
    }

    /**
     * This method deletes the indexed data from the temporary cores after it is merged to main core.
     * @param coreNames
     * @param solrUrl
     */
    private void deleteTempIndexes(List<String> coreNames, String solrUrl) {
        for (Iterator<String> iterator = coreNames.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport = getBibCrudRepositoryMultiCoreSupport(solrUrl, coreName);
            bibCrudRepositoryMultiCoreSupport.deleteAll();
        }
    }

    /**
     * To get the bib solr crud repository object based on the given core name for operations on that core.
     * @param solrUrl
     * @param coreName
     * @return
     */
    protected BibCrudRepositoryMultiCoreSupport getBibCrudRepositoryMultiCoreSupport(String solrUrl, String coreName) {
        return new BibCrudRepositoryMultiCoreSupport(coreName, solrUrl);
    }

    /**
     * To create names for temporary cores.
     * @param numThreads
     * @param coreNames
     */
    private void setupCoreNames(Integer numThreads, List<String> coreNames) {
        for (int i = 0; i < numThreads; i++) {
            coreNames.add("temp" + i);
        }
    }

    /**
     * Sets solr admin.
     *
     * @param solrAdmin the solr admin
     */
    public void setSolrAdmin(SolrAdmin solrAdmin) {
        this.solrAdmin = solrAdmin;
    }

    /**
     * This method gets the appropiate callable to be processed by the thread to generate solr input documents and index to solr.
     *
     * @param coreName            the core name
     * @param pageNum             the page num
     * @param docsPerpage         the docs perpage
     * @param owningInstitutionId the owning institution id
     * @param fromDate            the from date
     * @return the callable
     */
    public abstract Callable getCallable(String coreName, int pageNum, int docsPerpage, Integer owningInstitutionId, Date fromDate);

    /**
     * This method gets the total doc count.
     *
     * @param owningInstitutionId the owning institution id
     * @param fromDate            the from date
     * @return the total doc count
     */
    protected abstract Integer getTotalDocCount(Integer owningInstitutionId, Date fromDate);
}