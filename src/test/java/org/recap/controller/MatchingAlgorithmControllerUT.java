package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.executors.MatchingBibItemIndexExecutorService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmHelperService;
import org.recap.matchingalgorithm.service.MatchingAlgorithmUpdateCGDService;
import org.recap.matchingalgorithm.service.MatchingBibInfoDetailService;
import org.recap.model.jpa.*;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.*;
import org.recap.util.MatchingAlgorithmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 1/8/16.
 */
public class MatchingAlgorithmControllerUT extends BaseControllerUT {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmControllerUT.class);

    @InjectMocks
    MatchingAlgorithmController matchingAlgorithmController = new MatchingAlgorithmController();

    @Mock
    MatchingAlgorithmController matchingAlgoController;

    @Mock
    ReportGenerator reportGenerator;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Mock
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    @Mock
    MatchingAlgorithmUpdateCGDService matchingAlgorithmUpdateCGDService;

    @Mock
    MatchingBibItemIndexExecutorService matchingBibItemIndexExecutorService;

    @Mock
    MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Mock
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Mock
    ReportDataDetailsRepository reportDataDetailsRepository;

    @Mock
    BibliographicDetailsRepository mockedBibliographicDetailsRepository;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ReportDetailRepository reportDetailRepository;

    @Mock
    private JmxHelper jmxHelper;

    @Mock
    private MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    private Map collectionGroupMap;
    private Map institutionMap;

    private Integer batchSize = 10000;

    private int pageNum = 1;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(matchingAlgoController.getLogger()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenReturn(String.valueOf(batchSize));
    }

    @Test
    public void matchingAlgorithmFullTest() throws Exception {
        Map<String, Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount", 1);
        matchingAlgoMap.put("culMatchingCount", 2);
        matchingAlgoMap.put("nyplMatchingCount", 3);
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenReturn(matchingAlgorithmHelperService);
        Mockito.when(matchingAlgorithmHelperService.findMatchingAndPopulateMatchPointsEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateMatchingBibEntities()).thenReturn(new Long(10));
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmFull()).thenCallRealMethod();
        String response = matchingAlgoController.matchingAlgorithmFull();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void matchingAlgorithmOnlyReports() throws Exception {
        Map<String, Integer> matchingAlgoMap = new HashMap<>();
        matchingAlgoMap.put("pulMatchingCount", 1);
        matchingAlgoMap.put("culMatchingCount", 2);
        matchingAlgoMap.put("nyplMatchingCount", 3);
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenReturn(matchingAlgorithmHelperService);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCandISBN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForOCLCAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndISSN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISBNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForISSNAndLCCN(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgorithmHelperService.populateReportsForSingleMatch(batchSize)).thenReturn(matchingAlgoMap);
        Mockito.when(matchingAlgoController.matchingAlgorithmOnlyReports()).thenCallRealMethod();
        String response = matchingAlgoController.matchingAlgorithmOnlyReports();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateMonographCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMonographs(batchSize);
        Mockito.when(matchingAlgoController.updateMonographCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMonographCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void testUpdateMonographCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        //Mockito.when(matchingAlgorithmUpdateCGDService.getLogger()).thenCallRealMethod();
        Mockito.when(matchingAlgorithmUpdateCGDService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository()).thenReturn(collectionGroupDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgorithmUtil.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(reportDataDetailsRepository.getCountOfRecordNumForMatchingMonograph(RecapConstants.BIB_ID)).thenReturn(new Long(10000));
        Mockito.doCallRealMethod().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMonographs(Mockito.any());
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).updateMonographicSetRecords(Mockito.any(),Mockito.any());
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).updateExceptionRecords(Mockito.any(),Mockito.any());
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).saveCGDUpdatedSummaryReport(Mockito.any());
        Mockito.when(matchingAlgoController.updateMonographCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMonographCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateSerialCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForSerials(batchSize);
        Mockito.when(matchingAlgoController.updateSerialCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateSerialCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void testUpdateSerialCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.when(matchingAlgorithmUpdateCGDService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository()).thenReturn(collectionGroupDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgorithmUtil.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(reportDataDetailsRepository.getCountOfRecordNumForMatchingSerials(RecapConstants.BIB_ID)).thenReturn(new Long(10000));
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).saveCGDUpdatedSummaryReport(Mockito.any());
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doCallRealMethod().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForSerials(batchSize);
        Mockito.when(matchingAlgoController.updateSerialCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateSerialCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateMvmCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMVMs(batchSize);
        Mockito.when(matchingAlgoController.updateMvmCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMvmCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void testUpdateMvmCGDInDB() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.when(matchingAlgorithmUpdateCGDService.getMatchingAlgorithmUtil()).thenReturn(matchingAlgorithmUtil);
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getProducerTemplate()).thenReturn(producerTemplate);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupDetailsRepository()).thenReturn(collectionGroupDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemChangeLogDetailsRepository()).thenReturn(itemChangeLogDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getItemDetailsRepository()).thenReturn(itemDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getJmxHelper()).thenReturn(jmxHelper);
        Mockito.when(matchingAlgorithmUtil.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(reportDataDetailsRepository.getCountOfRecordNumForMatchingMVMs(RecapConstants.BIB_ID)).thenReturn(new Long(10000));
        Mockito.doCallRealMethod().when(matchingAlgorithmUtil).saveCGDUpdatedSummaryReport(Mockito.any());
        Mockito.doCallRealMethod().when(matchingAlgorithmUpdateCGDService).updateCGDProcessForMVMs(batchSize);
        Mockito.when(matchingAlgoController.updateMvmCGDInDB()).thenCallRealMethod();
        String response = matchingAlgoController.updateMvmCGDInDB();
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void updateCGDInSolr() throws Exception {
        Date matchingAlgoDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String matchingAlgoDateString = sdf.format(matchingAlgoDate);
        Date updatedDate = new Date();
        try {
            updatedDate = sdf.parse(matchingAlgoDateString);
        } catch (ParseException e) {
            logger.error("Exception while parsing Date : " + e.getMessage());
        }
        Mockito.when(matchingAlgoController.getMatchingBibItemIndexExecutorService()).thenReturn(matchingBibItemIndexExecutorService);
        Mockito.when(matchingBibItemIndexExecutorService.indexingForMatchingAlgorithm(RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, updatedDate)).thenReturn(1);
        Mockito.when(matchingAlgoController.updateCGDInSolr(matchingAlgoDateString)).thenCallRealMethod();
        String response = matchingAlgoController.updateCGDInSolr(matchingAlgoDateString);
        assertTrue(response.contains(RecapConstants.STATUS_DONE));
    }

    @Test
    public void populateDataForDataDump() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingBibInfoDetailService()).thenReturn(matchingBibInfoDetailService);
        Mockito.when(matchingBibInfoDetailService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(matchingBibInfoDetailService.getBatchSize()).thenReturn(batchSize);
        Mockito.when(reportDetailRepository.getCountByType(Mockito.any())).thenReturn(new Integer(100));
        Mockito.when(reportDetailRepository.getRecordNumByType(Mockito.any(),Mockito.any())).thenReturn(getRecordNumber());
        Mockito.when(reportDataDetailsRepository.getRecordsForMatchingBibInfo(Mockito.any(),Mockito.any())).thenReturn(Arrays.asList(new ReportDataEntity()));
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.populateDataForDataDump()).thenCallRealMethod();
        String response = matchingAlgoController.populateDataForDataDump();
        assertTrue(response.contains(RecapConstants.SUCCESS));
    }

    @Test
    public void testPopulateDataForDataDump() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingBibInfoDetailService()).thenReturn(matchingBibInfoDetailService);
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo()).thenReturn(RecapConstants.SUCCESS);
        Mockito.when(matchingAlgoController.populateDataForDataDump()).thenCallRealMethod();
        String response = matchingAlgoController.populateDataForDataDump();
        assertTrue(response.contains(RecapConstants.SUCCESS));
    }

    @Test
    public void itemCountForSerials() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doNothing().when(matchingAlgorithmUpdateCGDService).getItemsCountForSerialsMatching(batchSize);
        Mockito.when(matchingAlgoController.itemCountForSerials()).thenCallRealMethod();
        String response = matchingAlgoController.itemCountForSerials();
        assertTrue(response.contains("Items Count"));
    }

    @Test
    public void testItemCountForSerials() throws Exception {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderValue("1234");
        int totalPagesCount = 1;
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenReturn(matchingAlgorithmUpdateCGDService);
        Mockito.doCallRealMethod().when(matchingAlgorithmUpdateCGDService).getItemsCountForSerialsMatching(batchSize);
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository()).thenReturn(mockedBibliographicDetailsRepository);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupMap()).thenReturn(collectionGroupMap);
        Mockito.when(matchingAlgorithmUpdateCGDService.getCollectionGroupMap().get(RecapConstants.SHARED_CGD)).thenReturn(1);
        Mockito.when(matchingAlgorithmUpdateCGDService.getBibliographicDetailsRepository().findByBibliographicIdIn(Mockito.any())).thenReturn(Arrays.asList(saveBibSingleHoldingsSingleItem()));
        Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository().getCountOfRecordNumForMatchingSerials(RecapConstants.BIB_ID)).thenReturn(new Long(10000));
        for(int pageNum = 0; pageNum < totalPagesCount + 1; pageNum++) {
            long from = pageNum * Long.valueOf(batchSize);
            Mockito.when(matchingAlgorithmUpdateCGDService.getReportDataDetailsRepository().getReportDataEntityForMatchingSerials(RecapConstants.BIB_ID, from, batchSize)).thenReturn(Arrays.asList(reportDataEntity));
        }
        Mockito.when(matchingAlgoController.itemCountForSerials()).thenCallRealMethod();
        String response = matchingAlgoController.itemCountForSerials();
        assertTrue(response.contains("Items Count"));
    }

    @Test
    public void checkGetterServices() throws Exception {
        Mockito.when(matchingAlgoController.getMatchingBibInfoDetailService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgorithmHelperService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getReportGenerator()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingBibItemIndexExecutorService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgorithmUpdateCGDService()).thenCallRealMethod();
        Mockito.when(matchingAlgoController.getMatchingAlgoBatchSize()).thenCallRealMethod();
        assertNotEquals(matchingAlgorithmUpdateCGDService, matchingAlgoController.getMatchingAlgorithmUpdateCGDService());
        assertNotEquals(matchingAlgorithmHelperService, matchingAlgoController.getMatchingAlgorithmHelperService());
        assertNotEquals(matchingBibInfoDetailService, matchingAlgoController.getMatchingBibInfoDetailService());
        assertNotEquals(String.valueOf(batchSize), matchingAlgoController.getMatchingAlgoBatchSize());
        assertNotEquals(matchingBibItemIndexExecutorService, matchingAlgoController.getMatchingBibItemIndexExecutorService());
        assertNotEquals(reportGenerator, matchingAlgoController.getReportGenerator());
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

    public Page<Integer> getRecordNumber(){
        Page<Integer> recordNumber = new Page<Integer>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <S> Page<S> map(Converter<? super Integer, ? extends S> converter) {
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
            public List<Integer> getContent() {
                return new ArrayList<>(12);
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
            public Iterator<Integer> iterator() {
                return null;
            }
        };
        return recordNumber;

    }

}