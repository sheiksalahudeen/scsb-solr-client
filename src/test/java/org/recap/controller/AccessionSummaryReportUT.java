package org.recap.controller;

import org.apache.activemq.util.Handler;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.mockito.*;
import org.recap.BaseTestCase;
import org.recap.converter.MarcToBibEntityConverter;
import org.recap.model.accession.AccessionRequest;
import org.recap.service.accession.AccessionService;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by hemalathas on 23/11/16.
 */
public class AccessionSummaryReportUT extends BaseTestCase{

    @Autowired
    SharedCollectionRestController sharedCollectionRestController;
    @Mock
    AccessionService accessionService;

    @Mock
    MarcUtil mockMarcUtil;

    @Autowired
    MarcUtil marcUtil;


    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    @Mock
    RestTemplate restTemplate;

    String xml = "<collection xmlns='http://www.loc.gov/MARC21/slim' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd'>\n" +
            "</collection>";

    @Before
    public void setUp() throws URISyntaxException, IOException {
        MockitoAnnotations.initMocks(this);
        File bibContentFile = getBibContentFile();
        String bibDataResponse = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(marcUtil.readMarcXml(xml)).thenReturn(readMarcXml(bibDataResponse));
        //RestTemplate restTemplate = new RestTemplate();
        //mockServer = MockRestServiceServer.createServer(restTemplate);
        //when(restTemplate.getForObject(ilsprincetonBibData+"32101095533294", String.class)).thenReturn(bibDataResponse);

    }

    @Test
    public void testFailureRecords() throws Exception{
        String owningInstitution = "PUL";
        String barcode = "32101095533294";
        /*File bibContentFile = getBibContentFile();
        String bibDataResponse = FileUtils.readFileToString(bibContentFile, "UTF-8");
        when(mockMarcUtil.readMarcXml(bibDataResponse)).thenReturn(readMarcXml(bibDataResponse));*/
        String response = accessionService.processRequest(barcode,owningInstitution);
        assertNotNull(response);
    }

    public List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }

    public File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    /*@Test
    public void testFailureRecords(){
        String owningInstitution = "PUL";
        String barcode = "32101095533294";
        mockServer.expect(requestTo(ilsprincetonBibData+barcode))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(marcXml,MediaType.APPLICATION_XML));
        String response = accessionService.processRequest(barcode,owningInstitution);
        assertNotNull(response);

    }*/
}
