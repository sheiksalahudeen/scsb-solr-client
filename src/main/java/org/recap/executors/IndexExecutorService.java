package org.recap.executors;

import org.recap.admin.SolrAdmin;
import org.recap.repository.temp.BibCrudRepositoryMultiCoreSupport;
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

    public void index(Integer numThreads, Integer docsPerThread) {

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        Integer totalDocCount = getTotalDocCount();

        int quotient = totalDocCount / (docsPerThread);
        int remainder = totalDocCount % (docsPerThread);

        Integer loopCount = remainder == 0 ? quotient : quotient + 1;

        List<String> coreNames = new ArrayList<>();

        setupCoreNames(numThreads, coreNames);

        solrAdmin.createSolrCores(coreNames);

        int coreNum = 0;
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < loopCount; i++) {
            Callable callable = getCallable(coreNames.get(coreNum), i, docsPerThread);
            futures.add(executorService.submit(callable));
            coreNum = coreNum < numThreads ? 0 : coreNum + 1;
        }

        int mergeIndexCount = 0;

        for (Iterator<Future> iterator = futures.iterator(); iterator.hasNext(); ) {
            Future future = iterator.next();
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (mergeIndexCount < loopCount / 3) {
                mergeIndexCount++;
            } else {
                solrAdmin.mergeCores(coreNames);
                deleteTempIndexes(coreNames, solrUrl);
            }
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        solrAdmin.mergeCores(coreNames);
        stopWatch.stop();
        System.out.println("Time taken to merge cores: " + stopWatch.getTotalTimeSeconds());
        solrAdmin.unLoadCores(coreNames);
        executorService.shutdown();

    }

    private void deleteTempIndexes(List<String> coreNames, String solrUrl) {
        for (Iterator<String> iterator = coreNames.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport = new BibCrudRepositoryMultiCoreSupport(coreName, solrUrl);
            bibCrudRepositoryMultiCoreSupport.deleteAll();
        }
    }

    private void setupCoreNames(Integer numThreads, List<String> coreNames) {
        for (int i = 0; i < numThreads; i++) {
            coreNames.add("temp" + i);
        }
    }

    public abstract Callable getCallable(String coreName, int from, int to);

    protected abstract Integer getTotalDocCount();

    protected abstract String getResourceURL();
}
