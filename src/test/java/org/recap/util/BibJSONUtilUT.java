package org.recap.util;


import junit.framework.TestCase;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by premkb on 1/8/16.
 */
public class BibJSONUtilUT extends BaseTestCase{

    private String bibContent = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n"+
            "                <record>\n"+
            "                    <controlfield tag=\"001\">NYPG002000036-B</controlfield>\n"+
            "                    <controlfield tag=\"005\">20001116192424.2</controlfield>\n"+
            "                    <controlfield tag=\"008\">850225r19731907nyu b 001 0 ara</controlfield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n"+
            "                        <subfield code=\"a\">   77173005  </subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n"+
            "                        <subfield code=\"c\">NN</subfield>\n"+
            "                        <subfield code=\"d\">NN</subfield>\n"+
            "                        <subfield code=\"d\">CStRLIN</subfield>\n"+
            "                        <subfield code=\"d\">WaOLN</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n"+
            "                        <subfield code=\"a\">ff-----</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n"+
            "                        <subfield code=\"a\">DS36.6</subfield>\n"+
            "                        <subfield code=\"b\">.I26 1973</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"0\" ind2=\"0\" tag=\"082\">\n"+
            "                        <subfield code=\"a\">910.031/767</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n"+
            "                        <subfield code=\"a\">Ibn Jubayr, MuhÌ£ammad ibn AhÌ£mad,</subfield>\n"+
            "                        <subfield code=\"d\">1145-1217.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"1\" ind2=\"0\" tag=\"245\">\n"+
            "                        <subfield code=\"a\">RihÌ£lat</subfield>\n"+
            "                        <subfield code=\"b\">AbÄ« al-Husayn Muhammad ibn Ahmad ibn Jubayr al-KinÄ\u0081nÄ« al-AndalusÄ«\n"+
            "                            al-BalinsÄ«.\n"+
            "                        </subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"250\">\n"+
            "                        <subfield code=\"a\">2d ed.</subfield>\n"+
            "                        <subfield code=\"b\">rev. by M. J. de Goeje and printed for the Trustees of the \"E. J. W. Gibb\n"+
            "                            memorial\"\n"+
            "                        </subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n"+
            "                        <subfield code=\"a\">[New York,</subfield>\n"+
            "                        <subfield code=\"b\">AMS Press,</subfield>\n"+
            "                        <subfield code=\"c\">1973] 1907.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n"+
            "                        <subfield code=\"a\">363, 53 p.</subfield>\n"+
            "                        <subfield code=\"c\">23 cm.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"500\">\n"+
            "                        <subfield code=\"a\">Added t.p.: The travels of Ibn Jubayr. Edited from a ms. in the University\n"+
            "                            Library of Leyden by William Wright.\n"+
            "                        </subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"500\">\n"+
            "                        <subfield code=\"a\">Original ed. issued as v. 5 of \"E.J.W. Gibb memorial\" series.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n"+
            "                        <subfield code=\"a\">Includes bibliographical references and index.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n"+
            "                        <subfield code=\"a\">Islamic Empire</subfield>\n"+
            "                        <subfield code=\"x\">Description and travel.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n"+
            "                        <subfield code=\"a\">Wright, William,</subfield>\n"+
            "                        <subfield code=\"d\">1830-1889.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"1\" ind2=\" \" tag=\"700\">\n"+
            "                        <subfield code=\"a\">Goeje, M. J. de</subfield>\n"+
            "                        <subfield code=\"q\">(Michael Jan),</subfield>\n"+
            "                        <subfield code=\"d\">1836-1909.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\"0\" ind2=\" \" tag=\"740\">\n"+
            "                        <subfield code=\"a\">Travels of Ibn Jubayr.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\"0\" tag=\"830\">\n"+
            "                        <subfield code=\"a\">\"E.J.W. Gibb memorial\" series ;</subfield>\n"+
            "                        <subfield code=\"v\">v.5.</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n"+
            "                        <subfield code=\"a\">.b100006279</subfield>\n"+
            "                        <subfield code=\"c\">m</subfield>\n"+
            "                        <subfield code=\"d\">a</subfield>\n"+
            "                        <subfield code=\"e\">-</subfield>\n"+
            "                        <subfield code=\"f\">ara</subfield>\n"+
            "                        <subfield code=\"g\">nyu</subfield>\n"+
            "                        <subfield code=\"h\">0</subfield>\n"+
            "                        <subfield code=\"i\">3</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n"+
            "                        <subfield code=\"h\">*OAC (\"E. J. W. Gibb memorial\" series. v. 5)</subfield>\n"+
            "                    </datafield>\n"+
            "                    <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n"+
            "                        <subfield code=\"h\">*OFV 87-659</subfield>\n"+
            "                    </datafield>\n"+
            "                    <leader>01814cam a2200409 450000</leader>\n"+
            "                </record>\n"+
            "            </collection>";

