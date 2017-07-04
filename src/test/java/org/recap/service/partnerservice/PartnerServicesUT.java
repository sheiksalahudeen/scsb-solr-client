package org.recap.service.partnerservice;

import org.junit.Test;
import org.marc4j.marc.Record;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.service.authorization.NyplOauthTokenApiService;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by peris on 12/23/16.
 */
public class PartnerServicesUT extends BaseTestCase {

    @Mock
    private PrincetonService princetonService;

    @Mock
    private NYPLService nyplService;

    @Autowired
    private MarcUtil marcUtil;

    @Mock
    RestTemplate restTemplate;

    @Value("${ils.princeton.bibdata}")
    private String ilsprincetonBibData;

    @Value("${ils.nypl.bibdata}")
    private String ilsNYPLBibData;

    @Value("${ils.nypl.bibdata.parameter}")
    private String ilsNYPLBibDataParameter;

    @Autowired
    private NyplOauthTokenApiService nyplOauthTokenApiService;


    String bibData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><collection xmlns:marcxml=\"http://www.loc.gov/MARC21/slim\">\n" +
            "    <record>\n" +
            "        <leader>01302cas a2200361 a 4500</leader>\n" +
            "        <controlfield tag=\"001\">202304</controlfield>\n" +
            "        <controlfield tag=\"005\">20160526232735.0</controlfield>\n" +
            "        <controlfield tag=\"008\">830323c19819999iluqx p   gv  0    0eng d</controlfield>\n" +
            "        <datafield tag=\"010\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">   82640039  </subfield>\n" +
            "            <subfield code=\"z\">   81640039 </subfield>\n" +
            "            <subfield code=\"z\">sn 81001329 </subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"022\" ind1=\"0\" ind2=\" \">\n" +
            "            <subfield code=\"a\">0276-9948</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">(OCoLC)7466281</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">(CStRLIN)NJPG83-S372</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"9\">ABB7255TS</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"040\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">NSDP</subfield>\n" +
            "            <subfield code=\"d\">NjP</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"042\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">nsdp</subfield>\n" +
            "            <subfield code=\"a\">lc</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"043\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">n-us-il</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"050\" ind1=\"0\" ind2=\"0\">\n" +
            "            <subfield code=\"a\">K25</subfield>\n" +
            "            <subfield code=\"b\">.N63</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"222\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"a\">University of Illinois law review</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"245\" ind1=\"0\" ind2=\"0\">\n" +
            "            <subfield code=\"a\">University of Illinois law review.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"246\" ind1=\"3\" ind2=\"0\">\n" +
            "            <subfield code=\"a\">Law review</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"260\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">Champaign, IL :</subfield>\n" +
            "            <subfield code=\"b\">University of Illinois at Urbana-Champaign, College of Law,</subfield>\n" +
            "            <subfield code=\"c\">c1981-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"300\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">v. ;</subfield>\n" +
            "            <subfield code=\"c\">27 cm.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"310\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">5 times a year,</subfield>\n" +
            "            <subfield code=\"b\">2001-&lt;2013&gt;</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"321\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">Quarterly,</subfield>\n" +
            "            <subfield code=\"b\">1981-2000</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"362\" ind1=\"0\" ind2=\" \">\n" +
            "            <subfield code=\"a\">Vol. 1981, no. 1-</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"588\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">Title from cover.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"588\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">Latest issue consulted: Vol. 2013, no. 5.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"650\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"a\">Law reviews</subfield>\n" +
            "            <subfield code=\"z\">Illinois.</subfield>\n" +
            "            <subfield code=\"0\">(uri)http://id.loc.gov/authorities/subjects/sh2009129243</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"710\" ind1=\"2\" ind2=\" \">\n" +
            "            <subfield code=\"a\">University of Illinois at Urbana-Champaign.</subfield>\n" +
            "            <subfield code=\"b\">College of Law.</subfield>\n" +
            "            <subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/n50049213</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"780\" ind1=\"0\" ind2=\"0\">\n" +
            "            <subfield code=\"t\">University of Illinois law forum</subfield>\n" +
            "            <subfield code=\"x\">0041-963X</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"998\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">09/09/94</subfield>\n" +
            "            <subfield code=\"s\">9110</subfield>\n" +
            "            <subfield code=\"n\">NjP</subfield>\n" +
            "            <subfield code=\"w\">DCLC82640039S</subfield>\n" +
            "            <subfield code=\"d\">03/23/83</subfield>\n" +
            "            <subfield code=\"c\">DLJ</subfield>\n" +
            "            <subfield code=\"b\">SZF</subfield>\n" +
            "            <subfield code=\"i\">940909</subfield>\n" +
            "            <subfield code=\"l\">NJPG</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"911\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">19940916</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"912\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">19970731060735.0</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"866\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"0\">222420</subfield>\n" +
            "            <subfield code=\"a\">Vol. 1981, no. 1-v. 2013, no. 5</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"866\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"0\">222420</subfield>\n" +
            "            <subfield code=\"z\">LACKS: 2012, no. 1</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"866\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"0\">222420</subfield>\n" +
            "            <subfield code=\"x\">DESIGNATOR: year, no.</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"866\" ind1=\" \" ind2=\"0\">\n" +
            "            <subfield code=\"0\">222420</subfield>\n" +
            "            <subfield code=\"z\">Subscription cancelled with the last issue of 2013</subfield>\n" +
            "        </datafield>\n" +
            "        <datafield tag=\"959\" ind1=\" \" ind2=\" \">\n" +
            "            <subfield code=\"a\">2000-06-13 00:00:00 -0500</subfield>\n" +
            "        </datafield>\n" +
            "    </record>\n" +
            "</collection>\n";


