package org.recap.util;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.matchingalgorithm.MatchingCounter;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.recap.repository.jpa.ReportDataDetailsRepository;
import org.recap.repository.jpa.ReportDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;

/**
 * Created by angelind on 4/11/16.
 */
@Component
public class MatchingAlgorithmUtil {

    private static final Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUtil.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    ReportDetailRepository reportDetailRepository;

    @Autowired
    ReportDataDetailsRepository reportDataDetailsRepository;

    String and = " AND ";
    String coreParentFilterQuery = "{!parent which=\"ContentType:parent\"}";


    public Map<String,Integer> getSingleMatchBibsAndSaveReport(Integer batchSize, String matching) {
        Map<String, Set<Integer>> criteriaMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Integer> singleMatchBibIdsBasedOnMatching = matchingBibDetailsRepository.getSingleMatchBibIdsBasedOnMatching(matching);
        stopWatch.stop();
        logger.info("Time taken to fetch {} from db : {} ",matching,stopWatch.getTotalTimeSeconds());
        logger.info("Total {}  : {} " ,matching ,singleMatchBibIdsBasedOnMatching.size());

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(singleMatchBibIdsBasedOnMatching)) {
            List<List<Integer>> bibIdLists = Lists.partition(singleMatchBibIdsBasedOnMatching, batchSize);
            logger.info("Total {} list : {} ",matching, bibIdLists.size());
            for (Iterator<List<Integer>> iterator = bibIdLists.iterator(); iterator.hasNext(); ) {
                List<Integer> bibIds = iterator.next();
                List<MatchingBibEntity> matchingBibEntities = matchingBibDetailsRepository.getBibEntityBasedOnBibIds(bibIds);
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(matchingBibEntities)) {
                    for (Iterator<MatchingBibEntity> matchingBibEntityIterator = matchingBibEntities.iterator(); matchingBibEntityIterator.hasNext(); ) {
                        MatchingBibEntity matchingBibEntity = matchingBibEntityIterator.next();
                        Integer bibId = matchingBibEntity.getBibId();
                        String matchCriteriaValue = getMatchCriteriaValue(matching, matchingBibEntity);
                        if(!bibEntityMap.containsKey(bibId)) {
                            bibEntityMap.put(bibId, matchingBibEntity);
                        }
                        populateCriteriaMap(criteriaMap, bibId, matchCriteriaValue);
                    }
                }
            }

