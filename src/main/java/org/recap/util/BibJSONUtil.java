package org.recap.util;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.model.jpa.*;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class BibJSONUtil extends MarcUtil {

    public Map<String, List> generateBibAndItemsForIndex(JSONObject jsonObject) {
        Map map = new HashMap();
        Bib bib = new Bib();
        List<Item> items = new ArrayList<>();
        try {
            String bibliographicId = jsonObject.getString("bibliographicId");
            bib.setBibId(bibliographicId);
            bib.setDocType("Bib");
            String bibContent = jsonObject.getString("content");
            List<Record> records = convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);

            JSONObject institutionEntity = jsonObject.getJSONObject("institutionEntity");
            String institutionCode = null != institutionEntity ? institutionEntity.getString("institutionCode") : "";
            bib.setOwningInstitution(institutionCode);

            bib.setTitle(getDataFieldValue(marcRecord, "24", Arrays.asList('a', 'b')));
            bib.setAuthor(getDataFieldValue(marcRecord, "100", null, null, "a"));
            bib.setPublisher(getPublisherValue(marcRecord));
            bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
            bib.setPublicationDate(getPublicationDateValue(marcRecord));
            bib.setSubject(getDataFieldValue(marcRecord, "6"));
            bib.setIsbn(getMultiDataFieldValues(marcRecord, "020", null, null, "a"));
            bib.setIssn(getMultiDataFieldValues(marcRecord, "022", null, null, "a"));
            bib.setOclcNumber(getOCLCNumbers(marcRecord, institutionCode));
            bib.setMaterialType(getDataFieldValue(marcRecord, "245", null, null, "h"));
            bib.setNotes(getDataFieldValue(marcRecord, "5"));
            bib.setLccn(getLCCNValue(marcRecord));

            JSONArray holdingsEntities = jsonObject.getJSONArray("holdingsEntities");
            List<String> holdingsIds = new ArrayList<>();
            List<String> itemIds = new ArrayList<>();
            for (int j = 0; j < holdingsEntities.length(); j++) {
                JSONObject holdingsJSON = holdingsEntities.getJSONObject(j);
                String holdingsId = holdingsJSON.getString("holdingsId");
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

    private String getPublisherValue(Record record) {
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

    private String getPublicationDateValue(Record record) {
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

    private String getLCCNValue(Record record) {
        String lccnValue = null;
        String leaderFieldValue = record.getLeader() != null ? record.getLeader().toString() : null;
        if (StringUtils.isNotBlank(leaderFieldValue) && leaderFieldValue.length() > 7 && leaderFieldValue.charAt(7) == 's') {
            lccnValue = getDataFieldValue(record, "010", null, null, "z");
        } else {
            lccnValue = getDataFieldValue(record, "010", null, null, "a");
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

        List<String> holdingsIds = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();

        List<BibliographicItemEntity> bibliographicItemEntities = bibliographicEntity.getBibliographicItemEntities();
        for (BibliographicItemEntity bibliographicItemEntity : bibliographicItemEntities) {
            itemIds.add(bibliographicItemEntity.getItemId().toString());
            Item item = new ItemJSONUtil().generateItemForIndex(bibliographicItemEntity.getItemEntity());
            items.add(item);
        }
        List<BibliographicHoldingsEntity> bibliographicHoldingsEntities = bibliographicEntity.getBibliographicHoldingsEntities();
        for (BibliographicHoldingsEntity bibliographicHoldingsEntity : bibliographicHoldingsEntities) {
            holdingsIds.add(bibliographicHoldingsEntity.getHoldingsId().toString());
        }

        bib.setHoldingsIdList(holdingsIds);
        bib.setBibItemIdList(itemIds);

        map.put("Bib", Arrays.asList(bib));
        map.put("Item", items);
        return map;
    }

    public Bib generateBibForIndex(BibliographicEntity bibliographicEntity) {
        Bib bib = generateBib(bibliographicEntity);

        List<String> holdingsIds = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();

        List<BibliographicItemEntity> bibliographicItemEntities = bibliographicEntity.getBibliographicItemEntities();
        for (BibliographicItemEntity bibliographicItemEntity : bibliographicItemEntities) {
            itemIds.add(bibliographicItemEntity.getItemId().toString());
        }
        List<BibliographicHoldingsEntity> bibliographicHoldingsEntities = bibliographicEntity.getBibliographicHoldingsEntities();
        for (BibliographicHoldingsEntity bibliographicHoldingsEntity : bibliographicHoldingsEntities) {
            holdingsIds.add(bibliographicHoldingsEntity.getHoldingsId().toString());
        }

        bib.setHoldingsIdList(holdingsIds);
        bib.setBibItemIdList(itemIds);
        return bib;
    }

    private Bib generateBib(BibliographicEntity bibliographicEntity) {
        Bib bib = new Bib();
        Integer bibliographicId = bibliographicEntity.getBibliographicId();
        bib.setBibId(bibliographicId.toString());

        bib.setDocType("Bib");
        String bibContent = bibliographicEntity.getContent();
        List<Record> records = convertMarcXmlToRecord(bibContent);
        Record marcRecord = records.get(0);

        InstitutionEntity institutionEntity = bibliographicEntity.getInstitutionEntity();
        String institutionCode = null != institutionEntity ? institutionEntity.getInstitutionCode() : "";

        bib.setOwningInstitution(institutionCode);
        bib.setTitle(getDataFieldValue(marcRecord, "24", Arrays.asList('a', 'b')));
        bib.setAuthor(getDataFieldValue(marcRecord, "100", null, null, "a"));
        bib.setPublisher(getPublisherValue(marcRecord));
        bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
        bib.setPublicationDate(getPublicationDateValue(marcRecord));
        bib.setSubject(getDataFieldValue(marcRecord, "6"));
        bib.setIsbn(getMultiDataFieldValues(marcRecord, "020", null, null, "a"));
        bib.setIssn(getMultiDataFieldValues(marcRecord, "022", null, null, "a"));
        bib.setOclcNumber(getOCLCNumbers(marcRecord, institutionCode.toString()));
        bib.setMaterialType(getDataFieldValue(marcRecord, "245", null, null, "h"));
        bib.setNotes(getDataFieldValue(marcRecord, "5"));
        bib.setLccn(getLCCNValue(marcRecord));
        return bib;
    }


}
