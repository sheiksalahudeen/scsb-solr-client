package org.recap.executors;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */

public class ExecutorAT extends BaseTestCase {

    @Autowired
    BibIndexExecutorService bibIndexExecutorService;

    @Autowired
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Autowired
    ItemIndexExecutorService itemIndexExecutorService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${solr.server.protocol}")
    String solrServerProtocol;

    @Value("${solr.url}")
    String solrUrl;

    @Autowired
    SolrTemplate solrTemplate;

    private int numThreads = 5;
    private int docsPerThread = 10000;


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
        bibSolrCrudRepository.deleteAll();
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
        bibSolrCrudRepository.deleteAll();
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
        bibSolrCrudRepository.deleteAll();
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
        solrIndexRequest.setOwningInstitutionId(2);
        bibSolrCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        indexDocuments(solrIndexRequest);
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

    //Added for ReCAP-111 jira, test will be failing until duplicate issue is fixed.
    @Test
    public void testDuplicateRecordsIndexed() throws Exception {
        bibSolrCrudRepository.deleteAll();
        URL resource = getClass().getResource("BibContentToCheckDuplicateRecords.xml");
        File bibContentFile = new File(resource.toURI());
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setOwningInstitutionId(3);
        String owningInstitutionBibId = "001";
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);

        BibliographicEntity fetchedBibliographicEntity = bibliographicDetailsRepository.findByOwningInstitutionIdAndOwningInstitutionBibId(3, owningInstitutionBibId);
        assertNotNull(fetchedBibliographicEntity);
        assertNotNull(fetchedBibliographicEntity.getContent());

        performIndex(fetchedBibliographicEntity);

        Integer bibliographicId = fetchedBibliographicEntity.getBibliographicId();

        Long countOnSingleIndex = bibSolrCrudRepository.countByBibId(bibliographicId);
        assertEquals(countOnSingleIndex, new Long(1));

        Thread.sleep(1000);

        performIndex(fetchedBibliographicEntity);

        Long countOnDoubleIndex = bibSolrCrudRepository.countByBibId(bibliographicId);
        assertEquals(countOnDoubleIndex, new Long(1));

    }

    private void performIndex(BibliographicEntity bibliographicEntity) throws Exception {
        List<String> coreNames = new ArrayList<>();
        coreNames.add("temp0");
        solrAdmin.createSolrCores(coreNames);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        List<Future> futures = new ArrayList<>();
        Future submit = executorService.submit(new BibRecordSetupCallable(bibliographicEntity));
        futures.add(submit);
        List<Bib> bibsToIndex = new ArrayList<>();

        for (Iterator<Future> futureIterator = futures.iterator(); futureIterator.hasNext(); ) {
            Future future = futureIterator.next();

            Bib bib = (Bib) future.get();
            bibsToIndex.add(bib);
        }

        BibCrudRepositoryMultiCoreSupport bibCrudRepositoryMultiCoreSupport = new BibCrudRepositoryMultiCoreSupport(coreNames.get(0), solrServerProtocol + solrUrl);

        if (!CollectionUtils.isEmpty(bibsToIndex)) {
            bibCrudRepositoryMultiCoreSupport.save(bibsToIndex);
            solrTemplate.setSolrCore(coreNames.get(0));
            Thread.sleep(5000);
            solrTemplate.commit();
        }
        Thread.sleep(15000);
        solrAdmin.mergeCores(coreNames);
        Thread.sleep(15000);
        bibCrudRepositoryMultiCoreSupport.deleteAll();
        solrAdmin.unLoadCores(coreNames);
        executorService.shutdown();
    }

    @Test
    public void testUpdateIndexes() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionId(3);
        bibSolrCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        indexDocuments(solrIndexRequest);
        Thread.sleep(2000);
        long firstCount = bibSolrCrudRepository.countByDocType("Bib");
        indexDocuments(solrIndexRequest);
        Thread.sleep(2000);
        long secondCount = bibSolrCrudRepository.countByDocType("Bib");
        assertEquals(firstCount, secondCount);
    }

    private void indexDocuments(SolrIndexRequest solrIndexRequest) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        bibItemIndexExecutorService.indexByOwningInstitutionId(solrIndexRequest);
        stopWatch.stop();
        System.out.println("Total time taken:" + stopWatch.getTotalTimeSeconds());
    }

}