            Set<String> criteriaValueSet = new HashSet<>();
            for (Iterator<String> iterator = criteriaMap.keySet().iterator(); iterator.hasNext(); ) {
                String criteriaValue = iterator.next();
                if (!criteriaValueSet.contains(criteriaValue) && criteriaMap.get(criteriaValue).size() > 1) {
                    StringBuilder matchPointValue = new StringBuilder();
                    criteriaValueSet.add(criteriaValue);
                    Set<Integer> tempBibIds = new HashSet<>();
                    List<Integer> tempBibIdList = new ArrayList<>();
                    Set<Integer> bibIds = criteriaMap.get(criteriaValue);
                    tempBibIds.addAll(bibIds);
                    for (Integer bibId : bibIds) {
                        MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                        matchPointValue.append(StringUtils.isNotBlank(matchPointValue.toString()) ? "," : "").append(getMatchCriteriaValue(matching, matchingBibEntity));
                        String[] criteriaValueList = matchPointValue.toString().split(",");
                        tempBibIds.addAll(getBibIdsForCriteriaValue(criteriaMap, criteriaValueSet, criteriaValue, matching, criteriaValueList, bibEntityMap, matchPointValue));
                    }
                    tempBibIdList.addAll(tempBibIds);
                    Map<String, Integer> countsMap = saveReportForSingleMatch(matchPointValue.toString(), tempBibIdList, matching, bibEntityMap);
                    pulMatchingCount = pulMatchingCount + countsMap.get("pulMatchingCount");
                    culMatchingCount = culMatchingCount + countsMap.get("culMatchingCount");
                    nyplMatchingCount = nyplMatchingCount + countsMap.get("nyplMatchingCount");
                }
            }
        }

        Map countsMap = new HashMap();
        countsMap.put("pulMatchingCount", pulMatchingCount);
        countsMap.put("culMatchingCount", culMatchingCount);
        countsMap.put("nyplMatchingCount", nyplMatchingCount);
        return countsMap;
    }

    public Set<Integer> getBibIdsForCriteriaValue(Map<String, Set<Integer>> criteriaMap, Set<String> criteriaValueSet, String criteriaValue,
                                                   String matching, String[] criteriaValueList, Map<Integer, MatchingBibEntity> bibEntityMap, StringBuilder matchPointValue) {
        Set<Integer> tempBibIdSet = new HashSet<>();
        for (String value : criteriaValueList) {
            criteriaValueSet.add(value);
            if (!value.equalsIgnoreCase(criteriaValue)) {
                Set<Integer> bibIdSet = criteriaMap.get(value);
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(bibIdSet)) {
                    for(Integer bibId : bibIdSet) {
                        MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
                        String matchCriteriaValue = getMatchCriteriaValue(matching, matchingBibEntity);
                        String[] matchCriteriaValueList = matchCriteriaValue.split(",");
                        for(String matchingValue : matchCriteriaValueList) {
                            if(!criteriaValueSet.contains(matchingValue)) {
                                matchPointValue.append(StringUtils.isNotBlank(matchPointValue.toString()) ? "," : "").append(matchingValue);
                                criteriaValueSet.add(matchingValue);
                            }
                        }
                    }
                    tempBibIdSet.addAll(bibIdSet);
                }
            }
        }
        return tempBibIdSet;
    }

    public String normalizeDiacriticsInTitle(String title) {
        String normalizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD);
        normalizedTitle = normalizedTitle.replaceAll("[^\\p{ASCII}]", "");
        normalizedTitle = normalizedTitle.replaceAll("\\p{M}", "");
        return normalizedTitle;
    }

    public Map<String, Integer> saveReportForSingleMatch(String criteriaValue, List<Integer> bibIdList, String criteria, Map<Integer, MatchingBibEntity> matchingBibEntityMap) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Set<String> owningInstSet = new HashSet<>();
        Set<String> materialTypeSet = new HashSet<>();
        List<Integer> bibIds = new ArrayList<>();
        List<String> owningInstList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        Map<String,String> titleMap = new HashMap<>();
        List<ReportEntity> reportEntitiesToSave = new ArrayList<>();
        List<String> owningInstBibIds = new ArrayList<>();
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;

        int index=0;
        for (Iterator<Integer> iterator = bibIdList.iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            MatchingBibEntity matchingBibEntity = matchingBibEntityMap.get(bibId);
            owningInstSet.add(matchingBibEntity.getOwningInstitution());
            owningInstList.add(matchingBibEntity.getOwningInstitution());
            owningInstBibIds.add(matchingBibEntity.getOwningInstBibId());
            bibIds.add(bibId);
            materialTypeList.add(matchingBibEntity.getMaterialType());
            materialTypeSet.add(matchingBibEntity.getMaterialType());
            index = index + 1;
            if(StringUtils.isNotBlank(matchingBibEntity.getTitle())) {
                String titleHeader = RecapConstants.TITLE + index;
                getReportDataEntity(titleHeader, matchingBibEntity.getTitle(), reportDataEntities);
                titleMap.put(titleHeader, matchingBibEntity.getTitle());
            }
        }

        if(owningInstSet.size() > 1) {
            ReportEntity reportEntity = new ReportEntity();
            String fileName = criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC) ? RecapConstants.OCLC_CRITERIA : criteria;
            reportEntity.setFileName(fileName);
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            reportEntity.setCreatedDate(new Date());
            Set<String> unMatchingTitleHeaderSet = getMatchingAndUnMatchingBibsOnTitleVerification(titleMap);
            if(CollectionUtils.isNotEmpty(unMatchingTitleHeaderSet)) {

                reportEntitiesToSave.add(processReportsForUnMatchingTitles(fileName, titleMap, bibIds,
                        materialTypeList, owningInstList, owningInstBibIds,
                        criteriaValue, unMatchingTitleHeaderSet));

            }
            if(materialTypeSet.size() != 1) {
                reportEntity.setType(RecapConstants.MATERIAL_TYPE_EXCEPTION);
            } else {
                reportEntity.setType(RecapConstants.SINGLE_MATCH);
                for(String owningInst : owningInstList) {
                    if(owningInst.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                        pulMatchingCount++;
                    } else if(owningInst.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                        culMatchingCount++;
                    } else if(owningInst.equalsIgnoreCase(RecapConstants.NYPL)) {
                        nyplMatchingCount++;
                    }
                }
            }

            getReportDataEntityList(reportDataEntities, owningInstList, bibIds, materialTypeList, owningInstBibIds);

            getReportDataEntity(fileName, criteriaValue, reportDataEntities);

            reportEntity.addAll(reportDataEntities);
            reportEntitiesToSave.add(reportEntity);
        }
        if(CollectionUtils.isNotEmpty(reportEntitiesToSave)) {
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", reportEntitiesToSave);
        }
        Map countsMap = new HashMap();
        countsMap.put("pulMatchingCount", pulMatchingCount);
        countsMap.put("culMatchingCount", culMatchingCount);
        countsMap.put("nyplMatchingCount", nyplMatchingCount);
        return countsMap;
    }

    public Set<String> getMatchingAndUnMatchingBibsOnTitleVerification(Map<String, String> titleMap) {

        Set<String> unMatchingTitleHeaderSet = new HashSet<>();
        if (titleMap != null) {
            List<String> titleHeaders = new ArrayList(titleMap.keySet());
            for(int i=0; i < titleMap.size(); i++) {
                for(int j=i+1; j < titleMap.size(); j++) {
                    String titleHeader1 = titleHeaders.get(i);
                    String titleHeader2 = titleHeaders.get(j);
                    String title1 = titleMap.get(titleHeader1);
                    String title2 = titleMap.get(titleHeader2);
                    title1 = getTitleToMatch(title1);
                    title2 = getTitleToMatch(title2);
                    if(!(title1.equalsIgnoreCase(title2))) {
                        unMatchingTitleHeaderSet.add(titleHeader1);
                        unMatchingTitleHeaderSet.add(titleHeader2);
                    }
                }
            }
        }
        return unMatchingTitleHeaderSet;
    }

    public String getTitleToMatch(String title) {
        title = normalizeDiacriticsInTitle(title.trim());
        title = title.replaceAll("[^\\w\\s]", "").trim();
        title = title.replaceAll("\\s{2,}", " ");
        String titleToMatch = "";
        if(StringUtils.isNotBlank(title)) {
            String[] titleArray = title.split(" ");
            int count = 0;
            for (int j = 0; j < titleArray.length; j++) {
                String tempTitle = titleArray[j];
                if (!("a".equalsIgnoreCase(tempTitle) || "an".equalsIgnoreCase(tempTitle) || "the".equalsIgnoreCase(tempTitle))) {
                    if(count == 0) {
                        titleToMatch = tempTitle;
                    } else {
                        StringBuilder  stringBuilder = new StringBuilder();
                        stringBuilder.append(titleToMatch);
                        stringBuilder.append(" ");
                        stringBuilder.append(tempTitle);
                        titleToMatch = stringBuilder.toString();
                    }
                    count = count + 1;
                } else {
                    if(j != 0) {
                        if(count == 0) {
                            titleToMatch = tempTitle;
                        } else {
                            StringBuilder  stringBuilder = new StringBuilder();
                            stringBuilder.append(titleToMatch);
                            stringBuilder.append(" ");
                            stringBuilder.append(tempTitle);
                            titleToMatch = stringBuilder.toString();
                        }
                        count = count + 1;
                    }
                }
                if (count == 4) {
                    break;
                }
            }
        }
        return titleToMatch.replaceAll("\\s", "");
    }

    public void getReportDataEntityList(List<ReportDataEntity> reportDataEntities, Collection owningInstSet, Collection bibIds, Collection materialTypes, List<String> owningInstBibIds) {
        if(CollectionUtils.isNotEmpty(bibIds)) {
            ReportDataEntity bibIdReportDataEntity = getReportDataEntityForCollectionValues(bibIds, RecapConstants.BIB_ID);
            reportDataEntities.add(bibIdReportDataEntity);
        }

        if(CollectionUtils.isNotEmpty(owningInstSet)) {
            ReportDataEntity owningInstReportDataEntity = getReportDataEntityForCollectionValues(owningInstSet, RecapConstants.OWNING_INSTITUTION);
            reportDataEntities.add(owningInstReportDataEntity);
        }

        if(CollectionUtils.isNotEmpty(materialTypes)) {
            ReportDataEntity materialTypeReportDataEntity = getReportDataEntityForCollectionValues(materialTypes, "MaterialType");
            reportDataEntities.add(materialTypeReportDataEntity);
        }

        if(CollectionUtils.isNotEmpty(owningInstBibIds)) {
            ReportDataEntity owningInstBibIdReportDataEntity = getReportDataEntityForCollectionValues(owningInstBibIds, RecapConstants.OWNING_INSTITUTION_BIB_ID);
            reportDataEntities.add(owningInstBibIdReportDataEntity);
        }
    }

    public ReportDataEntity getReportDataEntityForCollectionValues(Collection headerValues, String headerName) {
        ReportDataEntity bibIdReportDataEntity = new ReportDataEntity();
        bibIdReportDataEntity.setHeaderName(headerName);
        bibIdReportDataEntity.setHeaderValue(StringUtils.join(headerValues, ","));
        return bibIdReportDataEntity;
    }

    public void populateBibIdWithMatchingCriteriaValue(Map<String, Set<Integer>> criteria1Map, List<MatchingBibEntity> matchingBibEntities, String matchCriteria1, Map<Integer, MatchingBibEntity> bibEntityMap) {
        for (Iterator<MatchingBibEntity> iterator = matchingBibEntities.iterator(); iterator.hasNext(); ) {
            MatchingBibEntity matchingBibEntity = iterator.next();
            Integer bibId = matchingBibEntity.getBibId();
            String matching = matchingBibEntity.getMatching();
            if(!bibEntityMap.containsKey(bibId)) {
                bibEntityMap.put(bibId, matchingBibEntity);
            }
            if(matching.equalsIgnoreCase(matchCriteria1)) {
                String criteriaValue1 = getMatchCriteriaValue(matchCriteria1, matchingBibEntity);
                populateCriteriaMap(criteria1Map, bibId, criteriaValue1);
            }
        }
    }

    public void populateCriteriaMap(Map<String, Set<Integer>> criteriaMap, Integer bibId, String value) {

        String[] criteriaValues = value.split(",");
        for(String criteriaValue : criteriaValues) {
            if(StringUtils.isNotBlank(criteriaValue)) {
                if(criteriaMap.containsKey(criteriaValue)) {
                    Set<Integer> bibIdSet = new HashSet<>();
                    Set<Integer> bibIds = criteriaMap.get(criteriaValue);
                    bibIdSet.addAll(bibIds);
                    bibIdSet.add(bibId);
                    criteriaMap.put(criteriaValue, bibIdSet);
                } else {
                    Set<Integer> bibIdSet = new HashSet<>();
                    bibIdSet.add(bibId);
                    criteriaMap.put(criteriaValue,bibIdSet);
                }
            }
        }
    }

    public String getMatchCriteriaValue(String matchCriteria, MatchingBibEntity matchingBibEntity) {
        if(matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            return matchingBibEntity.getOclc();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            return matchingBibEntity.getIsbn();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            return matchingBibEntity.getIssn();
        } else if (matchCriteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_LCCN)) {
            return matchingBibEntity.getLccn();
        }
        return "";
    }

    public Map<String,Integer> populateAndSaveReportEntity(Set<Integer> bibIds, Map<Integer, MatchingBibEntity> bibEntityMap, String header1, String header2, String oclcNumbers, String isbns) {
        ReportEntity reportEntity = new ReportEntity();
        Set<String> owningInstSet = new HashSet<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportEntity.setFileName(header1 + "," + header2);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        List<String> owningInstList = new ArrayList<>();
        List<Integer> bibIdList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        Set<String> materialTypes = new HashSet<>();
        List<String> owningInstBibIds = new ArrayList<>();
        Integer pulMatchingCount = 0;
        Integer culMatchingCount = 0;
        Integer nyplMatchingCount = 0;

        for (Iterator<Integer> integerIterator = bibIds.iterator(); integerIterator.hasNext(); ) {
            Integer bibId = integerIterator.next();
            MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
            owningInstSet.add(matchingBibEntity.getOwningInstitution());
            owningInstList.add(matchingBibEntity.getOwningInstitution());
            bibIdList.add(matchingBibEntity.getBibId());
            materialTypes.add(matchingBibEntity.getMaterialType());
            materialTypeList.add(matchingBibEntity.getMaterialType());
            owningInstBibIds.add(matchingBibEntity.getOwningInstBibId());
        }
        if(materialTypes.size() == 1) {
            reportEntity.setType(RecapConstants.MULTI_MATCH);
        } else {
            reportEntity.setType(RecapConstants.MATERIAL_TYPE_EXCEPTION);
        }
        if(owningInstSet.size() > 1) {
            getReportDataEntityList(reportDataEntities, owningInstList, bibIdList, materialTypeList, owningInstBibIds);

            for(String owningInst : owningInstList) {
                if(owningInst.equalsIgnoreCase(RecapConstants.PRINCETON)) {
                    pulMatchingCount++;
                } else if(owningInst.equalsIgnoreCase(RecapConstants.COLUMBIA)) {
                    culMatchingCount++;
                } else if(owningInst.equalsIgnoreCase(RecapConstants.NYPL)) {
                    nyplMatchingCount++;
                }
            }

            if(StringUtils.isNotBlank(oclcNumbers)) {
                getReportDataEntity(header1, oclcNumbers, reportDataEntities);
            }

            if(StringUtils.isNotBlank(isbns)) {
                getReportDataEntity(header2, isbns, reportDataEntities);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }

        Map countsMap = new HashMap();
        countsMap.put("pulMatchingCount", pulMatchingCount);
        countsMap.put("culMatchingCount", culMatchingCount);
        countsMap.put("nyplMatchingCount", nyplMatchingCount);
        return countsMap;
    }

    public void getReportDataEntity(String header1, String headerValues, List<ReportDataEntity> reportDataEntities) {
        ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
        criteriaReportDataEntity.setHeaderName(header1);
        criteriaReportDataEntity.setHeaderValue(headerValues);
        reportDataEntities.add(criteriaReportDataEntity);
    }

    public ReportEntity processReportsForUnMatchingTitles(String fileName, Map<String, String> titleMap, List<Integer> bibIds, List<String> materialTypes, List<String> owningInstitutions,
                                                          List<String> owningInstBibIds, String matchPointValue, Set<String> unMatchingTitleHeaderSet) {
        ReportEntity unMatchReportEntity = new ReportEntity();
        unMatchReportEntity.setType("TitleException");
        unMatchReportEntity.setCreatedDate(new Date());
        unMatchReportEntity.setInstitutionName(RecapConstants.ALL_INST);
        unMatchReportEntity.setFileName(fileName);
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        List<String> bibIdList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        List<String> owningInstitutionList = new ArrayList<>();
        List<String> owningInstBibIdList = new ArrayList<>();

        prepareReportForUnMatchingTitles(titleMap, bibIds, materialTypes, owningInstitutions, owningInstBibIds, unMatchingTitleHeaderSet, reportDataEntityList, bibIdList, materialTypeList, owningInstitutionList, owningInstBibIdList);

        getReportDataEntityList(reportDataEntityList, owningInstitutionList, bibIdList, materialTypeList, owningInstBibIdList);

        if(StringUtils.isNotBlank(matchPointValue)) {
            getReportDataEntity(fileName, matchPointValue, reportDataEntityList);
        }
        unMatchReportEntity.addAll(reportDataEntityList);
        return unMatchReportEntity;
    }

    public void prepareReportForUnMatchingTitles(Map<String, String> titleMap, List<Integer> bibIds, List<String> materialTypes, List<String> owningInstitutions, List<String> owningInstBibIds,
                                                 Set<String> unMatchingTitleHeaderSet, List<ReportDataEntity> reportDataEntityList, List<String> bibIdList,
                                                 List<String> materialTypeList, List<String> owningInstitutionList, List<String> owningInstBibIdList) {
        for (Iterator<String> stringIterator = unMatchingTitleHeaderSet.iterator(); stringIterator.hasNext(); ) {
            String titleHeader = stringIterator.next();
            int i = Integer.valueOf(titleHeader.replace(RecapConstants.TITLE, ""));
            if(bibIds != null) {
                bibIdList.add(String.valueOf(bibIds.get(i-1)));
            }
            if(materialTypes != null) {
                materialTypeList.add(materialTypes.get(i-1));
            }
            if(owningInstitutions != null) {
                owningInstitutionList.add(owningInstitutions.get(i-1));
            }
            if(owningInstBibIds != null) {
                owningInstBibIdList.add(owningInstBibIds.get(i-1));
            }
            ReportDataEntity titleReportDataEntity = new ReportDataEntity();
            titleReportDataEntity.setHeaderName(titleHeader);
            titleReportDataEntity.setHeaderValue(titleMap.get(titleHeader));
            reportDataEntityList.add(titleReportDataEntity);
        }
    }

    public List<MatchingMatchPointsEntity> getMatchingMatchPointsEntity(String fieldName) throws Exception {
        List<MatchingMatchPointsEntity> matchingMatchPointsEntities = new ArrayList<>();
        String query = RecapConstants.DOCTYPE + ":" + RecapConstants.BIB + and + RecapConstants.IS_DELETED_BIB + ":false" + and + coreParentFilterQuery + RecapConstants.COLLECTION_GROUP_DESIGNATION
                + ":" + RecapConstants.SHARED_CGD + and + coreParentFilterQuery + RecapConstants.IS_DELETED_ITEM + ":false";
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setFacet(true);
        solrQuery.addFacetField(fieldName);
        solrQuery.setFacetLimit(-1);
        solrQuery.setFacetMinCount(2);
        solrQuery.setRows(0);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        stopWatch.stop();
        logger.info("Total Time Taken to get {} duplicates from solr : {}  ",fieldName ,stopWatch.getTotalTimeSeconds());
        List<FacetField> facetFields = queryResponse.getFacetFields();
        for (FacetField facetField : facetFields) {
            List<FacetField.Count> values = facetField.getValues();
            for (Iterator<FacetField.Count> iterator = values.iterator(); iterator.hasNext(); ) {
                FacetField.Count next = iterator.next();
                String name = next.getName();
                if(StringUtils.isNotBlank(name)) {
                    MatchingMatchPointsEntity matchingMatchPointsEntity = new MatchingMatchPointsEntity();
                    matchingMatchPointsEntity.setMatchCriteria(fieldName);
                    matchingMatchPointsEntity.setCriteriaValue(name);
                    matchingMatchPointsEntity.setCriteriaValueCount((int) next.getCount());
                    matchingMatchPointsEntities.add(matchingMatchPointsEntity);
                }
            }
        }
        return matchingMatchPointsEntities;
    }

    public void saveMatchingMatchPointEntities(List<MatchingMatchPointsEntity> matchingMatchPointsEntities) {
        int batchSize = 1000;
        int size = 0;
        if (CollectionUtils.isNotEmpty(matchingMatchPointsEntities)) {
            for (int i = 0; i < matchingMatchPointsEntities.size(); i += batchSize) {
                List<MatchingMatchPointsEntity> matchingMatchPointsEntityList = new ArrayList<>();
                matchingMatchPointsEntityList.addAll(matchingMatchPointsEntities.subList(i, Math.min(i + batchSize, matchingMatchPointsEntities.size())));
                producerTemplate.sendBody("scsbactivemq:queue:saveMatchingMatchPointsQ", matchingMatchPointsEntityList);
                size = size + matchingMatchPointsEntityList.size();
            }
        }
    }

    public Integer getCGDCountBasedOnInst(String owningInstitution) throws SolrServerException, IOException {
        SolrQuery solrQuery = solrQueryBuilder.buildSolrQueryForCGDReports(owningInstitution, RecapConstants.SHARED_CGD);
        solrQuery.setRows(0);
        QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
        SolrDocumentList results = queryResponse.getResults();
        return Math.toIntExact(results.getNumFound());
    }

    public void updateExceptionRecords(List<Integer> exceptionRecordNums, Integer batchSize) {
        if(CollectionUtils.isNotEmpty(exceptionRecordNums)) {
            List<List<Integer>> exceptionRecordNumbers = Lists.partition(exceptionRecordNums, batchSize);
            for(List<Integer> exceptionRecordNumberList : exceptionRecordNumbers) {
                List<ReportEntity> reportEntities = reportDetailRepository.findByRecordNumberIn(exceptionRecordNumberList);
                for(ReportEntity reportEntity : reportEntities) {
                    reportEntity.setType(RecapConstants.MATERIAL_TYPE_EXCEPTION);
                }
                reportDetailRepository.save(reportEntities);
            }
        }
    }

    public void updateMonographicSetRecords(List<Integer> nonMonographRecordNums, Integer batchSize) {
        if(CollectionUtils.isNotEmpty(nonMonographRecordNums)) {
            List<List<Integer>> monographicSetRecordNumbers = Lists.partition(nonMonographRecordNums, batchSize);
            for(List<Integer> monographicSetRecordNumberList : monographicSetRecordNumbers) {
                List<ReportDataEntity> reportDataEntitiesToUpdate = reportDataDetailsRepository.getReportDataEntityByRecordNumIn(monographicSetRecordNumberList, RecapConstants.MATCHING_MATERIAL_TYPE);
                if(CollectionUtils.isNotEmpty(reportDataEntitiesToUpdate)) {
                    for(ReportDataEntity reportDataEntity : reportDataEntitiesToUpdate) {
                        String headerValue = reportDataEntity.getHeaderValue();
                        String[] materialTypes = headerValue.split(",");
                        List<String> modifiedMaterialTypes = new ArrayList<>();
                        for(int i=0; i < materialTypes.length; i++) {
                            modifiedMaterialTypes.add(RecapConstants.MONOGRAPHIC_SET);
                        }
                        reportDataEntity.setHeaderValue(StringUtils.join(modifiedMaterialTypes, ","));
                    }
                    reportDataDetailsRepository.save(reportDataEntitiesToUpdate);
                }
            }
        }
    }

    public void saveCGDUpdatedSummaryReport() {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setType("MatchingCGDSummary");
        reportEntity.setFileName("MatchingCGDSummaryReport");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        getReportDataEntity("PULSharedCount", String.valueOf(MatchingCounter.getPulCGDUpdatedSharedCount()), reportDataEntities);
        getReportDataEntity("CULSharedCount", String.valueOf(MatchingCounter.getCulCGDUpdatedSharedCount()), reportDataEntities);
        getReportDataEntity("NYPLSharedCount", String.valueOf(MatchingCounter.getNyplCGDUpdatedSharedCount()), reportDataEntities);
        getReportDataEntity("PULOpenCount", String.valueOf(MatchingCounter.getPulCGDUpdatedOpenCount()), reportDataEntities);
        getReportDataEntity("CULOpenCount", String.valueOf(MatchingCounter.getCulCGDUpdatedOpenCount()), reportDataEntities);
        getReportDataEntity("NYPLOpenCount", String.valueOf(MatchingCounter.getNyplCGDUpdatedOpenCount()), reportDataEntities);
        reportEntity.addAll(reportDataEntities);
        reportDetailRepository.save(reportEntity);
    }

    public void populateMatchingCounter() throws IOException, SolrServerException {
        MatchingCounter.reset();

        MatchingCounter.setPulSharedCount(getCGDCountBasedOnInst(RecapConstants.PRINCETON));
        MatchingCounter.setCulSharedCount(getCGDCountBasedOnInst(RecapConstants.COLUMBIA));
        MatchingCounter.setNyplSharedCount(getCGDCountBasedOnInst(RecapConstants.NYPL));

        logger.info("PUL Initial Counter Value: " + MatchingCounter.getPulSharedCount());
        logger.info("CUL Initial Counter Value: " + MatchingCounter.getCulSharedCount());
        logger.info("NYPL Initial Counter Value: " + MatchingCounter.getNyplSharedCount());
    }

}
