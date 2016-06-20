package org.recap.executors;

import org.recap.admin.SolrAdmin;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
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
    @Autowired
    SolrAdmin solrAdmin;

    @Value("${solr.url}")
    String solrUrl;

    @Value("${bib.rest.url}")
    public String bibResourceURL;

    @Value("${item.rest.url}")
    public String itemResourceURL;
    private ExecutorService executorService;
    private Integer loopCount;
    private double mergeIndexInterval;
    private long startTime;
    private StopWatch stopWatch;

    public void indexByOwningInstitutionId(SolrIndexRequest solrIndexRequest) {
        startProcess();

        Integer numThreads = solrIndexRequest.getNumberOfThreads();
        Integer docsPerThread = solrIndexRequest.getNumberOfDocs();
        Integer owningInstitutionId = solrIndexRequest.getOwningInstitutionId();

        try {
            ExecutorService executorService = getExecutorService(numThreads);

            Integer totalDocCount = (null == owningInstitutionId ? getTotalDocCount(null) : getTotalDocCount(owningInstitutionId));

            int quotient = totalDocCount / (docsPerThread);
            int remainder = totalDocCount % (docsPerThread);

            loopCount = remainder == 0 ? quotient : quotient + 1;

            List<String> coreNames = new ArrayList<>();

            setupCoreNames(numThreads, coreNames);

            solrAdmin.createSolrCores(coreNames);

            mergeIndexInterval = Math.ceil(loopCount / 2);

            int coreNum = 0;
            List<Future> futures = new ArrayList<>();

            for (int pageNum = 0; pageNum < loopCount; pageNum++) {
                Callable callable = getCallable(coreNames.get(coreNum), pageNum, docsPerThread);
                futures.add(executorService.submit(callable));
                coreNum = coreNum < numThreads-1 ? coreNum + 1 : 0;
            }

            int mergeIndexCount = 0;
            int totalBibsProcessed = 0;

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            int futureCount=0;
            for (Iterator<Future> iterator = futures.iterator(); iterator.hasNext(); ) {
                Future future = iterator.next();
                try {
                    future.get();
                    futureCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if (mergeIndexCount < mergeIndexInterval-1) {
                    mergeIndexCount++;
                } else {
                    solrAdmin.mergeCores(coreNames);
                    deleteTempIndexes(coreNames, solrUrl);
                    mergeIndexCount = 0;
                }
            }
            System.out.println("Num futures executed: " + futureCount);
            solrAdmin.mergeCores(coreNames);
            stopWatch.stop();
            System.out.println("Time taken to fetch " + totalBibsProcessed + " Bib Records and index : " + stopWatch.getTotalTimeSeconds() + " seconds" );
            solrAdmin.unLoadCores(coreNames);
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        endProcess();
    }

    public void index(SolrIndexRequest solrIndexRequest) {
        indexByOwningInstitutionId(solrIndexRequest);
    }

    private ExecutorService getExecutorService(Integer numThreads) {
        if (null == executorService) {
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
        if(null == stopWatch) {
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

    public abstract Callable getCallable(String coreName, int pageNum, int docsPerpage);

    protected abstract Integer getTotalDocCount(Integer owningInstitutionId);

    protected abstract String getResourceURL();
}
