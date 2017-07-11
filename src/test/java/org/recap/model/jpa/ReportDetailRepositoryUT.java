package org.recap.model.jpa;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by SheikS on 8/8/2016.
 */
public class ReportDetailRepositoryUT extends BaseTestCase {

    @Autowired
    private ReportDetailRepository reportDetailRepository;

    @Mock
    ReportDetailRepository getReportDetailRepository;

    @Autowired
    DateUtil dateUtil;

    @Test
    public void testSaveReportEntity() {

        ReportEntity savedReportEntity = saveReportEntity();

        assertNotNull(savedReportEntity);
        assertNotNull(savedReportEntity.getRecordNumber());
        List<ReportDataEntity> savedReportDataEntities = savedReportEntity.getReportDataEntities();
        for (Iterator<ReportDataEntity> iterator = savedReportDataEntities.iterator(); iterator.hasNext(); ) {
            ReportDataEntity savedReportDataEntity = iterator.next();
            System.out.println(savedReportDataEntity.getHeaderName() + " : " + savedReportDataEntity.getHeaderValue());
        }
    }

    @Test
    public void saveAndFindReportEntity() {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileName(reportEntity.getFileName());

        assertNotNull(reportEntities);
        ReportEntity reportEntity1 = reportEntities.get(0);
        assertNotNull(reportEntity1);
        assertNotNull(reportEntity1.getReportDataEntities());
    }

    @Test
    public void findAndUpdateReportEntity() {
        ReportEntity savedReportEntity = saveReportEntity();
        assertNotNull(savedReportEntity.getRecordNumber());
        ReportEntity entity = reportDetailRepository.findOne(savedReportEntity.getRecordNumber());
        assertNotNull(entity);
        ReportDataEntity reportDataEntity = entity.getReportDataEntities().get(0);
        assertNotNull(reportDataEntity);
        entity.setType("Test");
        ReportEntity savedEntity = reportDetailRepository.save(entity);
        assertNotNull(savedEntity);
        assertEquals(savedEntity.getRecordNumber(), entity.getRecordNumber());
        ReportDataEntity dataEntity = savedEntity.getReportDataEntities().get(0);
        assertNotNull(dataEntity);
        assertEquals(reportDataEntity.getReportDataId(), dataEntity.getReportDataId());
    }

