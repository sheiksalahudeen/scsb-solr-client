package org.recap.matchingAlgorithm.service;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.model.csv.MatchingReportReCAPCSVRecord;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.util.ReCAPCSVMatchingRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by angelind on 11/7/16.
 */
@Service
public class MatchingAlgorithmHelperService {

    @Autowired
    public BibSolrCrudRepository bibCrudRepository;

    @Autowired
    public ItemCrudRepository itemCrudRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ProducerTemplate producer;

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

    private String getTitleToMatch(String title) {
        String titleToMatch = "";
        if(StringUtils.isNotBlank(title)) {
            String[] titleArray = title.split(" ");
            int count = 0;
            for (int j = 0; j < titleArray.length; j++) {
                String tempTitle = titleArray[j];
                if (!(tempTitle.equalsIgnoreCase("a") || tempTitle.equalsIgnoreCase("an") || tempTitle.equalsIgnoreCase("the"))) {
                    if(count == 0) {
                        titleToMatch = tempTitle;
                    } else {
                        titleToMatch = titleToMatch + " " + tempTitle;
                    }
                    count = count + 1;
                } else {
                    if(j != 0) {
                        if(count == 0) {
                            titleToMatch = tempTitle;
                        } else {
                            titleToMatch = titleToMatch + " " + tempTitle;
                        }
                        count = count + 1;
                    }
                }
                if (count == 4) {
                    break;
                }
            }
        }
        return titleToMatch;
    }

    public ReportDataEntity getMatchingFieldEntity(String fieldName, String fieldValue) {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        if (fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            reportDataEntity.setHeaderName(RecapConstants.MATCHING_OCLC);
            reportDataEntity.setHeaderValue(fieldValue);
        } else if (fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            reportDataEntity.setHeaderName(RecapConstants.MATCHING_ISBN);
            reportDataEntity.setHeaderValue(fieldValue);
        } else if (fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            reportDataEntity.setHeaderName(RecapConstants.MATCHING_ISSN);
            reportDataEntity.setHeaderValue(fieldValue);
        } else if (fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_LCCN)) {
            reportDataEntity.setHeaderName(RecapConstants.MATCHING_LCCN);
            reportDataEntity.setHeaderValue(fieldValue);
        }
        return reportDataEntity;
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

