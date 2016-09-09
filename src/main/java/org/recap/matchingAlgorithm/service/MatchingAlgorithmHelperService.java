package org.recap.matchingAlgorithm.service;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.Bib;
import org.recap.model.solr.Item;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by angelind on 11/7/16.
 */
@Service
public class MatchingAlgorithmHelperService {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmHelperService.class);

    @Autowired
    public BibSolrCrudRepository bibCrudRepository;

    @Autowired
    public ItemCrudRepository itemCrudRepository;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    public BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    public ItemDetailsRepository itemDetailsRepository;

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

    public String getTitleToMatch(String title) {
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

    public Map<String, Set<Bib>> getMatchingBibsBasedOnTitle(List<Bib> bibs, Set<Bib> unMatchingBibSet ) {
        Map<String, Set<Bib>> owningInstitutionMap = new HashMap<>();
        Set<Bib> owningInstitutionBibSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(bibs)) {
            for (int i = 0; i < bibs.size(); i++) {
                for (int j = 0; j < bibs.size(); j++) {
                    if (i != j) {
                        Bib tempBib1 = bibs.get(i);
                        Bib tempBib2 = bibs.get(j);
                        String tempTitle1 = tempBib1.getTitleDisplay().replaceAll("[^\\w\\s]", "").trim();
                        String tempTitle2 = tempBib2.getTitleDisplay().replaceAll("[^\\w\\s]", "").trim();
                        if (StringUtils.isNotBlank(tempTitle1) && StringUtils.isNotBlank(tempTitle2)) {
                            String title1 = getTitleToMatch(tempTitle1);
                            String title2 = getTitleToMatch(tempTitle2);
                            if (title1.equalsIgnoreCase(title2)) {
                                owningInstitutionBibSet.add(tempBib1);
                            } else {
                                unMatchingBibSet.add(tempBib1);
                                unMatchingBibSet.add(tempBib2);
                            }
                        }
                    }
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

    public Map<String, ReportEntity> populateReportEntity(String fieldName, String fieldValue, Bib bib, String fileName, String type) {
        Map<String, ReportEntity> reportEntityMap = new HashMap<>();
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
                    String titleDisplay = bib.getTitleDisplay();
                    if(StringUtils.isNotBlank(titleDisplay)) {
                        ReportDataEntity titleReportDataEntity = new ReportDataEntity();
                        titleReportDataEntity.setHeaderName(RecapConstants.MATCHING_TITLE);
                        String headerValue = checkAndTruncateHeaderValue(titleDisplay);
                        titleReportDataEntity.setHeaderValue(headerValue);
                        reportDataEntities.add(titleReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(item.getBarcode())) {
                        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
                        barcodeReportDataEntity.setHeaderName(RecapConstants.MATCHING_BARCODE);
                        barcodeReportDataEntity.setHeaderValue(item.getBarcode());
                        reportDataEntities.add(barcodeReportDataEntity);
                    }
                    if(StringUtils.isNotBlank(item.getVolumePartYear())) {
                        ReportDataEntity volumePartYearReportDataEntity = new ReportDataEntity();
                        volumePartYearReportDataEntity.setHeaderName(RecapConstants.MATCHING_VOLUME_PART_YEAR);
                        volumePartYearReportDataEntity.setHeaderValue(item.getVolumePartYear());
                        reportDataEntities.add(volumePartYearReportDataEntity);
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
                    String summaryHoldings = item.getSummaryHoldings();
                    if(StringUtils.isNotBlank(summaryHoldings)) {
                        ReportDataEntity summaryHoldingsReportDataEntity = new ReportDataEntity();
                        summaryHoldingsReportDataEntity.setHeaderName(RecapConstants.MATCHING_SUMMARY_HOLDINGS);
                        String headerValue = checkAndTruncateHeaderValue(summaryHoldings);
                        summaryHoldingsReportDataEntity.setHeaderValue(headerValue);
                        reportDataEntities.add(summaryHoldingsReportDataEntity);
                    }
                    ReportDataEntity matchingFieldReportDataEntity = getMatchingFieldEntity(fieldName, fieldValue);
                    reportDataEntities.add(matchingFieldReportDataEntity);
                    reportEntity.setFileName(fileName);
                    reportEntity.setInstitutionName(RecapConstants.ALL_INST);
                    reportEntity.setType(type);
                    reportEntity.setCreatedDate(new Date());
                    reportEntity.setReportDataEntities(reportDataEntities);
                    reportEntityMap.put(item.getBarcode(), reportEntity);
                }
            }
        }
        return reportEntityMap;
    }

    private String checkAndTruncateHeaderValue(String headerValue) {
        if(headerValue.length() > 7999) {
            String headerValueSubString = headerValue.substring(0, 7996);
            headerValueSubString = headerValueSubString.concat("...");
            return headerValueSubString;
        }
        return headerValue;
    }

    public void saveExceptionReportEntity(Map<Integer, Map<String, ReportEntity>> exceptionReportEntityMap) {
        saveMatchingAndExceptionReportEntity(exceptionReportEntityMap);
    }

    public void saveMatchingReportEntity(Map<Integer, Map<String, ReportEntity>> matchingReportEntityMap) {
        saveMatchingAndExceptionReportEntity(matchingReportEntityMap);
    }

    public void saveMatchingAndExceptionReportEntity(Map<Integer, Map<String, ReportEntity>> reportEntityMap) {
        List<ReportEntity> reportEntities = new ArrayList<>();
        for(Integer bibId : reportEntityMap.keySet()) {
            Map<String, ReportEntity> barcodeReportEntityMap = reportEntityMap.get(bibId);
            for(String barcode : barcodeReportEntityMap.keySet()) {
                reportEntities.add(barcodeReportEntityMap.get(barcode));
            }
        }

        if(!CollectionUtils.isEmpty(reportEntities)) {
            int size = reportEntities.size();
            logger.info("Total Num of Report Entities : " + size);
            List<List<ReportEntity>> reportEntityPartitions = Lists.partition(reportEntities, 1000);

            for(List<ReportEntity> reportEntityList : reportEntityPartitions) {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                saveReportEntities(reportEntityList);
                stopWatch.stop();
                logger.info("Total time taken to save 1000 reportEntities : " + stopWatch.getTotalTimeSeconds());
            }
        }
    }

    public void saveReportEntities(List<ReportEntity> reportEntityList) {
        try{
            reportDetailRepository.save(reportEntityList);
        } catch (Exception ex) {
            for(ReportEntity reportEntity : reportEntityList) {
                try {
                    reportDetailRepository.save(reportEntity);
                } catch (Exception e) {
                    ReportEntity exceptionReportEntity = new ReportEntity();
                    exceptionReportEntity.setType(RecapConstants.MATCHING_EXCEPTION_OCCURED);
                    exceptionReportEntity.setCreatedDate(new Date());
                    exceptionReportEntity.setInstitutionName(RecapConstants.ALL_INST);
                    exceptionReportEntity.setFileName(RecapConstants.MATCHING_EXCEPTION_OCCURED);

                    ReportDataEntity reportDataEntity = new ReportDataEntity();
                    reportDataEntity.setHeaderName(RecapConstants.EXCEPTION_MSG);
                    reportDataEntity.setHeaderValue(e.getCause().getCause().getMessage());
                    exceptionReportEntity.setReportDataEntities(Arrays.asList(reportDataEntity));
                    reportDetailRepository.save(exceptionReportEntity);
                }
            }
        }
    }

    public void saveSummaryReportEntity(Map<String, Integer> oclcCountMap, Map<String, Integer> isbnCountMap, Map<String, Integer> issnCountMap, Map<String, Integer> lccnCountMap) {
        List<ReportEntity> reportEntities = new ArrayList<>();
        long bibCount = bibliographicDetailsRepository.count();
        long itemCount = itemDetailsRepository.count();
        reportEntities.add(getSummaryReportEntity(oclcCountMap, RecapConstants.MATCH_POINT_FIELD_OCLC, RecapConstants.SUMMARY_REPORT_FILE_NAME, bibCount, itemCount));
        reportEntities.add(getSummaryReportEntity(isbnCountMap, RecapConstants.MATCH_POINT_FIELD_ISBN, RecapConstants.SUMMARY_REPORT_FILE_NAME, bibCount, itemCount));
        reportEntities.add(getSummaryReportEntity(issnCountMap, RecapConstants.MATCH_POINT_FIELD_ISSN, RecapConstants.SUMMARY_REPORT_FILE_NAME, bibCount, itemCount));
        reportEntities.add(getSummaryReportEntity(lccnCountMap, RecapConstants.MATCH_POINT_FIELD_LCCN, RecapConstants.SUMMARY_REPORT_FILE_NAME, bibCount, itemCount));
        saveReportEntities(reportEntities);
    }

    public ReportEntity getSummaryReportEntity(Map<String, Integer> countMap, String fieldName, String fileName, long bibCount, long itemCount) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setCreatedDate(new Date());
        reportEntity.setFileName(fileName);
        reportEntity.setType(RecapConstants.SUMMARY_TYPE);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);

        ReportDataEntity bibsInTableDataEntity = new ReportDataEntity();
        bibsInTableDataEntity.setHeaderName(RecapConstants.SUMMARY_NUM_BIBS_IN_TABLE);
        bibsInTableDataEntity.setHeaderValue(String.valueOf(bibCount));
        reportDataEntities.add(bibsInTableDataEntity);

        ReportDataEntity itemsInTableDataEntity = new ReportDataEntity();
        itemsInTableDataEntity.setHeaderName(RecapConstants.SUMMARY_NUM_ITEMS_IN_TABLE);
        itemsInTableDataEntity.setHeaderValue(String.valueOf(itemCount));
        reportDataEntities.add(itemsInTableDataEntity);

        ReportDataEntity matchingKeyFieldDataEntity = new ReportDataEntity();
        matchingKeyFieldDataEntity.setHeaderName(RecapConstants.SUMMARY_MATCHING_KEY_FIELD);
        matchingKeyFieldDataEntity.setHeaderValue(getMatchPointTagEntity(fieldName));
        reportDataEntities.add(matchingKeyFieldDataEntity);

        ReportDataEntity matchedBibCountDataEntity = new ReportDataEntity();
        matchedBibCountDataEntity.setHeaderName(RecapConstants.SUMMARY_MATCHING_BIB_COUNT);
        matchedBibCountDataEntity.setHeaderValue(String.valueOf(countMap.get(RecapConstants.BIB_COUNT)));
        reportDataEntities.add(matchedBibCountDataEntity);

        ReportDataEntity matchedItemCountDataEntity = new ReportDataEntity();
        matchedItemCountDataEntity.setHeaderName(RecapConstants.SUMMARY_NUM_ITEMS_AFFECTED);
        matchedItemCountDataEntity.setHeaderValue(String.valueOf(countMap.get(RecapConstants.ITEM_COUNT)));
        reportDataEntities.add(matchedItemCountDataEntity);

        reportEntity.addAll(reportDataEntities);
        return reportEntity;
    }

    private String getMatchPointTagEntity(String fieldName) {
        if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            return RecapConstants.OCLC_TAG;
        } else if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            return RecapConstants.ISBN_TAG;
        } else if(fieldName.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            return RecapConstants.ISSN_TAG;
        } else {
            return RecapConstants.LCCN_TAG;
        }
    }
}
