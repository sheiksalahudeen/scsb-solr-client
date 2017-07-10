package org.recap.matchingalgorithm.service;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.camel.activemq.JmxHelper;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.*;
import org.recap.util.MatchingAlgorithmUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 7/7/17.
 */
public class MatchingAlgorithmUpdateCGDServiceUT extends BaseTestCase{

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private Map collectionGroupMap;

    @Mock
    private Map institutionMap;

    @Mock
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private ReportDataDetailsRepository reportDataDetailsRepository;

    @Mock
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    private JmxHelper jmxHelper;

    @Mock
    private MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    private ItemDetailsRepository itemDetailsRepository;

    @Mock
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Test
    public void testCollectionGroupMap(){
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setCollectionGroupId(1);
        List<CollectionGroupEntity> collectionGroupEntityList = new ArrayList<>();
        collectionGroupEntityList.add(collectionGroupEntity);
        Iterable<CollectionGroupEntity> collectionGroupEntityIterable = collectionGroupEntityList;
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository()).thenReturn(collectionGroupDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository().findAll()).thenReturn(collectionGroupEntityIterable);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupMap()).thenCallRealMethod();
        Map map = matchingAlgorithmUpdateCGDService.getCollectionGroupMap();
        assertNotNull(map);
        assertTrue(map.size() == 1);
        assertEquals(map.get("Shared"),1);
    }

    @Test
    public void testInstitutionEntityMap(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionId(1);
        List<InstitutionEntity> institutionEntityList = new ArrayList<>();
        institutionEntityList.add(institutionEntity);
        Iterable<InstitutionEntity> institutionEntityIterable = institutionEntityList;
        Mockito.when(matchingAlgorithmUpdateCGDService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getInstitutionDetailsRepository().findAll()).thenReturn(institutionEntityIterable);
        Mockito.when(matchingAlgorithmUpdateCGDService.getInstitutionEntityMap()).thenCallRealMethod();
        Map map = matchingAlgorithmUpdateCGDService.getInstitutionEntityMap();
        assertNotNull(map);
        assertTrue(map.size() == 1);
        assertEquals(map.get("PUL"),1);
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getProducerTemplate()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getInstitutionDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemChangeLogDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getJmxHelper()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getMatchingAlgorithmUtil()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemDetailsRepository()).thenCallRealMethod();
        assertNotEquals(bibliographicDetailsRepository,matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository());
        assertNotEquals(producerTemplate,matchingAlgorithmUpdateCGDService.getProducerTemplate());
        assertNotEquals(collectionGroupDetailsRepository,matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository());
        assertNotEquals(institutionDetailsRepository,matchingAlgorithmUpdateCGDService.getInstitutionDetailsRepository());
        assertNotEquals(reportDataDetailsRepository,matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository());
        assertNotEquals(itemChangeLogDetailsRepository,matchingAlgorithmUpdateCGDService.getItemChangeLogDetailsRepository());
        assertNotEquals(jmxHelper,matchingAlgorithmUpdateCGDService.getJmxHelper());
        assertNotEquals(matchingAlgorithmUtil,matchingAlgorithmUpdateCGDService.getMatchingAlgorithmUtil());
        assertNotEquals(itemDetailsRepository,matchingAlgorithmUpdateCGDService.getItemDetailsRepository());
    }



}