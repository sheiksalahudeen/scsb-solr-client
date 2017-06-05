package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pvsubrah on 6/14/16.
 */
@Ignore
public class ExecutorAT extends BaseTestCase {

    @Autowired
    BibItemIndexExecutorService bibItemIndexExecutorService;

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${solr.server.protocol}")
    String solrServerProtocol;

    @Value("${solr.url}")
    String solrUrl;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    private int numThreads = 5;
    private int docsPerThread = 10000;
    private int commitInterval = 10000;


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
    public void indexBibsAndItemsFromDB() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionCode(null);
        solrIndexRequest.setCommitInterval(commitInterval);
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
    public void indexBibsAndItemsFromDBByOwningInstitutionId() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionCode("CUL");
        solrIndexRequest.setCommitInterval(commitInterval);
        bibSolrCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        indexDocuments(solrIndexRequest);
    }


    @Test
    public void testUpdateIndexes() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setOwningInstitutionCode("NYPL");
        solrIndexRequest.setCommitInterval(commitInterval);
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

    @Test
    public void testIncrementalIndex() throws Exception {
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(numThreads);
        solrIndexRequest.setNumberOfDocs(docsPerThread);
        solrIndexRequest.setCommitInterval(commitInterval);
        //solrIndexRequest.setDateFrom("27-10-2016 01:00:00");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(RecapConstants.INCREMENTAL_DATE_FORMAT);
        Date from = DateUtils.addDays(new Date(), -1);
        solrIndexRequest.setDateFrom(dateFormatter.format(from));
        long dbCount = bibliographicDetailsRepository.countByLastUpdatedDateAfter(from);
        bibSolrCrudRepository.deleteAll();
        itemCrudRepository.deleteAll();
        indexDocuments(solrIndexRequest);
        solrTemplate.commit();
        Thread.sleep(5000);
        long solrCount = bibSolrCrudRepository.countByDocType("Bib");
        assertEquals(dbCount, solrCount);
    }

}
