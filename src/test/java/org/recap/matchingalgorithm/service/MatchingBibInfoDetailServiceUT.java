package org.recap.matchingalgorithm.service;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingBibInfoDetail;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.repository.jpa.MatchingBibInfoDetailRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by premkb on 29/1/17.
 */
public class MatchingBibInfoDetailServiceUT extends BaseTestCase {

    @Mock
    private MatchingBibInfoDetailService matchingBibInfoDetailService;

    @Mock
    private ReportDetailRepository reportDetailRepository;

    @Mock
    private MatchingBibInfoDetailRepository matchingBibInfoDetailRepository;

    @Mock
    private ReportDataDetailsRepository reportDataDetailsRepository;

    @Value("${matching.algorithm.bibinfo.batchsize}")
    private Integer batchSize;


    @Test
    public void populateMatchingBibInfo(){
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        List<String> headerNameList = new ArrayList<>();
        headerNameList.add(RecapConstants.BIB_ID);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION);
        headerNameList.add(RecapConstants.OWNING_INSTITUTION_BIB_ID);
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setRecordNum("1");
        reportDataEntity.setHeaderName("BibId");
        reportDataEntity.setHeaderValue("1");
        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setRecordNum("1");
        reportDataEntity1.setHeaderName("OwningInstitution");
        reportDataEntity1.setHeaderValue("1");
        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setRecordNum("1");
        reportDataEntity2.setHeaderName("OwningInstitutionBibId");
        reportDataEntity2.setHeaderValue("Ad4654564");
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        reportDataEntityList.add(reportDataEntity);
        reportDataEntityList.add(reportDataEntity1);
        reportDataEntityList.add(reportDataEntity2);
        MatchingBibInfoDetail matchingBibInfoDetail = new MatchingBibInfoDetail();
        matchingBibInfoDetail.setBibId("1234");
        Date fromDate = new Date();
        Date toDate = new Date();
        int pageNum = 0;
        int count = 0;
        int matchingCount = batchSize;
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(matchingBibInfoDetailService.getReportDataDetailsRepository()).thenReturn(reportDataDetailsRepository);
        Mockito.when(matchingBibInfoDetailService.getMatchingBibInfoDetailRepository()).thenReturn(matchingBibInfoDetailRepository);
        Mockito.when(matchingBibInfoDetailService.getBatchSize()).thenReturn(batchSize);
        Mockito.when(matchingBibInfoDetailService.getPageCount(matchingCount,batchSize)).thenCallRealMethod();
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository().getCountByType(typeList)).thenReturn(batchSize);
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository().getCountByTypeAndFileNameAndDateRange(typeList, RecapConstants.ONGOING_MATCHING_ALGORITHM, fromDate, toDate)).thenReturn(batchSize);
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository().getRecordNumByTypeAndFileNameAndDateRange(new PageRequest(pageNum, batchSize), typeList, RecapConstants.ONGOING_MATCHING_ALGORITHM, fromDate, toDate)).thenReturn(getRecordNumber());
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository().getRecordNumByType(new PageRequest(count, batchSize),typeList)).thenReturn(getRecordNumber());
        Mockito.when(matchingBibInfoDetailService.getReportDataDetailsRepository().getRecordsForMatchingBibInfo(Mockito.any(),Mockito.any())).thenReturn(reportDataEntityList);
        Mockito.when(matchingBibInfoDetailService.getMatchingBibInfoDetailRepository().findRecordNumByBibIds(Mockito.any())).thenReturn(new ArrayList<Integer>(1));
        Mockito.when(matchingBibInfoDetailService.getMatchingBibInfoDetailRepository().findByRecordNumIn(Mockito.any())).thenReturn(Arrays.asList(matchingBibInfoDetail));
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo(fromDate,toDate)).thenCallRealMethod();
        String respone  = matchingBibInfoDetailService.populateMatchingBibInfo(fromDate,toDate);
        assertNotNull(respone);
        assertEquals(respone,"Success");
        Mockito.when(matchingBibInfoDetailService.populateMatchingBibInfo()).thenCallRealMethod();
        String response = matchingBibInfoDetailService.populateMatchingBibInfo();
        assertNotNull(response);
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(matchingBibInfoDetailService.getBatchSize()).thenCallRealMethod();
        Mockito.when(matchingBibInfoDetailService.getMatchingBibInfoDetailRepository()).thenCallRealMethod();
        Mockito.when(matchingBibInfoDetailService.getReportDataDetailsRepository()).thenCallRealMethod();
        Mockito.when(matchingBibInfoDetailService.getReportDetailRepository()).thenCallRealMethod();
        assertNotEquals(batchSize,matchingBibInfoDetailService.getBatchSize());
        assertNotEquals(matchingBibInfoDetailRepository,matchingBibInfoDetailService.getMatchingBibInfoDetailRepository());
        assertNotEquals(reportDataDetailsRepository,matchingBibInfoDetailService.getReportDataDetailsRepository());
        assertNotEquals(reportDetailRepository,matchingBibInfoDetailService.getReportDetailRepository());
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
                return new ArrayList<>(1);
            }

            @Override
            public boolean hasContent() {
                return true;
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
