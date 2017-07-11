package org.recap.executors;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 5/7/17.
 */
public class MatchingBibItemIndexCallableUT extends BaseTestCase {

    private int pageNum = 1;
    private int docsPerPage = 5;
    private String coreName = "TempCore";
    @Mock
    private BibliographicDetailsRepository mockedBibliographicDetailsRepository;
    @Mock
    private HoldingsDetailsRepository holdingsDetailsRepository;
    @Autowired
    private ProducerTemplate producerTemplate;
    @Autowired
    private SolrTemplate solrTemplate;
    private String operationType;
    private Date from;
    private Date to;
    @PersistenceContext
    private EntityManager entityManager;
    BibliographicEntity bibliographicEntity = null;
    @Autowired
    MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        bibliographicEntity = saveBibSingleHoldingsSingleItem();
        Mockito.when(mockedBibliographicDetailsRepository.getBibliographicEntitiesForChangedItems(new PageRequest(pageNum, docsPerPage), operationType, from, to)).thenReturn(getBibliographicPagableObject(Arrays.asList(bibliographicEntity)));
        Mockito.when(mockedBibliographicDetailsRepository.getCountOfBibliographicEntitiesForChangedItems(operationType, from, to)).thenReturn(new Long(1));
    }

    @Test
    public void testMatchingBibItemIndexCallable() throws Exception {
        MatchingBibItemIndexCallable matchingBibItemIndexCallable = new MatchingBibItemIndexCallable(coreName, pageNum, docsPerPage, mockedBibliographicDetailsRepository, holdingsDetailsRepository,
                producerTemplate, solrTemplate, operationType, from, to);
        Object object = matchingBibItemIndexCallable.call();
        assertNotNull(object);

    }

    @Test
    public void testMatchingBibItemIndexExecutorService(){
        MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService = new MatchingBibItemIndexExecutorService();
        Callable callable = matchingBibItemIndexExecutorService.getCallable(coreName, pageNum, docsPerPage,operationType, from, to);
        assertNotNull(callable);

    }

    @Test
    public void testTotalDocument(){
        MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService = new MatchingBibItemIndexExecutorService();
        matchingBibItemIndexExecutorService.setBibliographicDetailsRepository(mockedBibliographicDetailsRepository);
        int bibCountForChangedItems = matchingBibItemIndexExecutorService.getTotalDocCount(operationType, from, to);
        assertEquals(bibCountForChangedItems,1);
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
        itemEntity.setBarcode("9123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        return savedBibliographicEntity;
    }


    public Page<BibliographicEntity> getBibliographicPagableObject(List<BibliographicEntity> bibliographicEntityList) {
        Page<BibliographicEntity> bibliographicEntityPageObject = new Page<BibliographicEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <S> Page<S> map(Converter<? super BibliographicEntity, ? extends S> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<BibliographicEntity> getContent() {
                return bibliographicEntityList;
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<BibliographicEntity> iterator() {
                Iterator<BibliographicEntity> bibliographicEntityIterator = bibliographicEntityList.iterator();
                return bibliographicEntityIterator;
            }
        };
        return bibliographicEntityPageObject;

    }






}