package org.recap.executors;

import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */

@Ignore
public class ExecutorTest extends BaseTestCase {

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Autowired
    BibSolrCrudRepository bibCrudRepository;

    @Autowired
    ItemIndexExecutorService itemIndexExecutorService;

    @Autowired
    ItemCrudRepository itemCrudRepository;

    private int numThreads = 5;
    private int docsPerThread = 1000;

    @Before
    public void unloadCores() throws Exception {

        CoreAdminRequest coreAdminRequest = solrAdmin.getCoreAdminRequest();

        coreAdminRequest.setAction(CoreAdminParams.CoreAdminAction.STATUS);
        CoreAdminResponse cores = coreAdminRequest.process(solrAdminClient);

        List<String> coreList = new ArrayList<String>();
        for (int i = 0; i < cores.getCoreStatus().size(); i++) {
            String name = cores.getCoreStatus().getName(i);
            if (name.contains("temp")) {
                coreList.add(name);
            }
        }

        for (Iterator<String> iterator = coreList.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            CoreAdminResponse adminResponse = coreAdminRequest.unloadCore(coreName, true, true, solrAdminClient);
            assertTrue(adminResponse.getStatus() == 0);

        }
    }


    @Test
    public void indexBibsFromDB() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(null);
        unloadCores();
        bibCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibIndexExecutorService.index(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }


    @Test
    public void indexBibsAndItemsFromDB() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(null);
        unloadCores();
        bibCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibItemIndexExecutorService.index(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void indexBibsFromDBByOwningInstitutionId() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(3);
        unloadCores();
        bibCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibIndexExecutorService.indexByOwningInstitutionId(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void indexBibsAndItemsFromDBByOwningInstitutionId() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(3);
        unloadCores();
        bibCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibItemIndexExecutorService.indexByOwningInstitutionId(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void indexItemsFromDB() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(null);
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        itemIndexExecutorService.index(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

    @Test
    public void indexItemsFromDBByOwningInstitutionId() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(3);
        itemCrudRepository.deleteAll();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        itemIndexExecutorService.index(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

}
