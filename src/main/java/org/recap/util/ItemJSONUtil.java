package org.recap.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.model.jpa.*;
import org.recap.model.solr.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 16/6/16.
 */
public class ItemJSONUtil extends MarcUtil{

    Logger logger = LoggerFactory.getLogger(ItemJSONUtil.class);

    private static ItemJSONUtil itemJSONUtil;

    public ItemJSONUtil() {
    }

    public Item generateItemForIndex(ItemEntity itemEntity) {
        Item item = new Item();
        try {
            Integer itemId = itemEntity.getItemId();
            item.setItemId(itemId);
            item.setBarcode(itemEntity.getBarcode());
            item.setDocType("Item");
            item.setCustomerCode(itemEntity.getCustomerCode());
            item.setUseRestriction(itemEntity.getUseRestrictions());
            item.setVolumePartYear(itemEntity.getVolumePartYear());
            item.setCallNumber(itemEntity.getCallNumber());

            List<Integer> bibIdList = new ArrayList<>();
            List<BibliographicEntity> bibliographicEntities = itemEntity.getBibliographicEntities();
            for (BibliographicEntity bibliographicEntity : bibliographicEntities){
                bibIdList.add(bibliographicEntity.getBibliographicId());
            }
            item.setItemBibIdList(bibIdList);

            ItemStatusEntity itemStatusEntity = itemEntity.getItemStatusEntity();
            if (itemStatusEntity != null) {
                item.setAvailability(itemStatusEntity.getStatusCode());
            }
            CollectionGroupEntity collectionGroupEntity = itemEntity.getCollectionGroupEntity();
            if (collectionGroupEntity != null) {
                item.setCollectionGroupDesignation(collectionGroupEntity.getCollectionGroupCode());
            }

            List<Integer> holdingsIds = new ArrayList<>();
            HoldingsEntity holdingsEntity = itemEntity.getHoldingsEntity();
            if(null != holdingsEntity) {
                holdingsIds.add(holdingsEntity.getHoldingsId());
                item.setHoldingsIdList(holdingsIds);
                String holdingsContent = new String(holdingsEntity.getContent());
                List<Record> records = convertMarcXmlToRecord(holdingsContent);
                Record marcRecord = records.get(0);
                item.setSummaryHoldings(getDataFieldValue(marcRecord, "866", null, null, "a"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
