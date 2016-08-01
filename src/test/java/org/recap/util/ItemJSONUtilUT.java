package org.recap.util;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.solr.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 29/7/16.
 */
public class ItemJSONUtilUT extends BaseTestCase {

    Logger logger = LoggerFactory.getLogger(ItemJSONUtilUT.class);

    @Test
    public void generateItemForIndexFromJsonObject()throws Exception{
        JSONObject itemJSON = new JSONObject();
        itemJSON.put("itemId",1);
        itemJSON.put("bibliographicId",1);
        itemJSON.put("holdingsId",1);
        itemJSON.put("barcode","CU54519993");
        itemJSON.put("availability","Available");
        itemJSON.put("collectionGroupDesignation","Shared");
        itemJSON.put("docType","Item");
        itemJSON.put("customerCode","NA");
        itemJSON.put("useRestrictions","In Library Use");
        itemJSON.put("volumePartYear","Bd. 1, Lfg. 7-10");
        itemJSON.put("callNumber","JFN 73-43");
        JSONObject itemStatusEntity = new JSONObject();
        itemStatusEntity.put("itemStatusId",1);
        itemStatusEntity.put("statusCode","Available");
        itemStatusEntity.put("statusDescription","Available");
        itemJSON.put("itemStatusEntity",itemStatusEntity);

        JSONObject collectionGroupEntity = new JSONObject();
        collectionGroupEntity.put("collectionGroupId",1);
        collectionGroupEntity.put("collectionGroupCode","Shared");
        collectionGroupEntity.put("collectionGroupDescription","Shared");
        itemJSON.put("collectionGroupEntity",collectionGroupEntity);

        JSONObject holdingsJSON = new JSONObject();
        holdingsJSON.put("content","<collection xmlns=\"http://www.loc.gov/MARC21/slim\">\n" +
                "            <record>\n" +
                "              <datafield tag=\"852\" ind1=\"0\" ind2=\"1\">\n" +
                "                <subfield code=\"b\">off,che</subfield>\n" +
                "                <subfield code=\"h\">TA434 .S15</subfield>\n" +
                "              </datafield>\n" +
                "              <datafield tag=\"866\" ind1=\"0\" ind2=\"0\">\n" +
                "                <subfield code=\"a\">v.1-16         </subfield>\n" +
                "              </datafield>\n" +
                "            </record>\n" +
                "          </collection>\n");
        ItemJSONUtil itemJSONUtil = new ItemJSONUtil();
        Item item = itemJSONUtil.generateItemForIndex(itemJSON,holdingsJSON);
        assertNotNull(item);
        assertEquals(new Integer(1),item.getItemId());
        assertEquals("CU54519993",item.getBarcode());
        assertEquals("Available",item.getAvailability());
        assertEquals("Shared",item.getCollectionGroupDesignation());
        assertEquals("Item",item.getDocType());
        assertEquals("NA",item.getCustomerCode());
        assertEquals("In Library Use",item.getUseRestriction());
        assertEquals("Bd. 1, Lfg. 7-10",item.getVolumePartYear());
        assertEquals("JFN 73-43",item.getCallNumber());

    }

    @Test
    public void generateItemForIndex(){

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(1);
        itemEntity.setBarcode("CU54519993");
        itemEntity.setCustomerCode("NA");
        itemEntity.setUseRestrictions("In Library Use");
        itemEntity.setVolumePartYear("Bd. 1, Lfg. 7-10");
        itemEntity.setCallNumber("JFN 73-43");
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setItemStatusId(1);
        itemStatusEntity.setStatusCode("Available");
        itemStatusEntity.setStatusDescription("Available");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupId(1);
        collectionGroupEntity.setCollectionGroupCode("Shared");
        collectionGroupEntity.setCollectionGroupDescription("collectionGroupEntity");
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        List<BibliographicEntity> bibliographicEntities = new ArrayList<>();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("1");
        bibliographicEntity.setOwningInstitutionId(3);
        bibliographicEntities.add(bibliographicEntity);
        itemEntity.setBibliographicEntities(bibliographicEntities);

        ItemJSONUtil itemJSONUtil = new ItemJSONUtil();
        Item item = itemJSONUtil.generateItemForIndex(itemEntity);
        assertNotNull(item);
        assertEquals("CU54519993",itemEntity.getBarcode());
        assertEquals("NA",itemEntity.getCustomerCode());
        assertEquals("In Library Use",itemEntity.getUseRestrictions());
        assertEquals("Bd. 1, Lfg. 7-10",itemEntity.getVolumePartYear());
        assertEquals("JFN 73-43",itemEntity.getCallNumber());
    }
}
