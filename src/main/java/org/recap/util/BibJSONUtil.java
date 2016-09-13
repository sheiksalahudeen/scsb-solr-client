package org.recap.util;

import org.apache.commons.lang3.StringUtils;
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

    public List<String> getISBNNumber(Record record){
        List<String> isbnNumbers = new ArrayList<>();
        List<String> isbnNumberList = getMultiDataFieldValues(record,"020", null, null, "a");
        for(String isbnNumber : isbnNumberList){
            isbnNumbers.add(isbnNumber.replaceAll("[^0-9]", ""));
        }
        return isbnNumbers;
    }

    public List<String> getISSNNumber(Record record){
        List<String> issnNumbers = new ArrayList<>();
        List<String> issnNumberList = getMultiDataFieldValues(record,"022", null, null, "a");
        for(String issnNumber : issnNumberList){
            issnNumbers.add(issnNumber.replaceAll("[^0-9]", ""));
        }
        return issnNumbers;
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
        bib.setTitleSort(getTitleSort(marcRecord, bib.getTitleDisplay()));
        bib.setAuthorDisplay(getAuthorDisplayValue(marcRecord));
        bib.setAuthorSearch(getAuthorSearchValue(marcRecord));
        bib.setPublisher(getPublisherValue(marcRecord));
        bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
        bib.setPublicationDate(getPublicationDateValue(marcRecord));
        bib.setSubject(getDataFieldValueStartsWith(marcRecord, "6"));
        bib.setIsbn(getISBNNumber(marcRecord));
        bib.setIssn(getISSNNumber(marcRecord));
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
        StringBuilder title=new StringBuilder();
        title.append(getDataFieldValueStartsWith(marcRecord, "245", Arrays.asList('a', 'b','n','p')) + " ");
        title.append(getDataFieldValueStartsWith(marcRecord, "246", Arrays.asList('a', 'b')) + " ");
        title.append(getDataFieldValueStartsWith(marcRecord, "130", Arrays.asList('a')) + " ");
        title.append(getDataFieldValueStartsWith(marcRecord, "730", Arrays.asList('a')) + " ");
        title.append(getDataFieldValueStartsWith(marcRecord, "740", Arrays.asList('a')) + " ");
        title.append(getDataFieldValueStartsWith(marcRecord, "830", Arrays.asList('a'))+ " ");
        return title.toString();
    }

    public String getTitleDisplay(Record marcRecord) {
        StringBuilder titleDisplay = new StringBuilder();
        titleDisplay.append(getDataFieldValueStartsWith(marcRecord, "245", Arrays.asList('a', 'b','n','p')));
        return titleDisplay.toString();
    }

    public String getAuthorDisplayValue(Record marcRecord) {
        StringBuilder author = new StringBuilder();
        author.append(getDataFieldValueStartsWith(marcRecord, "100", Arrays.asList('a','b','c','d','e','f','g','j','k','l','n','p','q','t','u','0','4','6','8')) + " ");
        author.append(getDataFieldValueStartsWith(marcRecord, "110", Arrays.asList('a','b','c','d','e','f','g','k','l','n','p','t','u','0','4','6','8')) + " ");
        author.append(getDataFieldValueStartsWith(marcRecord, "111", Arrays.asList('a','c','d','e','f','g','j','k','l','n','p','q','t','u','0','4','6','8')) + " ");
        return author.toString();
    }

    public List<String> getAuthorSearchValue(Record marcRecord) {
        List<String> authorSearchValues = new ArrayList<>();
        List<String> fieldValues = null;

        Map<String, List<Character>> authorMap = new HashMap<>();
        authorMap.put("100", Arrays.asList('a','q'));
        authorMap.put("110", Arrays.asList('a','b'));
        authorMap.put("111", Arrays.asList('a'));
        authorMap.put("700", Arrays.asList('a'));
        authorMap.put("710", Arrays.asList('a','b'));
        authorMap.put("711", Arrays.asList('a'));


        for (Map.Entry<String, List<Character>> entry : authorMap.entrySet()) {
            fieldValues = getListOfDataFieldValuesStartsWith(marcRecord, entry.getKey(), entry.getValue());
            if (!CollectionUtils.isEmpty(fieldValues)) {
                authorSearchValues.addAll(fieldValues);
            }
        }
        return authorSearchValues;
    }

    public String getLeader(Record marcRecord) {
        return marcRecord.getLeader() != null ? marcRecord.getLeader().toString() : null;
    }

    public String getTitleSort(Record marcRecord, String titleDisplay) {
        Integer secondIndicatorForDataField = getSecondIndicatorForDataField(marcRecord, "245");
        if (StringUtils.isNotBlank(titleDisplay) && titleDisplay.length() >= secondIndicatorForDataField) {
            return titleDisplay.substring(secondIndicatorForDataField);
        }
        return "";
    }

}
