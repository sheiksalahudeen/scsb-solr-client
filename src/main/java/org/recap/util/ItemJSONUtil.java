package org.recap.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.model.jpa.*;
import org.recap.model.solr.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 16/6/16.
 */
public class ItemJSONUtil extends MarcUtil{

    private static ItemJSONUtil itemJSONUtil;

    public ItemJSONUtil() {
    }

    public Item generateItemForIndex(JSONObject itemJSON, JSONObject holdingsJSON) {
        Item item = new Item();
        try {
            Integer itemId = itemJSON.getInt("itemId");
            item.setItemId(itemId);
            item.setBarcode(itemJSON.getString("barcode"));
            item.setDocType("Item");
            item.setCustomerCode(itemJSON.getString("customerCode"));
            item.setUseRestriction(itemJSON.getString("useRestrictions"));
            item.setVolumePartYear(itemJSON.getString("volumePartYear"));
            item.setCallNumber(itemJSON.getString("callNumber"));
            Integer bibId = itemJSON.getInt("bibliographicId");
            List<Integer> bibIdList = new ArrayList<>();
            bibIdList.add(bibId);
            item.setItemBibIdList(bibIdList);
            List<Integer> holdingsIds = new ArrayList<>();
            holdingsIds.add(itemJSON.getInt("holdingsId"));
            item.setHoldingsIdList(holdingsIds);

            JSONObject itemAvailabilityStatus = itemJSON.getJSONObject("itemStatusEntity");
            item.setAvailability(null != itemAvailabilityStatus ? itemAvailabilityStatus.getString("statusCode") : "");
            JSONObject collectionGroup = itemJSON.getJSONObject("collectionGroupEntity");
            item.setCollectionGroupDesignation(null != collectionGroup ? collectionGroup.getString("collectionGroupCode") : "");

            if (holdingsJSON != null) {
                String holdingsContent = holdingsJSON.getString("content");
                List<Record> records = convertMarcXmlToRecord(holdingsContent);
                Record marcRecord = records.get(0);
                item.setSummaryHoldings(getDataFieldValue(marcRecord, "866", null, null, "a"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
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
