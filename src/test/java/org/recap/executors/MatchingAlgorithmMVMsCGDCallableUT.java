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
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 5/7/17.
 */
public class MatchingAlgorithmMVMsCGDCallableUT extends BaseTestCase{

    @Mock
    private ReportDataDetailsRepository reportDataDetailsRepository;
    @Mock
    private BibliographicDetailsRepository mockedBibliographicDetailsRepository;
    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;
    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;
    @Mock
    private ItemDetailsRepository itemDetailsRepository;
    @Mock
    private MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor;
    @Autowired
    ProducerTemplate producerTemplate;
    @Mock
    private Map collectionGroupMap;
    @PersistenceContext
    private EntityManager entityManager;

    long from = new Long(0);
    int pageNum = 1;
    Integer batchSize = 10;
    BibliographicEntity bibliographicEntity = null;
    int collectionGroupId = 0;



    public MatchingAlgorithmMVMsCGDCallableUT() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        from = pageNum * Long.valueOf(batchSize);
        MatchingCounter.reset();
        bibliographicEntity = saveBibSingleHoldingsSingleItem();
        collectionGroupId = bibliographicEntity.getItemEntities().get(0).getCollectionGroupId();
        Mockito.when(reportDataDetailsRepository.getReportDataEntityForMatchingMVMs(RecapConstants.BIB_ID, from, batchSize)).thenReturn(getReportDataEntity(bibliographicEntity.getBibliographicId()));
        Mockito.when((Integer) collectionGroupMap.get(RecapConstants.REPORTS_OPEN)).thenReturn(2);
        Mockito.when(collectionGroupMap.get(RecapConstants.SHARED_CGD)).thenReturn(1);
        Mockito.when(mockedBibliographicDetailsRepository.findByBibliographicId(Mockito.any())).thenReturn(bibliographicEntity);
    }

    public List<ReportDataEntity> getReportDataEntity(Integer bibId){
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderValue(bibId.toString());
        reportDataEntityList.add(reportDataEntity);
        return reportDataEntityList;
    }

    @Test
    public void testMatchingAlgorithmMVMsCGDCallable() throws Exception {
        Map institutionMap = new HashMap();
        MatchingAlgorithmMVMsCGDCallable matchingAlgorithmMVMsCGDCallable = new MatchingAlgorithmMVMsCGDCallable(reportDataDetailsRepository, mockedBibliographicDetailsRepository,pageNum,batchSize,producerTemplate,
                collectionGroupMap,institutionMap,itemChangeLogDetailsRepository,collectionGroupDetailsRepository,itemDetailsRepository);
        Object object = matchingAlgorithmMVMsCGDCallable.call();
        assertEquals(collectionGroupId,1);
        Thread.sleep(1000);
        BibliographicEntity afterUpdate = mockedBibliographicDetailsRepository.findByBibliographicId(bibliographicEntity.getBibliographicId());
        assertNotNull(afterUpdate);
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1134);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }




}