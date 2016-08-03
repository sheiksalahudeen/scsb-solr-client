package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Leader;
import org.marc4j.marc.Record;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class BibJSONUtil extends MarcUtil {

    Logger logger = LoggerFactory.getLogger(BibJSONUtil.class);

    public Map<String, List> generateBibAndItemsForIndex(JSONObject jsonObject) {
        Map map = new HashMap();
        Bib bib = new Bib();
        List<Item> items = new ArrayList<>();
        try {
            Integer bibliographicId = jsonObject.getInt("bibliographicId");
            bib.setBibId(bibliographicId);
            bib.setDocType("Bib");
            String bibContent = jsonObject.getString("content");
            List<Record> records = convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);

            JSONObject institutionEntity = jsonObject.getJSONObject("institutionEntity");
            String institutionCode = null != institutionEntity ? institutionEntity.getString("institutionCode") : "";
            bib.setOwningInstitution(institutionCode);

            bib.setTitle(getDataFieldValueStartsWith(marcRecord, "24", Arrays.asList('a', 'b')));
            bib.setAuthor(getDataFieldValue(marcRecord, "100", null, null, "a"));
            bib.setPublisher(getPublisherValue(marcRecord));
            bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
            bib.setPublicationDate(getPublicationDateValue(marcRecord));
            bib.setSubject(getDataFieldValueStartsWith(marcRecord, "6"));
            bib.setIsbn(getMultiDataFieldValues(marcRecord, "020", null, null, "a"));
            bib.setIssn(getMultiDataFieldValues(marcRecord, "022", null, null, "a"));
            bib.setOclcNumber(getOCLCNumbers(marcRecord, institutionCode));
            bib.setMaterialType(getDataFieldValue(marcRecord, "245", null, null, "h"));
            bib.setNotes(getDataFieldValueStartsWith(marcRecord, "5"));
            bib.setLccn(getLCCNValue(marcRecord));

            JSONArray holdingsEntities = jsonObject.getJSONArray("holdingsEntities");
            List<Integer> holdingsIds = new ArrayList<>();
            List<Integer> itemIds = new ArrayList<>();
            for (int j = 0; j < holdingsEntities.length(); j++) {
                JSONObject holdingsJSON = holdingsEntities.getJSONObject(j);
                Integer holdingsId = holdingsJSON.getInt("holdingsId");
                holdingsIds.add(holdingsId);

                JSONArray itemEntities = holdingsJSON.getJSONArray("itemEntities");
                for (int i = 0; i < itemEntities.length(); i++) {
                    JSONObject itemJSON = itemEntities.getJSONObject(i);
                    Item item = new ItemJSONUtil().generateItemForIndex(itemJSON, holdingsJSON);
                    items.add(item);
                    itemIds.add(item.getItemId());
                }
            }
            bib.setHoldingsIdList(holdingsIds);
            bib.setBibItemIdList(itemIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("Bib", bib);
        map.put("Item", items);
        return map;
    }

    public String getPublisherValue(Record record) {
        String publisherValue = null;
        List<String> publisherDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publisherDataField : publisherDataFields) {
            publisherValue = getDataFieldValue(record, publisherDataField, null, null, "b");
            if (StringUtils.isNotBlank(publisherValue)) {
                return publisherValue;
            }
        }
        return null;
    }

    private String getPublicationPlaceValue(Record record) {
        String publicationPlaceValue = null;
        List<String> publicationPlaceDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publicationPlaceDataField : publicationPlaceDataFields) {
            publicationPlaceValue = getDataFieldValue(record, publicationPlaceDataField, null, null, "a");
            if (StringUtils.isNotBlank(publicationPlaceValue)) {
                return publicationPlaceValue;
            }
        }
        return null;
    }

    public String getPublicationDateValue(Record record) {
        String publicationDateValue = null;
        List<String> publicationDateDataFields = Arrays.asList("260", "261", "262", "264");
        for (String publicationDateDataField : publicationDateDataFields) {
            publicationDateValue = getDataFieldValue(record, publicationDateDataField, null, null, "c");
            if (StringUtils.isNotBlank(publicationDateValue)) {
                return publicationDateValue;
            }
        }
        return null;
    }

    public String getLCCNValue(Record record) {
        String lccnValue = getDataFieldValue(record, "010", null, null, "a");
        if (lccnValue != null) {
            lccnValue = lccnValue.trim();
        }
        return lccnValue;
    }

    private List<String> getOCLCNumbers(Record record, String institutionCode) {
        List<String> oclcNumbers = new ArrayList<>();
        List<String> oclcNumberList = getMultiDataFieldValues(record, "035", null, null, "a");
        for (String oclcNumber : oclcNumberList) {
            if (StringUtils.isNotBlank(oclcNumber) && oclcNumber.contains("OCoLC")) {
                oclcNumbers.add(oclcNumber.replaceAll("[^0-9]", ""));
            }
        }
        if (CollectionUtils.isEmpty(oclcNumbers) && StringUtils.isNotBlank(institutionCode) && institutionCode.equalsIgnoreCase("NYPL")) {
            String oclcTag = getControlFieldValue(record, "003");
            if (StringUtils.isNotBlank(oclcTag) && oclcTag.equalsIgnoreCase("OCoLC")) {
                oclcNumbers.add(getControlFieldValue(record, "001"));
            }
        }
        return oclcNumbers;
    }

    public Map<String, List> generateBibAndItemsForIndex(BibliographicEntity bibliographicEntity) {
        Map map = new HashMap();
        List<Item> items = new ArrayList<>();

        Bib bib = generateBib(bibliographicEntity);

        List<Integer> holdingsIds = new ArrayList<>();
        List<Integer> itemIds = new ArrayList<>();

        List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
        for (ItemEntity itemEntity : itemEntities) {
            itemIds.add(itemEntity.getItemId());
            Item item = new ItemJSONUtil().generateItemForIndex(itemEntity);
            items.add(item);
        }
        List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
        for (HoldingsEntity holdingsEntity : holdingsEntities) {
            holdingsIds.add(holdingsEntity.getHoldingsId());
        }

        bib.setHoldingsIdList(holdingsIds);
        bib.setBibItemIdList(itemIds);

        map.put("Bib", Arrays.asList(bib));
        map.put("Item", items);
        return map;
    }

    public Bib generateBibForIndex(BibliographicEntity bibliographicEntity) {
        Bib bib = generateBib(bibliographicEntity);

        List<Integer> holdingsIds = new ArrayList<>();
        List<Integer> itemIds = new ArrayList<>();

        List<ItemEntity> itemEntities = bibliographicEntity.getItemEntities();
        for (ItemEntity itemEntity : itemEntities) {
            itemIds.add(itemEntity.getItemId());
        }
        List<HoldingsEntity> holdingsEntities = bibliographicEntity.getHoldingsEntities();
        for (HoldingsEntity holdingsEntity : holdingsEntities) {
            holdingsIds.add(holdingsEntity.getHoldingsId());
        }

        bib.setHoldingsIdList(holdingsIds);
        bib.setBibItemIdList(itemIds);
        return bib;
    }

    private Bib generateBib(BibliographicEntity bibliographicEntity) {
        Bib bib = new Bib();
        Integer bibliographicId = bibliographicEntity.getBibliographicId();
        bib.setBibId(bibliographicId);

        bib.setDocType("Bib");
        String bibContent = new String(bibliographicEntity.getContent());
        List<Record> records = convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);

        InstitutionEntity institutionEntity = bibliographicEntity.getInstitutionEntity();
        String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : "";

        bib.setOwningInstitution(institutionCode);
        bib.setTitle(getTitle(marcRecord));
        bib.setTitleDisplay(getTitleDisplay(marcRecord));
        bib.setAuthor(getAuthor(marcRecord));
        bib.setPublisher(getPublisherValue(marcRecord));
        bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
        bib.setPublicationDate(getPublicationDateValue(marcRecord));
        bib.setSubject(getDataFieldValueStartsWith(marcRecord, "6"));
        bib.setIsbn(getMultiDataFieldValues(marcRecord, "020", null, null, "a"));
        bib.setIssn(getMultiDataFieldValues(marcRecord, "022", null, null, "a"));
        bib.setOclcNumber(getOCLCNumbers(marcRecord, institutionCode.toString()));
        bib.setMaterialType(getDataFieldValue(marcRecord, "245", null, null, "h"));
        bib.setNotes(getDataFieldValueStartsWith(marcRecord, "5"));
        bib.setLccn(getLCCNValue(marcRecord));
        bib.setOwningInstitutionBibId(bibliographicEntity.getOwningInstitutionBibId());
        bib.setLeaderMaterialType(getLeaderMaterialType(marcRecord.getLeader()));
        return bib;
    }

    private String getLeaderMaterialType(Leader leader) {
        String leaderMaterialType = null;
        String leaderFieldValue = leader != null ? leader.toString() : null;
        if (StringUtils.isNotBlank(leaderFieldValue) && leaderFieldValue.length() > 7) {
            char materialTypeChar = leaderFieldValue.charAt(7);
            if ('m' == materialTypeChar) {
                leaderMaterialType = RecapConstants.MONOGRAPH;
            } else if ('s' == materialTypeChar) {
                leaderMaterialType = RecapConstants.SERIAL;
            } else {
                leaderMaterialType = RecapConstants.OTHER;
            }
        }
        return leaderMaterialType;
    }

    public String getTitle(Record marcRecord) {
        return getDataFieldValueStartsWith(marcRecord, "24", Arrays.asList('a', 'b'));
    }

    public String getTitleDisplay(Record marcRecord) {
        return getDataFieldValue(marcRecord, "245", null, null, "a");
    }

    public String getAuthor(Record marcRecord) {
        StringBuffer author = new StringBuffer();
        String fieldValue = null;

        Map<String, String> authorMap = new HashMap<>();
        authorMap.put("100", "a");
        authorMap.put("110", "a");
        authorMap.put("111", "a");
        authorMap.put("130", "1");
        authorMap.put("700", "a");
        authorMap.put("710", "a");
        authorMap.put("711", "a");
        authorMap.put("730", "a");

        for (Map.Entry<String, String> entry : authorMap.entrySet()) {
            fieldValue = getDataFieldValue(marcRecord, entry.getKey(), null, null, entry.getValue());
            if (StringUtils.isNotBlank(fieldValue)) {
                author.append(fieldValue);
                author.append(" ");
            }
        }
        return author.toString().trim();
    }

    public String getLeader(Record marcRecord) {
        return marcRecord.getLeader() != null ? marcRecord.getLeader().toString() : null;
    }

}
