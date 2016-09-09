package org.recap.util;

import org.junit.Test;
import org.marc4j.marc.Record;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class MarcUtilUT {

    private String marcXML = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
            "          <record>\n" +
            "            <controlfield tag=\"001\">NYPG001000011-B</controlfield>\n" +
            "            <controlfield tag=\"005\">20001116192418.8</controlfield>\n" +
            "            <controlfield tag=\"008\">841106s1976    le       b    000 0 arax </controlfield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "              <subfield code=\"a\">79971032</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "              <subfield code=\"a\">NNSZ00100011</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "              <subfield code=\"a\">(WaOLN)nyp0200023</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"040\">\n" +
            "              <subfield code=\"c\">NN</subfield>\n" +
            "              <subfield code=\"d\">NN</subfield>\n" +
            "              <subfield code=\"d\">WaOLN</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"043\">\n" +
            "              <subfield code=\"a\">a-ba---</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\"0\" ind2=\"0\" tag=\"050\">\n" +
            "              <subfield code=\"a\">DS247.B28</subfield>\n" +
            "              <subfield code=\"b\">R85</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "              <subfield code=\"a\">RumayhÌ£Ä«, MuhÌ£ammad GhÄ\u0081nim.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\"1\" ind2=\"3\" tag=\"245\">\n" +
            "              <subfield code=\"a\">al-BahÌ£rayn :</subfield>\n" +
            "              <subfield code=\"b\">mushkilÄ\u0081t al-taghyÄ«r al-siyÄ\u0081sÄ« wa-al-ijtimÄ\u0081Ê»Ä« /</subfield>\n" +
            "              <subfield code=\"c\">MuhÌ£ammad al-RumayhÌ£Ä«.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
            "              <subfield code=\"a\">al-TÌ£abÊ»ah 1.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "              <subfield code=\"a\">[BayrÅ«t] :</subfield>\n" +
            "              <subfield code=\"b\">DÄ\u0081r Ibn KhaldÅ«n,</subfield>\n" +
            "              <subfield code=\"c\">1976.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "              <subfield code=\"a\">264 p. ;</subfield>\n" +
            "              <subfield code=\"c\">24 cm.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "              <subfield code=\"a\">Includes bibliographies.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"546\">\n" +
            "              <subfield code=\"a\">In Arabic.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
            "              <subfield code=\"a\">Bahrain</subfield>\n" +
            "              <subfield code=\"x\">History</subfield>\n" +
            "              <subfield code=\"y\">20th century.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
            "              <subfield code=\"a\">Bahrain</subfield>\n" +
            "              <subfield code=\"x\">Economic conditions.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\"0\" tag=\"651\">\n" +
            "              <subfield code=\"a\">Bahrain</subfield>\n" +
            "              <subfield code=\"x\">Social conditions.</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"907\">\n" +
            "              <subfield code=\"a\">.b100000241</subfield>\n" +
            "              <subfield code=\"c\">m</subfield>\n" +
            "              <subfield code=\"d\">a</subfield>\n" +
            "              <subfield code=\"e\">-</subfield>\n" +
            "              <subfield code=\"f\">ara</subfield>\n" +
            "              <subfield code=\"g\">le </subfield>\n" +
            "              <subfield code=\"h\">3</subfield>\n" +
            "              <subfield code=\"i\">1</subfield>\n" +
            "            </datafield>\n" +
            "            <datafield ind1=\" \" ind2=\" \" tag=\"952\">\n" +
            "              <subfield code=\"h\">*OFK 84-1944</subfield>\n" +
            "            </datafield>\n" +
            "          </record>\n" +
            "        </collection>";

    @Test
    public void getMultiFieldValues() throws  Exception {
        MarcUtil marcUtil = new MarcUtil();

        List<Record> records =
                marcUtil.convertMarcXmlToRecord(marcXML);

        assertNotNull(records);
        assertTrue(records.size() == 1);


        List<String> oThirtyFives = marcUtil.getMultiDataFieldValues(records.get(0), "035", null, null, "a");
        assertEquals(2, oThirtyFives.size());


    }

    @Test
    public void getDataFieldValues() throws  Exception {
        MarcUtil marcUtil = new MarcUtil();

        List<Record> records =
                marcUtil.convertMarcXmlToRecord(marcXML);

        assertNotNull(records);
        assertTrue(records.size() == 1);


        String title = marcUtil.getDataFieldValue(records.get(0), "245", null, null, "a");
        assertEquals("al-BahÌ£rayn :", title);

    }

    @Test
    public void getDataFieldValue(){
        MarcUtil marcUtil = new MarcUtil();

        List<Record> records =
                marcUtil.convertMarcXmlToRecord(marcXML);
        List<Character> subFields = new ArrayList<>();
        subFields.add('a');
        subFields.add('h');

        assertNotNull(records);
        assertTrue(records.size() == 1);
        String fieldVaule = marcUtil.getDataFieldValueStartsWith(records.get(0),"9");
        assertEquals(".b100000241 m a - ara le  3 1 *OFK 84-1944" , fieldVaule);
        String subFieldValue = marcUtil.getDataFieldValueStartsWith(records.get(0),"9",subFields);
        assertEquals(".b100000241 3 *OFK 84-1944",subFieldValue);
    }

    @Test
    public void getControlFieldValues(){
        MarcUtil marcUtil = new MarcUtil();

        List<Record> records =
                marcUtil.convertMarcXmlToRecord(marcXML);

        assertNotNull(records);
        assertTrue(records.size() == 1);
        String controlField = marcUtil.getControlFieldValue(records.get(0),"001");
        assertEquals("NYPG001000011-B" , controlField);


    }



    @Test
    public void math() throws Exception {
        int numThreads = 5;
        int docsPerThread = 1;
        int totalDocs = 15;

        System.out.println(totalDocs % (docsPerThread*numThreads));
        System.out.println(totalDocs / (docsPerThread*numThreads));


    }

    @Test
    public void getSecondIndicatorForDataField() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = marcUtil.convertMarcXmlToRecord(marcXML);
        assertNotNull(records.get(0));
        Integer secondIndicatorForDataField = marcUtil.getSecondIndicatorForDataField(records.get(0), "245");
        assertEquals(secondIndicatorForDataField, new Integer(3));
    }

}