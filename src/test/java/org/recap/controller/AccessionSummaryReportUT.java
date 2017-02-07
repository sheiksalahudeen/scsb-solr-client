package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jpa.ReportEntity;
import org.recap.report.ReportGenerator;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.service.accession.AccessionService;
import org.recap.service.accession.SolrIndexService;
import org.recap.service.partnerservice.PrincetonService;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @PersistenceContext
    private EntityManager entityManager;

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


    String bibMarcRecord = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "    <record>\n" +
            "        <leader>01750cam a2200493 i 4500</leader>\n" +
            "        <controlfield tag=\"001\">9919400</controlfield>\n" +
            "        <controlfield tag=\"005\">20160912115017.0</controlfield>\n" +
            "        <controlfield tag=\"008\">160120t20172016enk b 000 0 eng</controlfield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "            <subfield code=\"a\">2016002744</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"a\">9780415710466</subfield>\n" +
            "            <subfield code=\"q\">hardcover</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"a\">0415710464</subfield>\n" +
            "            <subfield code=\"q\">hardcover</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"z\">9781315867618</subfield>\n" +
            "            <subfield code=\"q\">electronic book</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "            <subfield code=\"z\">1315867613</subfield>\n" +
            "            <subfield code=\"q\">electronic book</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)909322578</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "            <subfield code=\"a\">(OCoLC)ocn909322578</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "            <subfield code=\"a\">DLC</subfield>\n" +
            "            <subfield code=\"e\">rda</subfield>\n" +
            "            <subfield code=\"b\">eng</subfield>\n" +
            "            <subfield code=\"c\">DLC</subfield>\n" +
            "            <subfield code=\"d\">YDX</subfield>\n" +
            "            <subfield code=\"d\">BTCTA</subfield>\n" +
            "            <subfield code=\"d\">BDX</subfield>\n" +
            "            <subfield code=\"d\">OCLCF</subfield>\n" +
            "            <subfield code=\"d\">YDXCP</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"042\">\n" +
            "            <subfield code=\"a\">pcc</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "            <subfield code=\"a\">K236</subfield>\n" +
            "            <subfield code=\"b\">.F38 2017</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"082\">\n" +
            "            <subfield code=\"a\">342.08/5297</subfield>\n" +
            "            <subfield code=\"2\">23</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "            <subfield code=\"a\">Farrar, Salim,</subfield>\n" +
            "            <subfield code=\"e\">author.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\"0\" tag=\"245\">\n" +
            "            <subfield code=\"a\">test1 :</subfield>\n" +
            "            <subfield code=\"b\">test2 /</subfield>\n" +
            "            <subfield code=\"c\">test3.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"1\" tag=\"264\">\n" +
            "            <subfield code=\"a\">Abingdon, Oxon ;</subfield>\n" +
            "            <subfield code=\"a\">New York, NY :</subfield>\n" +
            "            <subfield code=\"b\">Routledge,</subfield>\n" +
            "            <subfield code=\"c\">2017.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"4\" tag=\"264\">\n" +
            "            <subfield code=\"c\">©2016</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "            <subfield code=\"a\">viii, 206 pages ;</subfield>\n" +
            "            <subfield code=\"c\">25 cm</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"336\">\n" +
            "            <subfield code=\"a\">text</subfield>\n" +
            "            <subfield code=\"b\">txt</subfield>\n" +
            "            <subfield code=\"2\">rdacontent</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"337\">\n" +
            "            <subfield code=\"a\">unmediated</subfield>\n" +
            "            <subfield code=\"b\">n</subfield>\n" +
            "            <subfield code=\"2\">rdamedia</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"338\">\n" +
            "            <subfield code=\"a\">volume</subfield>\n" +
            "            <subfield code=\"b\">nc</subfield>\n" +
            "            <subfield code=\"2\">rdacarrier</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "            <subfield code=\"a\">Includes bibliographical references.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Legal polycentricity.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Legal status, laws, etc.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Civil rights.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Common law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Islamic law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Comparative law.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Common law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00869795</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Comparative law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00871350</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Islamic law.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00979949</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Legal polycentricity.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst00995519</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Civil rights.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst01031035</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\"7\" tag=\"650\">\n" +
            "            <subfield code=\"a\">Muslims</subfield>\n" +
            "            <subfield code=\"x\">Legal status, laws, etc.</subfield>\n" +
            "            <subfield code=\"2\">fast</subfield>\n" +
            "            <subfield code=\"0\">(OCoLC)fst01031055</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n" +
            "            <subfield code=\"a\">Krayem, Ghena,</subfield>\n" +
            "            <subfield code=\"e\">author.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"902\">\n" +
            "            <subfield code=\"a\">kl</subfield>\n" +
            "            <subfield code=\"b\">s</subfield>\n" +
            "            <subfield code=\"6\">a</subfield>\n" +
            "            <subfield code=\"7\">m</subfield>\n" +
            "            <subfield code=\"d\">v</subfield>\n" +
            "            <subfield code=\"f\">1</subfield>\n" +
            "            <subfield code=\"e\">20160912</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\" \" ind2=\" \" tag=\"904\">\n" +
            "            <subfield code=\"a\">kl</subfield>\n" +
            "            <subfield code=\"b\">a</subfield>\n" +
            "            <subfield code=\"h\">m</subfield>\n" +
            "            <subfield code=\"c\">b</subfield>\n" +
            "            <subfield code=\"e\">20160912</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\" \" tag=\"852\">\n" +
            "            <subfield code=\"0\">9734816</subfield>\n" +
            "            <subfield code=\"b\">rcppa</subfield>\n" +
            "            <subfield code=\"h\">K236 .F38 2017</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield ind1=\"0\" ind2=\"0\" tag=\"876\">\n" +
            "            <subfield code=\"0\">9734816</subfield>\n" +
            "            <subfield code=\"a\">7453441</subfield>\n" +
            "            <subfield code=\"h\"/>\n" +
            "            <subfield code=\"j\">Not Charged</subfield>\n" +
            "            <subfield code=\"p\">32101095533293</subfield>\n" +
            "            <subfield code=\"t\">0</subfield>\n" +
            "            <subfield code=\"x\">Shared</subfield>\n" +
            "            <subfield code=\"z\">PA</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>";


    @Test
    public void testSuccessfullAccessionAndReport() throws Exception{
        String barcode = "32101058378587";
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101058378587");
        accessionRequestList.add(accessionRequest);
        Mockito.when(mockRestTemplate.getForObject(ilsprincetonBibData + barcode, String.class)).thenReturn(bibMarcRecord);
        Mockito.when(accessionService.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(accessionService.processRequest(accessionRequestList)).thenCallRealMethod();
        Mockito.when(accessionService.updateBibliographicEntity(Mockito.any())).thenCallRealMethod();
        Mockito.when(accessionService.getMarcUtil()).thenReturn(new MarcUtil());
        Mockito.when(accessionService.getMarcToBibEntityConverter()).thenReturn(marcToBibEntityConverter);
        Mockito.when(accessionService.getReportDetailRepository()).thenReturn(reportDetailRepository);
        Mockito.when(accessionService.getSolrIndexService()).thenReturn(solrIndexService);
        Mockito.when(accessionService.getBibliographicDetailsRepository()).thenReturn(bibliographicDetailsRepository);
        Mockito.when(accessionService.getEntityManager()).thenReturn(entityManager);
        Mockito.when(accessionService.getCustomerCodeDetailsRepository()).thenReturn(customerCodeDetailsRepository);
        Mockito.when(accessionService.getInstitutionDetailsRepository()).thenReturn(institutionDetailsRepository);
        Mockito.when(accessionService.getPrincetonService()).thenReturn(princetonService);
        String response = accessionService.processRequest(accessionRequestList);
        assertNotNull(response);
        assertEquals(response, RecapConstants.SUCCESS);

        List<ReportEntity> reportEntityList = reportDetailRepository.findByFileAndDateRange("Accession_Report",getFromDate(new Date()),getToDate(new Date()));
        assertNotNull(reportEntityList);
        String generatedReportFileName = reportGenerator.generateReport("Accession_Report","PUL","Accession_Summary_Report","FileSystem",getFromDate(new Date()), getToDate(new Date()));
        assertNotNull(generatedReportFileName);
    }

    private Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    private Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

}
