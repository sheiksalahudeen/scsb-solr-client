package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.model.Bib;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pvsubrah on 6/15/16.
 */
public class BibJSONUtil extends MarcUtil {

    private static BibJSONUtil bibJSONUtil;

    private BibJSONUtil() {
    }

    public static BibJSONUtil getInstance() {
        if (bibJSONUtil == null) {
            bibJSONUtil = new BibJSONUtil();
        }
        return bibJSONUtil;
    }

    public Bib generateBibForIndex(JSONObject jsonObject) {
        Bib bib = new Bib();
        try {
            String bibliographicId = jsonObject.getString("bibliographicId");
            bib.setBibId(bibliographicId);
            bib.setId(bibliographicId);

            String bibContent = jsonObject.getString("content");
            List<Record> records = convertMarcXmlToRecord(bibContent);
            Record marcRecord = records.get(0);
            String owningInstitution = jsonObject.getString("owningInstitutionId");

            bib.setTitle(getDataFieldValue(marcRecord, "24", Arrays.asList('a', 'b')));
            bib.setAuthor(getDataFieldValue(marcRecord, "100", null, null, "a"));
            bib.setPublisher(getPublisherValue(marcRecord));
            bib.setPublicationPlace(getPublicationPlaceValue(marcRecord));
            bib.setPublicationDate(getPublicationDateValue(marcRecord));
            bib.setSubject(getDataFieldValue(marcRecord, "6"));
            bib.setIsbn(getMultiDataFieldValues(marcRecord, "020", null,null,"a"));
            bib.setIssn(getMultiDataFieldValues(marcRecord, "022", null, null, "a"));
            bib.setOclcNumber(getOCLCNumbers(marcRecord, owningInstitution));
            bib.setMaterialType(getDataFieldValue(marcRecord, "245",null, null, "h"));
            bib.setNotes(getDataFieldValue(marcRecord, "5"));
            bib.setLccn(getLCCNValue(marcRecord));

            JSONArray holdingsEntities = jsonObject.getJSONArray("holdingsEntities");
            List<String> holdingsIds = new ArrayList<>();
            for (int j = 0; j < holdingsEntities.length(); j++) {
                JSONObject holdings = holdingsEntities.getJSONObject(j);
                String holdingsId = holdings.getString("holdingsId");
                holdingsIds.add(holdingsId);
            }
            bib.setHoldingsIdList(holdingsIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bib;
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

    private List<String> getOCLCNumbers(Record record, String owningInstitution){
        List<String> oclcNumbers = new ArrayList<>();
        List<String> oclcNumberList = getMultiDataFieldValues(record, "035",null, null, "a");
        for (String oclcNumber : oclcNumberList){
            if (StringUtils.isNotBlank(oclcNumber) && oclcNumber.contains("OCoLC")){
                oclcNumbers.add(oclcNumber.replaceAll("[^0-9]", ""));
            }
        }
        if (CollectionUtils.isEmpty(oclcNumbers) && StringUtils.isNotBlank(owningInstitution) && owningInstitution.equalsIgnoreCase("3")) {
            String oclcTag = getControlFieldValue(record, "003");
            if (StringUtils.isNotBlank(oclcTag) && oclcTag.equalsIgnoreCase("OCoLC")){
                oclcNumbers.add(getControlFieldValue(record,"001"));
            }
        }
        return oclcNumbers;
    }
}
