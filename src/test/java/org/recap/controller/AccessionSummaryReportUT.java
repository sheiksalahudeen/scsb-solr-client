package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.SolrIndexService;
import org.recap.service.partnerservice.PrincetonService;
import org.recap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 23/11/16.
 */
public class AccessionSummaryReportUT extends BaseTestCase{

    @Mock
    AccessionService accessionService;

    @Mock
    RestTemplate mockRestTemplate;

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    SolrIndexService solrIndexService;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    PrincetonService princetonService;

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;


    @Value("${scsb.collection.report.directory}")
    String reportDirectory;

    @Autowired
    ReportGenerator reportGenerator;

    @Autowired
    DateUtil dateUtil;


    @Test
    public void testSuccessfullAccessionAndReport() throws Exception{
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101058378587");
        accessionRequestList.add(accessionRequest);

        List<AccessionResponse> accessionResponseList = new ArrayList<>();
        AccessionResponse accessionResponse = new AccessionResponse();
        accessionResponse.setItemBarcode("32101058378587");
        accessionResponse.setMessage(RecapConstants.SUCCESS);
        accessionResponseList.add(accessionResponse);
        Mockito.when(accessionService.processRequest(accessionRequestList)).thenReturn(accessionResponseList);
        List<AccessionResponse> accessionResponses  = accessionService.processRequest(accessionRequestList);
        assertNotNull(accessionResponses);
        assertEquals(RecapConstants.SUCCESS,accessionResponses.get(0).getMessage());
        saveReportEntity();
        String generatedReportFileName = reportGenerator.generateReport("Accession_Report","PUL","Accession_Summary_Report","FileSystem",dateUtil.getFromDate(new Date()), dateUtil.getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    private void saveReportEntity() {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName("Accession_Report");
        reportEntity.setInstitutionName("PUL");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setType("Accession_Summary_Report");

        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.BIB_SUCCESS_COUNT);
        reportDataEntity.setHeaderValue("1");

        reportEntity.addAll(Arrays.asList(reportDataEntity));
        reportDetailRepository.save(reportEntity);
    }

}
