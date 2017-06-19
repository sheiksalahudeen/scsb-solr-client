package org.recap.converter;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.jaxb.JAXBHandler;
import org.recap.model.jaxb.marc.BibRecords;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 15/12/16.
 */
public class SCSBToBibEntityConverterUT extends BaseTestCase{

    @Autowired
    private SCSBToBibEntityConverter scsbToBibEntityConverter;

    private String scsbXmlContent = "<bibRecords>\n" +
            "    <bibRecord>\n" +
            "        <bib>\n" +
            "            <owningInstitutionId>NYPL</owningInstitutionId>\n" +
            "            <owningInstitutionBibId>.b100000186</owningInstitutionBibId>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <controlfield tag=\"001\">NYPG001000008-B</controlfield>\n" +
            "                    <controlfield tag=\"005\">20001116192418.8</controlfield>\n" +
            "                    <controlfield tag=\"008\">841106s1975 le b 000 0 arax cam i</controlfield>\n" +
            "                    <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "                        <subfield code=\"a\">Bashsh.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"952\">\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "                        <subfield code=\"a\">Women</subfield>\n" +
            "                        <subfield code=\"z\">Lebanon.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
            "                        <subfield code=\"a\">al-Tah 1.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "                        <subfield code=\"a\">78970449</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "                        <subfield code=\"a\">Includes bibliographical references.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"546\">\n" +
            "                        <subfield code=\"a\">In Arabic.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "                        <subfield code=\"a\">Bayr:</subfield>\n" +
            "                        <subfield code=\"b\">Dr al-Tah,</subfield>\n" +
            "                        <subfield code=\"c\">1975.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"952\">\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "                        <subfield code=\"a\">68 p. ;</subfield>\n" +
            "                        <subfield code=\"c\">20 cm.</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"1\" ind2=\"3\" tag=\"245\">\n" +
            "                        <subfield code=\"a\">al-Marah al-Lubnyah :</subfield>\n" +
            "                        <subfield code=\"b\">wwa-qad/</subfield>\n" +
            "                        <subfield code=\"c\">NajlBashsh</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"959\">\n" +
            "                        <subfield code=\"a\">.b10000197</subfield>\n" +
            "                        <subfield code=\"b\">07-18-08</subfield>\n" +
            "                        <subfield code=\"c\">07-29-91</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "                        <subfield code=\"c\">NN</subfield>\n" +
            "                        <subfield code=\"d\">NN</subfield>\n" +
            "                        <subfield code=\"d\">WaOLN</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
            "                        <subfield code=\"a\">a-le---</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "                        <subfield code=\"a\">HQ1728</subfield>\n" +
            "                        <subfield code=\"b\">.B37</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"908\">\n" +
            "                        <subfield code=\"a\">HQ1728</subfield>\n" +
            "                        <subfield code=\"b\">.B37</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"997\">\n" +
            "                        <subfield code=\"a\">ho</subfield>\n" +
            "                        <subfield code=\"b\">12-15-00</subfield>\n" +
            "                        <subfield code=\"c\">m</subfield>\n" +
            "                        <subfield code=\"d\">a</subfield>\n" +
            "                        <subfield code=\"e\">-</subfield>\n" +
            "                        <subfield code=\"f\">ara</subfield>\n" +
            "                        <subfield code=\"g\">le</subfield>\n" +
            "                        <subfield code=\"h\">3</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"907\">\n" +
            "                        <subfield code=\"a\">.b100000186</subfield>\n" +
            "                    </datafield>\n" +
            "                    <leader>00777cam a2200229 i 4500</leader>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "    </bib>\n" +
            "    <holdings>\n" +
            "        <holding>\n" +
            "            <owningInstitutionHoldingsId/>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <datafield ind1=\"8\" ind2=\" \" tag=\"852\">\n" +
            "                        <subfield code=\"b\">rcma2</subfield>\n" +
            "                        <subfield code=\"h\">*OFX 84-1995</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"866\">\n" +
            "                        <subfield code=\"a\"/>\n" +
            "                    </datafield>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "        <items>\n" +
            "            <content>\n" +
            "                <collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "                <record>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"876\">\n" +
            "                        <subfield code=\"p\">33433002031718</subfield>\n" +
            "                        <subfield code=\"h\">In Library Use</subfield>\n" +
            "                        <subfield code=\"a\">.i100000046</subfield>\n" +
            "                        <subfield code=\"j\">Available</subfield>\n" +
            "                        <subfield code=\"t\">1</subfield>\n" +
            "                    </datafield>\n" +
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"900\">\n" +
            "                        <subfield code=\"a\">Shared</subfield>\n" +
            "                        <subfield code=\"b\">NA</subfield>\n" +
            "                    </datafield>\n" +
            "                </record>\n" +
            "            </collection>\n" +
            "        </content>\n" +
            "    </items>\n" +
            "</holding>\n" +
            "</holdings>\n" +
            "</bibRecord>\n" +
            "</bibRecords>\n";
    @Test
    public void convert() throws Exception {
        BibRecords bibRecords = (BibRecords) JAXBHandler.getInstance().unmarshal(scsbXmlContent, BibRecords.class);
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("NA");
        accessionRequest.setItemBarcode("33433002031718");
        Map map = scsbToBibEntityConverter.convert(bibRecords.getBibRecordList().get(0), "NYPL",accessionRequest);
        assertNotNull(map);
        BibliographicEntity bibliographicEntity = (BibliographicEntity) map.get("bibliographicEntity");
        assertNotNull(bibliographicEntity);
        List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
        assertNotNull(holdingsEntities);
        assertTrue(holdingsEntities.size() == 1);
        List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
        assertNotNull(itemEntities);
        assertTrue(itemEntities.size() == 1);
    }
}
