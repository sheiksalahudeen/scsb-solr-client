package org.recap.executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.admin.SolrAdmin;
import org.recap.model.solr.Item;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.recap.repository.solr.temp.ItemCrudRepositoryMultiCoreSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by premkb on 1/8/16.
 */
public class ItemIndexExecutorServiceUT {

    Logger logger = LoggerFactory.getLogger(ItemIndexExecutorServiceUT.class);

    @Mock
    BibliographicDetailsRepository mockBibliographicDetailsRepository;

    @Mock
    ItemDetailsRepository mockItemDetailsRepository;

    @Mock
    SolrAdmin mockSolrAdmin;

    @Mock
    ItemIndexCallable mockItemIndexCallable;

    @Mock
    BibCrudRepositoryMultiCoreSupport mockBibCrudRepositoryMultiCoreSupport;
    
    @Mock
    ItemCrudRepositoryMultiCoreSupport mockItemCrudRepositoryMultiCoreSupport;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mergeIndexFrequency() throws Exception {
        Mockito.when(mockItemDetailsRepository.countByIsDeletedFalse()).thenReturn(500000L);
        Mockito.when(mockItemIndexCallable.call()).thenReturn(1000);

        ItemIndexExecutorService itemIndexExecutorService = new MockIndexExecutorService();
        itemIndexExecutorService.setItemDetailsRepository(mockItemDetailsRepository);
        itemIndexExecutorService.setSolrAdmin(mockSolrAdmin);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setOwningInstitutionCode(null);
        solrIndexRequest.setCommitInterval(10000);
        itemIndexExecutorService.index(solrIndexRequest);
    }

    private class MockIndexExecutorService extends ItemIndexExecutorService{
        @Override
        public Callable getCallable(String coreName, int startingPage, int numRecordsPerPage, Integer owningInstitutionId, Date fromDate){
            return mockItemIndexCallable;
        }

        @Override
        protected BibCrudRepositoryMultiCoreSupport getBibCrudRepositoryMultiCoreSupport(String solrUrl, String coreName) {
            return mockBibCrudRepositoryMultiCoreSupport;
        }
    }
}