    String scsbXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>CUL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>12040230</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                    <record>\n" +
            "                        <leader>00319cam a2200109 a 4500</leader>\n" +
            "                        <controlfield tag=\"001\">12040230</controlfield>\n" +
            "                        <controlfield tag=\"003\">NNC</controlfield>\n" +
            "                        <controlfield tag=\"005\">20170502100045.0</controlfield>\n" +
            "                        <controlfield tag=\"008\">170502s2011    ie ab    b    001 0 eng d</controlfield>\n" +
            "                        <datafield tag=\"100\" ind1=\"1\" ind2=\" \">\n" +
            "                            <subfield code=\"a\">Lane, Zack.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"245\" ind1=\"1\" ind2=\"0\">\n" +
            "                            <subfield code=\"a\">Monographs and life /</subfield>\n" +
            "                            <subfield code=\"c\">by Zack Lane.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"260\" ind1=\" \" ind2=\" \">\n" +
            "                            <subfield code=\"a\">Derry</subfield>\n" +
            "                            <subfield code=\"b\">Derry University Press,</subfield>\n" +
            "                            <subfield code=\"c\">2013.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"300\" ind1=\" \" ind2=\" \">\n" +
            "                            <subfield code=\"a\">viii, 300 p. :</subfield>\n" +
            "                            <subfield code=\"b\">ill., ports. ;</subfield>\n" +
            "                            <subfield code=\"c\">24 cm.</subfield>\n" +
            "                        </datafield>\n" +
            "                    </record>\n" +
            "                </collection>\n" +
            "            </content>\n" +
            "        </bib>\n" +
            "        <holdings>\n" +
            "            <holding>\n" +
            "                <owningInstitutionHoldingsId>15064227</owningInstitutionHoldingsId>\n" +
            "                <content>\n" +
            "                    <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                        <record>\n" +
            "                            <datafield tag=\"852\" ind1=\"0\" ind2=\"0\">\n" +
            "                                <subfield code=\"b\">off,glx</subfield>\n" +
            "                                <subfield code=\"h\">CD98 .D32</subfield>\n" +
            "                            </datafield>\n" +
            "                        </record>\n" +
            "                    </collection>\n" +
            "                </content>\n" +
            "                <items>\n" +
            "                    <content>\n" +
            "                        <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                            <record>\n" +
            "                                <datafield tag=\"876\" ind1=\"0\" ind2=\"0\">\n" +
            "                                    <subfield code=\"a\">8967952</subfield>\n" +
            "                                    <subfield code=\"h\"> </subfield>\n" +
            "                                    <subfield code=\"p\">CU24049476</subfield>\n" +
            "                                    <subfield code=\"t\">1</subfield>\n" +
            "                                </datafield>\n" +
            "                                <datafield tag=\"900\" ind1=\"0\" ind2=\"0\">\n" +
            "                                    <subfield code=\"a\">Shared</subfield>\n" +
            "                                </datafield>\n" +
            "                            </record>\n" +
            "                        </collection>\n" +
            "                    </content>\n" +
            "                </items>\n" +
            "            </holding>\n" +
            "        </holdings>\n" +
            "    </bibRecord>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>CUL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>12040231</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                    <record>\n" +
            "                        <leader>00331cam a2200109 a 4500</leader>\n" +
            "                        <controlfield tag=\"001\">12040231</controlfield>\n" +
            "                        <controlfield tag=\"003\">NNC</controlfield>\n" +
            "                        <controlfield tag=\"005\">20170502100143.0</controlfield>\n" +
            "                        <controlfield tag=\"008\">170502s2011    ie ab    b    001 0 eng d</controlfield>\n" +
            "                        <datafield tag=\"100\" ind1=\"1\" ind2=\" \">\n" +
            "                            <subfield code=\"a\">Lane, Zack.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"245\" ind1=\"1\" ind2=\"0\">\n" +
            "                            <subfield code=\"a\">Monographs and fate /</subfield>\n" +
            "                            <subfield code=\"c\">by Zack Lane.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"260\" ind1=\" \" ind2=\" \">\n" +
            "                            <subfield code=\"a\">Londonderry</subfield>\n" +
            "                            <subfield code=\"b\">Londonderry University Press,</subfield>\n" +
            "                            <subfield code=\"c\">2014.</subfield>\n" +
            "                        </datafield>\n" +
            "                        <datafield tag=\"300\" ind1=\" \" ind2=\" \">\n" +
            "                            <subfield code=\"a\">viii, 300 p. :</subfield>\n" +
            "                            <subfield code=\"b\">ill., ports. ;</subfield>\n" +
            "                            <subfield code=\"c\">24 cm.</subfield>\n" +
            "                        </datafield>\n" +
            "                    </record>\n" +
            "                </collection>\n" +
            "            </content>\n" +
            "        </bib>\n" +
            "        <holdings>\n" +
            "            <holding>\n" +
            "                <owningInstitutionHoldingsId>15064228</owningInstitutionHoldingsId>\n" +
            "                <content>\n" +
            "                    <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                        <record>\n" +
            "                            <datafield tag=\"852\" ind1=\"0\" ind2=\"0\">\n" +
            "                                <subfield code=\"b\">off,glx</subfield>\n" +
            "                                <subfield code=\"h\">CD98 .D32</subfield>\n" +
            "                            </datafield>\n" +
            "                        </record>\n" +
            "                    </collection>\n" +
            "                </content>\n" +
            "                <items>\n" +
            "                    <content>\n" +
            "                        <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                            <record>\n" +
            "                                <datafield tag=\"876\" ind1=\"0\" ind2=\"0\">\n" +
            "                                    <subfield code=\"a\">8967952</subfield>\n" +
            "                                    <subfield code=\"h\"> </subfield>\n" +
            "                                    <subfield code=\"p\">CU24049476</subfield>\n" +
            "                                    <subfield code=\"t\">1</subfield>\n" +
            "                                </datafield>\n" +
            "                                <datafield tag=\"900\" ind1=\"0\" ind2=\"0\">\n" +
            "                                    <subfield code=\"a\">Shared</subfield>\n" +
            "                                </datafield>\n" +
            "                            </record>\n" +
            "                        </collection>\n" +
            "                    </content>\n" +
            "                </items>\n" +
            "            </holding>\n" +
            "        </holdings>\n" +
            "    </bibRecord>\n" +
            "</bibRecords>";

