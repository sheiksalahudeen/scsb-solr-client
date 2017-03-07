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

    private static final Logger logger = LoggerFactory.getLogger(ItemJSONUtilUT.class);

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
