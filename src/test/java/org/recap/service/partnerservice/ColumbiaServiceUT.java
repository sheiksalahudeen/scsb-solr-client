package org.recap.service.partnerservice;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.util.MarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by premkb on 14/3/17.
 */
public class ColumbiaServiceUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(ColumbiaServiceUT.class);

    @Mock
    private ColumbiaService columbiaService;

    @Autowired
    private MarcUtil marcUtil;

    @Mock
    RestTemplate restTemplate;

    @Value("${ils.columbia.bibdata}")
    private String ilsColumbiaBibData;

    String bibData = "<?xml version = \"1.0\" encoding = \"UTF-8\"?>\n" +
            "  <collection xmlns=\"http://www.loc.gov/MARC21/slim\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim\n" +
            "http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "    <record xmlns=\"http://www.loc.gov/MARC21/slim\"\n" +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim\n" +
            "http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "      <leader>     nam  2200457 a 4500</leader>\n" +
            "      <controlfield tag=\"001\">003404590</controlfield>\n" +
            "      <controlfield tag=\"005\">20091106150915.0</controlfield>\n" +
            "      <controlfield tag=\"006\">m        d</controlfield>\n" +
            "      <controlfield tag=\"007\">cr bn|||||||||</controlfield>\n" +
            "      <controlfield tag=\"008\">091028s2008    caua          000 p eng d</controlfield>\n" +
            "      <datafield tag=\"024\" ind1=\"8\" ind2=\" \">\n" +
            "        <subfield code=\"a\">21085</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"024\" ind1=\"8\" ind2=\" \">\n" +
            "        <subfield code=\"a\">3.84</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"024\" ind1=\"8\" ind2=\" \">\n" +
            "        <subfield code=\"a\">R219235</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">(OCoLC)ocn460736493</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"040\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">CUT</subfield>\n" +
            "        <subfield code=\"c\">CUT</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"245\" ind1=\"0\" ind2=\"4\">\n" +
            "        <subfield code=\"a\">The bonny scot</subfield>\n" +
            "        <subfield code=\"h\">[electronic resource] :</subfield>\n" +
            "        <subfield code=\"b\">or, the yielding lass.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"246\" ind1=\"1\" ind2=\" \">\n" +
            "        <subfield code=\"i\">First line:</subfield>\n" +
            "        <subfield code=\"a\">AS I sate at my Spinning=Wheel</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"260\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Santa Barbara, CA :</subfield>\n" +
            "        <subfield code=\"b\">University of California, Santa Barbara, The Early Modern Center,</subfield>\n" +
            "        <subfield code=\"c\">2008.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"516\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Text (XML).</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"538\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">System requirements: Web browser for the XML and HTML files.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"538\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Mode of access: Internet; host: ebba.english.ucsb.edu.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Title from English Broadside Ballad Archive (queried 2008 Nov 07).</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Original Publication Date: c. 1664-1696.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">TEI file encoded by the English Broadside Ballad Archive Editorial Team.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">EMC no. 21085.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Pepys 3.84.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">ESTC no. R219235.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"500\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Reference: Wing B3604[B]; Rollins (2) ?222 (March 1, 1675, ii, 499).</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"534\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"p\">Reproduction of Microfilm :</subfield>\n" +
            "        <subfield code=\"a\">Magdalene College (University of Cambridge).</subfield>\n" +
            "        <subfield code=\"t\">The Pepys ballads</subfield>\n" +
            "        <subfield code=\"c\">Cambridge [England] : The Pepys Library, Magdalene College, 2003.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"536\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">Sponsored by the University of California, Santa Barbara and The Early Modern Center.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"540\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">The University of California makes a claim of copyright only to original contributions made by Early Modern Center participants and other members of the university community. The University of California makes no claim of copyright to the original text. Permission is granted to download, transmit or otherwise reproduce, distribute or display the contributions to this work claimed by The University of California for non-profit educational purposes, provided that this header is included in its entirety. For inquiries about commercial uses, please contact: Early Modern Center - English Department, University of California, Santa Barbara, CA 93105, United States of America, Email: http://ebba.english.ucsb.edu/contact/.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"650\" ind1=\" \" ind2=\"0\">\n" +
            "        <subfield code=\"a\">Ballads, English</subfield>\n" +
            "        <subfield code=\"y\">17th century.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"650\" ind1=\" \" ind2=\"0\">\n" +
            "        <subfield code=\"a\">Broadside</subfield>\n" +
            "        <subfield code=\"y\">17th century.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"655\" ind1=\" \" ind2=\"7\">\n" +
            "        <subfield code=\"a\">Ballads</subfield>\n" +
            "        <subfield code=\"2\">aat</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"710\" ind1=\"2\" ind2=\" \">\n" +
            "        <subfield code=\"a\">University of California, Santa Barbara.</subfield>\n" +
            "        <subfield code=\"b\">Early Modern Center.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"740\" ind1=\"0\" ind2=\" \">\n" +
            "        <subfield code=\"a\">English Broadside Ballad Archive.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"856\" ind1=\"4\" ind2=\"0\">\n" +
            "        <subfield code=\"u\">http://ebba.english.ucsb.edu/ballad/21085/</subfield>\n" +
            "        <subfield code=\"z\">English Broadside Ballad Archive.</subfield>\n" +
            "      </datafield>\n" +
            "      <datafield tag=\"049\" ind1=\" \" ind2=\" \">\n" +
            "        <subfield code=\"a\">CUTM</subfield>\n" +
            "      </datafield>\n" +
            "    </record>\n" +
            "\n" +
            "</collection>  \n";


    @Test
    public void getBibData() {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(bibData, HttpStatus.OK);
        String itemBarcode = "CU54175534";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity requestEntity = new HttpEntity(headers);
        Map<String, String> params = new HashMap<>();
        params.put("barcode", itemBarcode);
        Mockito.when(columbiaService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(columbiaService.getIlsColumbiaBibData()).thenReturn(ilsColumbiaBibData);
        Mockito.when(restTemplate.exchange(ilsColumbiaBibData, HttpMethod.GET, requestEntity, String.class, params)).thenReturn(responseEntity);
        Mockito.when(columbiaService.getBibData(itemBarcode)).thenCallRealMethod();
        String bibDataResponse = columbiaService.getBibData(itemBarcode);
        assertNotNull(bibDataResponse);
        List<Record> records = marcUtil.readMarcXml(bibDataResponse);
        assertNotNull(records);
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(columbiaService.getIlsColumbiaBibData()).thenCallRealMethod();
        Mockito.when(columbiaService.getRestTemplate()).thenCallRealMethod();
        assertNotEquals(ilsColumbiaBibData,columbiaService.getIlsColumbiaBibData());
        assertNotEquals(restTemplate,columbiaService.getRestTemplate());
        assertNotEquals(logger,columbiaService.getLogger());
    }
}
