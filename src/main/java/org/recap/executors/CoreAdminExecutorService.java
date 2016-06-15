package org.recap.executors;

import com.google.common.collect.Lists;
import org.recap.admin.SolrAdmin;
import org.recap.model.Bib;
import org.recap.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by pvsubrah on 6/13/16.
 */


@Service
public class CoreAdminExecutorService {
    @Autowired
    SolrAdmin solrAdmin;

    @Value("${solr.url}")
    String solrUrl;


    public void indexBibs(Integer numThreads, Integer chunkSize , List<Bib> bibs){
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<String> coreNames = new ArrayList<>();

        setupCoreNames(numThreads, coreNames);

        solrAdmin.createSolrCores(coreNames);

        List<Future> futures = new ArrayList<>();

        List<List<Bib>> partitionedLists = Lists.partition(bibs, chunkSize);

        int i = 0;
        for (Iterator<List<Bib>> iterator = partitionedLists.iterator(); iterator.hasNext(); ) {
            List<Bib> bibList = iterator.next();
            BibIndexCallable bibIndexCallable = new BibIndexCallable(solrUrl, coreNames.get(i), bibList);
            futures.add(executorService.submit(bibIndexCallable));
            i++;
            if(i > numThreads){
                i=0;
            }
        }

        getFuture(futures);

        solrAdmin.mergeCores(coreNames);
        solrAdmin.unLoadCores(coreNames);
        executorService.shutdown();
    }

    public void indexItems(Integer numThreads, Integer chunkSize, List<Item> items) {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<String> coreNames = new ArrayList<>();

        setupCoreNames(numThreads, coreNames);

        solrAdmin.createSolrCores(coreNames);

        List<Future> futures = new ArrayList<>();

        List<List<Item>> partitionedLists = Lists.partition(items, chunkSize);

        int i=0;
        for(Iterator<List<Item>> iterator = partitionedLists.iterator(); iterator.hasNext();) {
            List<Item> itemList = iterator.next();
            ItemIndexCallable itemIndexCallable = new ItemIndexCallable(solrUrl, coreNames.get(i), itemList);
            futures.add(executorService.submit(itemIndexCallable));
            i++;
            if(i > numThreads) {
                i=0;
            }
        }

        getFuture(futures);

        solrAdmin.mergeCores(coreNames);
        solrAdmin.unLoadCores(coreNames);
        executorService.shutdown();
    }

    private void getFuture(List<Future> futures) {
        for (Future future : futures){
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupCoreNames(Integer numThreads, List<String> coreNames) {
        for(int i = 0; i < numThreads; i++){
            coreNames.add("temp"+i);
        }
    }

}