    private ReportEntity saveReportEntity() {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.INITIAL_MATCHING_OPERATION_TYPE);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.MULTI_MATCH);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);

        ReportDataEntity reportDataEntity1 = new ReportDataEntity();
        reportDataEntity1.setHeaderName(RecapConstants.BIB_ID);
        reportDataEntity1.setHeaderValue("1");
        reportDataEntities.add(reportDataEntity1);

        ReportDataEntity reportDataEntity2 = new ReportDataEntity();
        reportDataEntity2.setHeaderName("InstitutionId");
        reportDataEntity2.setHeaderValue("PUL");
        reportDataEntities.add(reportDataEntity2);

        ReportDataEntity reportDataEntity3 = new ReportDataEntity();
        reportDataEntity3.setHeaderName("Barcode");
        reportDataEntity3.setHeaderValue("103");
        reportDataEntities.add(reportDataEntity3);

        ReportDataEntity reportDataEntity4 = new ReportDataEntity();
        reportDataEntity4.setHeaderName("Oclc");
        reportDataEntity4.setHeaderValue("213654");
        reportDataEntities.add(reportDataEntity4);

        ReportDataEntity reportDataEntity5 = new ReportDataEntity();
        reportDataEntity5.setHeaderName("UseRestrictions");
        reportDataEntity5.setHeaderValue("In Library Use");
        reportDataEntities.add(reportDataEntity5);

        ReportDataEntity reportDataEntity6 = new ReportDataEntity();
        reportDataEntity6.setHeaderName("SummaryHoldings");
        reportDataEntity6.setHeaderValue("no.1 18292938");
        reportDataEntities.add(reportDataEntity6);

        ReportDataEntity reportDataEntity7 = new ReportDataEntity();
        reportDataEntity7.setHeaderName("MaterialType");
        reportDataEntity7.setHeaderValue("Monograph,Monograph");
        reportDataEntities.add(reportDataEntity7);

        reportEntity.setReportDataEntities(reportDataEntities);

        return reportDetailRepository.save(reportEntity);
    }

    @Test
    public void testFindByFileAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndDateRange(reportEntity.getFileName(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndType() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndType(reportEntity.getFileName(), reportEntity.getType());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

        List<ReportEntity> reportEntities = reportDetailRepository.findByTypeAndDateRange(reportEntity.getType(), simpleDateFormat.format(from), simpleDateFormat.format(to));
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndTypeAndDateRange(reportEntity.getFileName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void findByFileLikeAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileLikeAndTypeAndDateRange("%Initial%", reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileNameAndInstitutionName() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndInstitutionName(reportEntity.getFileName(), reportEntity.getInstitutionName());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileNameAndInstitutionNameAndType() throws Exception {
        ReportEntity reportEntity = saveReportEntity();

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileNameAndInstitutionNameAndType(reportEntity.getFileName(), reportEntity.getInstitutionName(), reportEntity.getType());
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByFileAndInstitutionAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        List<ReportEntity> reportEntities = reportDetailRepository.findByFileAndInstitutionAndTypeAndDateRange(reportEntity.getFileName(), reportEntity.getInstitutionName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByInstitutionAndTypeAndDateRange() throws Exception {
        ReportEntity reportEntity = saveReportEntity();
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());

        List<ReportEntity> reportEntities = reportDetailRepository.findByInstitutionAndTypeAndDateRange(reportEntity.getInstitutionName(), reportEntity.getType(), from, to);
        assertNotNull(reportEntities);
        assertNotNull(reportEntities.get(0));
    }

    @Test
    public void testFindByRecordNumberIn() throws Exception {
        ArrayList<ReportEntity> reportEntities = new ArrayList<>();
        reportEntities.add(new ReportEntity());
        Mockito.when(getReportDetailRepository.findByRecordNumberIn(Arrays.asList(1, 2, 3, 4, 5))).thenReturn(reportEntities);
        List<ReportEntity> byRecordNumberIn = getReportDetailRepository.findByRecordNumberIn(Arrays.asList(1, 2, 3, 4, 5));
        assertNotNull(byRecordNumberIn);
        assertTrue(byRecordNumberIn.size() >= 1);
    }

    @Test
    public void getCountByType(){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("OCLC,ISBN");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType(RecapConstants.MULTI_MATCH);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        reportDetailRepository.saveAndFlush(reportEntity);

        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.MULTI_MATCH);
        Integer matchingBibCount = reportDetailRepository.getCountByType(typeList);
        assertNotNull(matchingBibCount);
        assertTrue(matchingBibCount > 0);
    }

    @Test
    public void getRecordNumByType(){
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        Page<Integer> recordNumbers = reportDetailRepository.getRecordNumByType(new PageRequest(0, 10),typeList);
        List<Integer> recordNumList = recordNumbers.getContent();
        assertNotNull(recordNumList);
    }

    @Test
    public void getCountByTypeAndFileNameAndDateRange() {
        ReportEntity reportEntity = saveReportEntity();
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());
        Integer countByTypeAndDateRange = reportDetailRepository.getCountByTypeAndFileNameAndDateRange(typeList, RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, from, to);
        assertNotNull(countByTypeAndDateRange);
        assertTrue(countByTypeAndDateRange > 0);
    }

    @Test
    public void getRecordNumByTypeAndDateRange() {
        ReportEntity reportEntity = saveReportEntity();
        List<String> typeList = new ArrayList<>();
        typeList.add(RecapConstants.SINGLE_MATCH);
        typeList.add(RecapConstants.MULTI_MATCH);
        Date from = dateUtil.getFromDate(reportEntity.getCreatedDate());
        Date to = dateUtil.getToDate(reportEntity.getCreatedDate());
        Page<Integer> recordNumByTypeAndDateRange = reportDetailRepository.getRecordNumByTypeAndFileNameAndDateRange(new PageRequest(0, 10), typeList, RecapConstants.INITIAL_MATCHING_OPERATION_TYPE, from, to);
        assertNotNull(recordNumByTypeAndDateRange);
        assertNotNull(recordNumByTypeAndDateRange.getContent());
        assertTrue(recordNumByTypeAndDateRange.getTotalElements() > 0);
    }
}