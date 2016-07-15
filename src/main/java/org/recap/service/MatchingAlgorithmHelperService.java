package org.recap.service;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.model.solr.MatchingRecordReport;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by angelind on 11/7/16.
 */
@Service
public class MatchingAlgorithmHelperService {

    @Autowired
    public BibSolrCrudRepository bibCrudRepository;

    @Autowired
    public ItemCrudRepository itemCrudRepository;

    public List<Bib> getBibs(String fieldName, String fieldValue) {
        List<Bib> bibs = new ArrayList<>();
        if (fieldName.equalsIgnoreCase("OCLCNumber")) {
            bibs = bibCrudRepository.findByOclcNumber(fieldValue);
        } else if (fieldName.equalsIgnoreCase("ISBN")) {
            bibs = bibCrudRepository.findByIsbn(fieldValue);
        } else if (fieldName.equalsIgnoreCase("ISSN")) {
            bibs = bibCrudRepository.findByIssn(fieldValue);
        } else if (fieldName.equalsIgnoreCase("LCCN")) {
            bibs = bibCrudRepository.findByLccn(fieldValue);
        }
        return bibs;
    }

    public String getMatchPointTag(String fieldName) {
        String matchPointTag = "";
        if (fieldName.equalsIgnoreCase("OCLCNumber")) {
            matchPointTag = "035";
        } else if (fieldName.equalsIgnoreCase("ISBN")) {
            matchPointTag = "020";
        } else if (fieldName.equalsIgnoreCase("ISSN")) {
            matchPointTag = "022";
        } else if (fieldName.equalsIgnoreCase("LCCN")) {
            matchPointTag = "010";
        }
        return matchPointTag;
    }


    public Map<String, Integer> getBibListUsingFacet(JSONObject facetFieldObject, String fieldName) {
        Map<String, Integer> matchingFieldValues = new HashMap<>();
        JSONArray fieldObjecArray;
        try {
            fieldObjecArray = facetFieldObject.getJSONArray(fieldName);
            for (int i = 0; i < fieldObjecArray.length(); i++) {
                String fieldValue = "";
                Integer count = 0;
                if (i % 2 == 0) {
                    fieldValue = fieldObjecArray.getString(i);
                    count = fieldObjecArray.getInt(i + 1);
                }
                if (StringUtils.isNotBlank(fieldValue)) {
                    matchingFieldValues.put(fieldValue, count);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return matchingFieldValues;
    }

    public MatchingRecordReport populateMatchingRecordReport(String fieldValue, Bib bib, Item item, String fieldName) {
        MatchingRecordReport matchingRecordReport = new MatchingRecordReport();
        matchingRecordReport.setBibId(bib.getOwningInstitutionBibId());
        matchingRecordReport.setTitle(bib.getTitle());
        matchingRecordReport.setMatchPointContent(fieldValue);
        matchingRecordReport.setInstitutionId(bib.getOwningInstitution());
        matchingRecordReport.setBarcode(item.getBarcode());
        matchingRecordReport.setUseRestrictions(item.getUseRestriction());
        matchingRecordReport.setMatchPointTag(getMatchPointTag(fieldName));
        return matchingRecordReport;
    }

    public Map<String, List<MatchingRecordReport>> getMatchingReports(String fieldName, String fieldValue, List<Bib> bibs) {
        Map<String, List<MatchingRecordReport>> owningInstitutionMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bibs)) {
            for (Bib bib : bibs) {
                List<MatchingRecordReport> matchingRecordReports = new ArrayList<>();
                if (!CollectionUtils.isEmpty(bib.getBibItemIdList())) {
                    for (Integer itemId : bib.getBibItemIdList()) {
                        Item item = itemCrudRepository.findByItemId(itemId);
                        if (item.getCollectionGroupDesignation().equalsIgnoreCase("Shared")) {
                            matchingRecordReports.add(populateMatchingRecordReport(fieldValue, bib, item, fieldName));
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(matchingRecordReports)) {
                    owningInstitutionMap.put(bib.getOwningInstitution(), matchingRecordReports);
                }
            }
        }
        return owningInstitutionMap;
    }
}