    public Map<String, Set<Bib>> getMatchingReports(String fieldName, String fieldValue, List<Bib> bibs, Set<Bib> unMatchingBibSet ) {
        Map<String, Set<Bib>> owningInstitutionMap = new HashMap<>();
        Set<Bib> owningInstitutionBibSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(bibs)) {
            for (Bib bib : bibs) {
                String titleToMatch = getTitleToMatch(bib.getTitleDisplay());
                List<Bib> bibList = new ArrayList<>();
                if(fieldName.equals(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
                    bibList = bibCrudRepository.findByTitleDisplayAndOclcNumber(titleToMatch, fieldValue);
                } else if (fieldName.equals(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
                    bibList = bibCrudRepository.findByTitleDisplayAndIsbn(titleToMatch, fieldValue);
                } else if (fieldName.equals(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
                    bibList = bibCrudRepository.findByTitleDisplayAndIssn(titleToMatch, fieldValue);
                } else if(fieldName.equals(RecapConstants.MATCH_POINT_FIELD_LCCN)) {
                    bibList = bibCrudRepository.findByTitleDisplayAndLccn(titleToMatch, fieldValue);
                }

                if(bibList.size() > 1) {
                    owningInstitutionBibSet.addAll(bibList);
                } else {
                    unMatchingBibSet.addAll(bibs);
                }
            }
        }

        if (!CollectionUtils.isEmpty(owningInstitutionBibSet)) {
            owningInstitutionMap = getBibsForOwningInstitution(owningInstitutionBibSet);
        }
        return owningInstitutionMap;
    }

    public Map<String, Set<Bib>> getBibsForOwningInstitution(Set<Bib> owningInstitutionBibSet) {
        Map<String, Set<Bib>> owningInstitutionMap = new HashMap<>();
        for(Bib bib : owningInstitutionBibSet) {
            Set<Bib> bibSet = new HashSet<>();
            List<Item> itemList = itemCrudRepository.findByCollectionGroupDesignationAndItemIdIn(RecapConstants.SHARED_CGD, bib.getBibItemIdList());
            if(!CollectionUtils.isEmpty(itemList)) {
                if(owningInstitutionMap.containsKey(bib.getOwningInstitution())) {
                    bibSet.addAll(owningInstitutionMap.get(bib.getOwningInstitution()));
                }
                bibSet.add(bib);
                owningInstitutionMap.put(bib.getOwningInstitution(), bibSet);
            }
        }
        return owningInstitutionMap;
    }

    public void populateAndSaveReportEntity(String fieldName, String fieldValue, Bib bib, String fileName, String type) {
        List<ReportEntity> reportEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bib.getBibItemIdList())) {
            List<Item> itemList = itemCrudRepository.findByCollectionGroupDesignationAndItemIdIn(RecapConstants.SHARED_CGD, bib.getBibItemIdList());
            for (Item item : itemList) {
                List<ReportDataEntity> reportDataEntities = new ArrayList<>();
                if(item != null) {
                    ReportEntity reportEntity = new ReportEntity();
                    if(StringUtils.isNotBlank(String.valueOf(bib.getBibId()))) {
                        ReportDataEntity localBibIdReportDataEntity = new ReportDataEntity();
                        localBibIdReportDataEntity.setHeaderName(RecapConstants.MATCHING_LOCAL_BIB_ID);
                        localBibIdReportDataEntity.setHeaderValue(String.valueOf(bib.getBibId()));
                        reportDataEntities.add(localBibIdReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(bib.getOwningInstitutionBibId())) {
                        ReportDataEntity owningInstBibIdReportDataEntity = new ReportDataEntity();
                        owningInstBibIdReportDataEntity.setHeaderName(RecapConstants.MATCHING_BIB_ID);
                        owningInstBibIdReportDataEntity.setHeaderValue(bib.getOwningInstitutionBibId());
                        reportDataEntities.add(owningInstBibIdReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(bib.getTitle())) {
                        ReportDataEntity titleReportDataEntity = new ReportDataEntity();
                        titleReportDataEntity.setHeaderName(RecapConstants.MATCHING_TITLE);
                        titleReportDataEntity.setHeaderValue(bib.getTitleDisplay());
                        reportDataEntities.add(titleReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(item.getBarcode())) {
                        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
                        barcodeReportDataEntity.setHeaderName(RecapConstants.MATCHING_BARCODE);
                        barcodeReportDataEntity.setHeaderValue(item.getBarcode());
                        reportDataEntities.add(barcodeReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(bib.getOwningInstitution())) {
                        ReportDataEntity institutionReportDataEntity = new ReportDataEntity();
                        institutionReportDataEntity.setHeaderName(RecapConstants.MATCHING_INSTITUTION_ID);
                        institutionReportDataEntity.setHeaderValue(bib.getOwningInstitution());
                        reportDataEntities.add(institutionReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(item.getUseRestriction())) {
                        ReportDataEntity useRestrictionsReportDataEntity = new ReportDataEntity();
                        useRestrictionsReportDataEntity.setHeaderName(RecapConstants.MATCHING_USE_RESTRICTIONS);
                        useRestrictionsReportDataEntity.setHeaderValue(item.getUseRestriction());
                        reportDataEntities.add(useRestrictionsReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(item.getSummaryHoldings())) {
                        ReportDataEntity summaryHoldingsReportDataEntity = new ReportDataEntity();
                        summaryHoldingsReportDataEntity.setHeaderName(RecapConstants.MATCHING_SUMMARY_HOLDINGS);
                        summaryHoldingsReportDataEntity.setHeaderValue(item.getSummaryHoldings());
                        reportDataEntities.add(summaryHoldingsReportDataEntity);
                    }
                    if(type.equals(RecapConstants.EXCEPTION_TYPE)) {
                        ReportDataEntity matchingPointReportDataEntity = getMatchPointTagEntity(fieldName, fieldValue);
                        reportDataEntities.add(matchingPointReportDataEntity);
                        ReportDataEntity matchingContentReportDataEntity = new ReportDataEntity();
                        matchingContentReportDataEntity.setHeaderName(RecapConstants.MATCH_POINT_CONTENT);
                        matchingContentReportDataEntity.setHeaderValue(fieldValue);
                        reportDataEntities.add(matchingContentReportDataEntity);
                    } else {
                        ReportDataEntity matchingFieldReportDataEntity = getMatchingFieldEntity(fieldName, fieldValue);
                        reportDataEntities.add(matchingFieldReportDataEntity);
                    }
                    reportEntity.setFileName(fileName);
                    reportEntity.setInstitutionName(RecapConstants.ALL_INST);
                    reportEntity.setType(type);
                    reportEntity.setCreatedDate(new Date());
                    reportEntity.setReportDataEntities(reportDataEntities);
                    reportEntities.add(reportEntity);
                }
            }
        }
        if(!CollectionUtils.isEmpty(reportEntities)) {
            producer.sendBody(RecapConstants.MATCHING_ALGO_Q, reportEntities);
        }
    }

    private ReportDataEntity getMatchPointTagEntity(String fieldName, String fieldValue) {
        ReportDataEntity reportDataEntity = new ReportDataEntity();
        reportDataEntity.setHeaderName(RecapConstants.MATCH_POINT_TAG);
        if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            reportDataEntity.setHeaderValue(RecapConstants.OCLC_TAG);
        } else if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            reportDataEntity.setHeaderValue(RecapConstants.ISBN_TAG);
        } else if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            reportDataEntity.setHeaderValue(RecapConstants.ISSN_TAG);
        } else {
            reportDataEntity.setHeaderValue(RecapConstants.LCCNN_TAG);
        }
        return reportDataEntity;
    }

    public void getMultipleMatchPointMatchRecords(String fileName, String type, Date from, Date to, List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecords) {
        ReCAPCSVMatchingRecordGenerator reCAPCSVMatchingRecordGenerator = new ReCAPCSVMatchingRecordGenerator();
        List<String> barcodesHavingMoreThanOneCount = reportDetailRepository.groupByHeaderValueHavingCountMoreThanOne(RecapConstants.MATCHING_BARCODE);
        if(!CollectionUtils.isEmpty(barcodesHavingMoreThanOneCount)) {
            for(String barcode : barcodesHavingMoreThanOneCount) {
                Map<String,List<ReportDataEntity>> reportDataEntityMap = new HashMap<>();
                List<ReportEntity> reportEntities = reportDetailRepository.fetchReportEntityBasedOnHeaderValue(fileName, type, from, to, RecapConstants.MATCHING_BARCODE, barcode);
                if(!CollectionUtils.isEmpty(reportEntities)) {
                    for(ReportEntity reportEntityFromDB : reportEntities) {
                        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
                        for (ReportDataEntity reportDataEntity : reportEntityFromDB.getReportDataEntities()) {
                            if(RecapConstants.MATCHING_LOCAL_BIB_ID.equalsIgnoreCase(reportDataEntity.getHeaderName())) {
                                String headerValue = reportDataEntity.getHeaderValue();
                                if(reportDataEntityMap.containsKey(headerValue)) {
                                    reportDataEntityList.addAll(reportDataEntityMap.get(headerValue));
                                }
                                reportDataEntityList.addAll(reportEntityFromDB.getReportDataEntities());
                                reportDataEntityMap.put(headerValue, reportDataEntityList);
                                break;
                            }
                        }
                    }
                    for(String localBibId : reportDataEntityMap.keySet()) {
                        ReportEntity reportEntity = new ReportEntity();
                        if(!CollectionUtils.isEmpty(reportDataEntityMap.get(localBibId))) {
                            reportEntity.addAll(reportDataEntityMap.get(localBibId));
                            MatchingReportReCAPCSVRecord matchingReportReCAPCSVRecord = reCAPCSVMatchingRecordGenerator.prepareMatchingReportReCAPCSVRecord(reportEntity);
                            matchingReportReCAPCSVRecords.add(matchingReportReCAPCSVRecord);
                        }
                    }
                }
            }
        }
    }

    public void getSingleMatchPointMatchRecords(String fileName, String type, Date from, Date to, List<MatchingReportReCAPCSVRecord> matchingReportReCAPCSVRecords) {
        ReCAPCSVMatchingRecordGenerator reCAPCSVMatchingRecordGenerator = new ReCAPCSVMatchingRecordGenerator();
        List<String> barcodesHavingOnlyOneCount = reportDetailRepository.groupByHeaderValueHavingCountEqualsOne(RecapConstants.MATCHING_BARCODE);
        if(!CollectionUtils.isEmpty(barcodesHavingOnlyOneCount)) {
            for(String barcode : barcodesHavingOnlyOneCount) {
                List<ReportEntity> reportEntities = reportDetailRepository.fetchReportEntityBasedOnHeaderValue(fileName, type, from, to, RecapConstants.MATCHING_BARCODE, barcode);
                if(!CollectionUtils.isEmpty(reportEntities)) {
                    for(ReportEntity reportEntity : reportEntities) {
                        MatchingReportReCAPCSVRecord matchingReportReCAPCSVRecord = reCAPCSVMatchingRecordGenerator.prepareMatchingReportReCAPCSVRecord(reportEntity);
                        matchingReportReCAPCSVRecords.add(matchingReportReCAPCSVRecord);
                    }
                }
            }
        }
    }
}
