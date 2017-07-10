package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 5/7/17.
 */
public class MatchingAlgorithmMonographCGDCallableUT extends BaseTestCase{

    @Mock
    private ReportDataDetailsRepository reportDataDetailsRepository;
    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;
    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    @Mock
    private ItemDetailsRepository itemDetailsRepository;
    @Mock
    private MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor;
    @Mock
    ProducerTemplate producerTemplate;

    long from = new Long(0);
    int pageNum = 1;
    Integer batchSize = 10;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        from = pageNum * Long.valueOf(batchSize);
        Mockito.when(reportDataDetailsRepository.getReportDataEntityForMatchingMonographs(RecapConstants.BIB_ID, from, batchSize)).thenReturn(getReportDataEntity());
    }

    @Test
    public void testMatchingAlgorithmMonographCGDCallable() throws Exception {
        Map collectionGroupMap = new HashMap();
        Map institutionMap = new HashMap();

        MatchingAlgorithmMonographCGDCallable matchingAlgorithmMonographCGDCallable = new MatchingAlgorithmMonographCGDCallable(reportDataDetailsRepository,bibliographicDetailsRepository,pageNum,batchSize,producerTemplate,
                                                                                        collectionGroupMap,institutionMap,itemChangeLogDetailsRepository,collectionGroupDetailsRepository,itemDetailsRepository,true);
        Object object = matchingAlgorithmMonographCGDCallable.call();
        assertNotNull(object);
    }

    public List<ReportDataEntity> getReportDataEntity(){
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderValue("1134");
        reportDataEntityList.add(reportDataEntity);
        return reportDataEntityList;
    }

}