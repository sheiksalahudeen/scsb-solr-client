package org.recap.service.accession;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.ReportDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 7/7/17.
 */
public class AccessionDAOUT extends BaseTestCase{

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    AccessionDAO accessionDAO;

    @Test
    public void testAccessionDAO(){
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("Accession");
        reportEntity.setType("Success");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName("PUL");
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName("BibId");
        reportDataEntity.setHeaderValue("123");
        reportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
        ReportEntity savedReportEntity = accessionDAO.saveReportEntity(reportEntity);
        assertNotNull(savedReportEntity);
    }

}