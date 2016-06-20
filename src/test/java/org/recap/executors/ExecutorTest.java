package org.recap.executors;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.solr.main.BibCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class ExecutorTest extends BaseTestCase {

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    BibCrudRepository bibCrudRepository;

    @Autowired
    ItemIndexExecutorService itemIndexExecutorService;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    private int numThreads = 5;
    private int docsPerThread = 1000;

    @Test
    public void indexBibsFromDB() throws Exception {
        unloadCores();
        bibCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibIndexExecutorService.index(numThreads, docsPerThread);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void indexItemsFromDB() throws Exception {
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        itemIndexExecutorService.index(numThreads, docsPerThread);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

}
