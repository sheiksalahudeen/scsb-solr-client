package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ReportDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by angelind on 9/1/17.
 */
public class ReportDataDetailsRepositoryUT extends BaseTestCase{

    @Autowired
    ReportDataDetailsRepository reportDataDetailsRepository;

    @Test
    public void getCountOfRecordNumForMatchingMonographTest() throws Exception {
        long countOfRecordNumForMatchingMonograph = reportDataDetailsRepository.getCountOfRecordNumForMatchingMonograph("BibId");
        assertTrue(countOfRecordNumForMatchingMonograph > 0);
    }

    @Test
    public void getReportDataEntityForMatchingMonographsTest() throws Exception {
        List<ReportDataEntity> reportDataEntities = reportDataDetailsRepository.getReportDataEntityForMatchingMonographs("BibId", 0, 100);
        assertNotNull(reportDataEntities);
    }

}