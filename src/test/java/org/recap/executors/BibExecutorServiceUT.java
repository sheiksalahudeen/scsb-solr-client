package org.recap.executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.admin.SolrAdmin;
import org.recap.model.solr.SolrIndexRequest;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.solr.temp.BibCrudRepositoryMultiCoreSupport;

import java.util.concurrent.Callable;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by pvsubrah on 6/19/16.
 */
public class BibExecutorServiceUT {

    @Mock
    BibliographicDetailsRepository mockBibliographicDetailsRepository;

    @Mock
    SolrAdmin mockSolrAdmin;

    @Mock
    BibIndexCallable mockBibIndexCallable;

    @Mock
    BibCrudRepositoryMultiCoreSupport mockBibCrudRepositoryMultiCoreSupport;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mergeIndexFrequency() throws Exception {

        Mockito.when(mockBibliographicDetailsRepository.count()).thenReturn(500000L);
        Mockito.when(mockBibIndexCallable.call()).thenReturn(1000);

        BibIndexExecutorService bibIndexExecutorService = new MockBibIndexExecutorService();
        bibIndexExecutorService.setBibliographicDetailsRepository(mockBibliographicDetailsRepository);
        bibIndexExecutorService.setSolrAdmin(mockSolrAdmin);
        SolrIndexRequest solrIndexRequest = new SolrIndexRequest();
        solrIndexRequest.setNumberOfThreads(5);
        solrIndexRequest.setNumberOfDocs(1000);
        solrIndexRequest.setOwningInstitutionCode(null);
        bibIndexExecutorService.index(solrIndexRequest);
        assertEquals(1000,mockBibIndexCallable.call());
    }

    private class MockBibIndexExecutorService extends BibIndexExecutorService {
        @Override
        public Callable getCallable(String coreName, int startingPage, int numRecordsPerPage, Integer owningInstitutionId) {
            return mockBibIndexCallable;
        }

        @Override
        protected BibCrudRepositoryMultiCoreSupport getBibCrudRepositoryMultiCoreSupport(String solrUrl, String coreName) {
            return mockBibCrudRepositoryMultiCoreSupport;
        }
    }
}
