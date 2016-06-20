package org.recap.util;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.model.solr.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelind on 16/6/16.
 */
public class ItemJSONUtil {

    private static ItemJSONUtil itemJSONUtil;

    private ItemJSONUtil() {
    }

    public static ItemJSONUtil getInstance() {
        if(null == itemJSONUtil) {
            itemJSONUtil = new ItemJSONUtil();
        }
        return itemJSONUtil;
    }

    public Item generateItemForIndex(JSONObject jsonObject) {
        Item item = new Item();
        MarcUtil marcUtil = new MarcUtil();
        try {
            String itemId = jsonObject.getString("itemId");
            item.setItemId(itemId);
            item.setId(itemId);
            item.setBarcode(jsonObject.getString("barcode"));
            item.setDocType("Item");
            item.setCustomerCode(jsonObject.getString("customerCode"));
            item.setUseRestriction(jsonObject.getString("useRestrictions"));
            item.setVolumePartYear(jsonObject.getString("volumePartYear"));
            item.setCallNumber(jsonObject.getString("callNumber"));
            String bibId = jsonObject.getString("bibliographicId");
            List<String> bibIdList = new ArrayList<>();
            bibIdList.add(bibId);
            item.setItemBibIdList(bibIdList);
            List<String> holdingsIds = new ArrayList<>();
            holdingsIds.add(jsonObject.getString("holdingsId"));
            item.setHoldingsIdList(holdingsIds);

            JSONObject itemAvailabilityStatus = jsonObject.getJSONObject("itemStatusEntity");
            item.setAvailability(null != itemAvailabilityStatus ? itemAvailabilityStatus.getString("statusCode") : "");
            JSONObject collectionGroup = jsonObject.getJSONObject("collectionGroupEntity");
            item.setCollectionGroupDesignation( null != collectionGroup ? collectionGroup.getString("collectionGroupCode") : "");

            /*JSONObject holdingsEntity = jsonObject.getJSONObject("holdingsEntity");
            String holdingsContent = holdingsEntity.getString("content");
            List<Record> records = marcUtil.convertMarcXmlToRecord(holdingsContent);
            Record marcRecord = records.get(0);
            item.setSummaryHoldings(marcUtil.getDataFieldValue(marcRecord, "866", null, null, "a"));*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }
}
