package org.recap.executors;

import org.junit.Before;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.Bib;
import org.recap.model.Item;
import org.recap.repository.main.BibCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class ExecutorTest extends BaseTestCase {

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    BibCrudRepository bibCrudRepository;

    private int numThreads = 5;
    private int docsPerThread = 1000;

    @Test
    public void indexBibsFromDB() throws Exception {
        bibCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibIndexExecutorService.index(numThreads, docsPerThread);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

}