    private String holdingContent = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "            <record>\n" +
            "              <datafield tag=\"852\" ind1=\"0\" ind2=\"1\">\n" +
            "                <subfield code=\"b\">off,che</subfield>\n" +
            "                <subfield code=\"h\">TA434 .S15</subfield>\n" +
            "              </datafield>\n" +
            "              <datafield tag=\"866\" ind1=\"0\" ind2=\"0\">\n" +
            "                <subfield code=\"a\">v.1-16         </subfield>\n" +
            "              </datafield>\n" +
            "            </record>\n" +
            "          </collection>";
    @Test
    public void generateBibAndItemsForIndex()throws Exception {
        Random random = new Random();

        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(bibContent.getBytes());
        bibliographicEntity.setOwningInstitutionId(1);
        String owningInstitutionBibId = String.valueOf(random.nextInt());
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setLastUpdatedBy("tst");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(holdingContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setCreatedBy("etl");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setLastUpdatedBy("etl");
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("etl");
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setLastUpdatedBy("etl");
        String barcode = "1234";
        itemEntity.setBarcode(barcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("1");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntity(holdingsEntity);


        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntities.add(bibliographicEntity);
        itemEntity.setBibliographicEntities(bibliographicEntities);

        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        Map<String, List> bibItemMap = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity);
        assertNotNull(bibItemMap);
        List bibs = bibItemMap.get("Bib");
        List items = bibItemMap.get("Item");
        assertNotNull(bibs);
        Bib bib = (Bib)bibs.get(0);
        assertEquals("^RihÌ£lat",bib.getTitleStartsWith());
        assertEquals(bibs.size(), 1);
        assertNotNull(items);
        assertEquals(items.size(), 1);
    }

    @Test
    public void testLccnTrimValue() throws Exception {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);
        String lccnValue = bibJSONUtil.getLCCNValue(marcRecord);
        assertEquals(lccnValue, "77173005");
    }

    @Test
    public void testTitleDisplayValue() throws Exception {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);
        String titleDisplay = bibJSONUtil.getTitleDisplay(marcRecord);
        assertEquals(titleDisplay, "RihÌ£lat");
    }

    @Test
    public void testAuthorDisplayValue() throws Exception {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);
        String authorDisplayValue = bibJSONUtil.getAuthorDisplayValue(marcRecord);
        assertEquals(authorDisplayValue, "Ibn Jubayr, MuhÌ£ammad ibn AhÌ£mad,");
    }

    @Test
    public void testAuthorSearchValue() throws Exception {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        List<Record> records = bibJSONUtil.convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);
        List<String> authorSearchValue = bibJSONUtil.getAuthorSearchValue(marcRecord);
        assertNotNull(authorSearchValue);
        assertEquals(authorSearchValue.size(), 4);
        assertTrue(authorSearchValue.contains("Ibn Jubayr, MuhÌ£ammad ibn AhÌ£mad,"));
        assertTrue(authorSearchValue.contains("Wright, William,"));
        assertTrue(authorSearchValue.contains("Travels of Ibn Jubayr."));
        assertTrue(authorSearchValue.contains("Travels of Ibn Jubayr."));
    }
}