    @Test
    public void getBibData() throws Exception {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(bibData, HttpStatus.OK);
        String itemBarcode = "32101062128309";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity requestEntity = new HttpEntity(headers);
        Map<String, String> params = new HashMap<>();
        params.put("barcode", itemBarcode);
        Mockito.when(princetonService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(princetonService.getIlsprincetonBibData()).thenReturn(ilsprincetonBibData);
        Mockito.when(restTemplate.exchange(ilsprincetonBibData, HttpMethod.GET, requestEntity, String.class, params)).thenReturn(responseEntity);
        Mockito.when(princetonService.getBibData(itemBarcode)).thenCallRealMethod();
        String bibDataResponse = princetonService.getBibData(itemBarcode);
        assertNotNull(bibDataResponse);
        List<Record> records = marcUtil.readMarcXml(bibDataResponse);
        assertNotNull(records);

        String customerCode = "NA";
        itemBarcode = "33433002031718";
        String authorization = "Bearer " + nyplOauthTokenApiService.generateAccessTokenForNyplApi();
        headers = new HttpHeaders();
        headers.clear();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        headers.set("Authorization", authorization);
        params = new HashMap<>();
        params.clear();
        params.put("barcode", itemBarcode);
        params.put("customercode", customerCode);
        requestEntity = new HttpEntity(headers);
        String url = ilsNYPLBibData + ilsNYPLBibDataParameter;
        responseEntity = new ResponseEntity<String>(scsbXml, HttpStatus.OK);
        Mockito.when(nyplService.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(nyplService.getNyplOauthTokenApiService()).thenReturn(nyplOauthTokenApiService);
        Mockito.when(nyplService.getIlsNYPLBibData()).thenReturn(ilsNYPLBibData);
        Mockito.when(nyplService.getHttpHeaders()).thenReturn(headers);
        Mockito.when(nyplService.getHttpEntity(headers)).thenReturn(requestEntity);
        Mockito.when(nyplService.getIlsNYPLBibDataParameter()).thenReturn(ilsNYPLBibDataParameter);
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params)).thenReturn(responseEntity);
        Mockito.when(nyplService.getBibData(itemBarcode, customerCode)).thenCallRealMethod();
        String bibDataResponseFoNYPL = nyplService.getBibData(itemBarcode, customerCode);
        assertNotNull(bibDataResponseFoNYPL);
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(bibDataResponseFoNYPL, BibRecords.class);
        assertNotNull(bibRecords);
    }
}
