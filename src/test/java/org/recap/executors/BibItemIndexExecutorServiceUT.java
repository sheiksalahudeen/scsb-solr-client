package org.recap.executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.admin.SolrAdmin;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by premkb on 29/7/16.
 */
public class BibItemIndexExecutorServiceUT extends BaseTestCase{

    Logger logger = LoggerFactory.getLogger(BibItemIndexExecutorServiceUT.class);

    @Mock
    BibliographicDetailsRepository mockBibliographicDetailsRepository;

    @Mock
    SolrAdmin mockSolrAdmin;

    @Mock
    BibItemIndexCallable mockBibItemIndexCallable;

    @Mock
    BibCrudRepositoryMultiCoreSupport mockBibCrudRepositoryMultiCoreSupport;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mergeIndexFrequency() throws Exception {

        Mockito.when(mockBibliographicDetailsRepository.countByIsDeletedFalse()).thenReturn(500000L);
        Mockito.when(mockBibItemIndexCallable.call()).thenReturn(1000);

        BibItemIndexExecutorService bibItemIndexExecutorService = new MockBibItemIndexExecutorService();
        bibItemIndexExecutorService.setBibliographicDetailsRepository(mockBibliographicDetailsRepository);
        bibItemIndexExecutorService.setSolrAdmin(mockSolrAdmin);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setOwningInstitutionCode(null);
        solrIndexRequest.setCommitInterval(10000);
        bibItemIndexExecutorService.index(solrIndexRequest);
    }

    private class MockBibItemIndexExecutorService extends BibItemIndexExecutorService {
        @Override
        public Callable getCallable(String coreName, int startingPage, int numRecordsPerPage, Integer owningInstitutionId, Date fromDate) {
            return mockBibItemIndexCallable;
        }

        @Override
        protected BibCrudRepositoryMultiCoreSupport getBibCrudRepositoryMultiCoreSupport(String solrUrl, String coreName) {
            return mockBibCrudRepositoryMultiCoreSupport;
        }
    }
}
