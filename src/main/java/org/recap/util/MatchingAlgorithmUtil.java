package org.recap.util;

import com.google.common.collect.Lists;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.repository.jpa.MatchingBibDetailsRepository;
import org.recap.repository.jpa.MatchingMatchPointsDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by angelind on 4/11/16.
 */
@Component
public class MatchingAlgorithmUtil {

    Logger logger = LoggerFactory.getLogger(MatchingAlgorithmUtil.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    MatchingMatchPointsDetailsRepository matchingMatchPointsDetailsRepository;

    @Autowired
    MatchingBibDetailsRepository matchingBibDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    String and = " AND ";
    String coreParentFilterQuery = "{!parent which=\"ContentType:parent\"}";


    public void getSingleMatchBibsAndSaveReport(Integer batchSize, String matching) {
        Map<String, Set<Integer>> criteriaMap = new HashMap<>();
        Map<Integer, MatchingBibEntity> bibEntityMap = new HashMap<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Integer> singleMatchBibIdsBasedOnMatching = matchingBibDetailsRepository.getSingleMatchBibIdsBasedOnMatching(matching);
        stopWatch.stop();
        logger.info("Time taken to fetch " + matching + " from db : " + stopWatch.getTotalTimeSeconds());
        logger.info("Total " + matching + " : " + singleMatchBibIdsBasedOnMatching.size());

        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(singleMatchBibIdsBasedOnMatching)) {
            List<List<Integer>> bibIdLists = Lists.partition(singleMatchBibIdsBasedOnMatching, batchSize);
            logger.info("Total " + matching + " list : " + bibIdLists.size());
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
                    saveReportForSingleMatch(matchPointValue.toString(), tempBibIdList, matching, bibEntityMap);
                }
            }
        }
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

    public void saveReportForSingleMatch(String criteriaValue, List<Integer> bibIdList, String criteria, Map<Integer, MatchingBibEntity> matchingBibEntityMap) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Set<String> owningInstSet = new HashSet<>();
        List<Integer> bibIds = new ArrayList<>();
        List<String> materialTypes = new ArrayList<>();

        for (Iterator<Integer> iterator = bibIdList.iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            MatchingBibEntity matchingBibEntity = matchingBibEntityMap.get(bibId);
            int index=0;
            if(!owningInstSet.contains(matchingBibEntity.getOwningInstitution())) {
                owningInstSet.add(matchingBibEntity.getOwningInstitution());
                bibIds.add(bibId);
                materialTypes.add(matchingBibEntity.getMaterialType());
                index = index + 1;
                if(StringUtils.isNotBlank(matchingBibEntity.getTitle())) {
                    ReportDataEntity titleReportDataEntity = new ReportDataEntity();
                    titleReportDataEntity.setHeaderName("Title" + index);
                    titleReportDataEntity.setHeaderValue(matchingBibEntity.getTitle());
                    reportDataEntities.add(titleReportDataEntity);
                }
            }
        }
        if(owningInstSet.size() > 1) {
            ReportEntity reportEntity = new ReportEntity();
            reportEntity.setFileName(criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC) ? RecapConstants.OCLC_CRITERIA : criteria);
            reportEntity.setType("SingleMatch");
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            reportEntity.setCreatedDate(new Date());

            if(CollectionUtils.isNotEmpty(bibIds)) {
                ReportDataEntity bibIdReportDataEntity = getReportDataEntityForCollectionValues(bibIds, "BibId");
                reportDataEntities.add(bibIdReportDataEntity);
            }

            if(CollectionUtils.isNotEmpty(owningInstSet)) {
                ReportDataEntity owningInstReportDataEntity = getReportDataEntityForCollectionValues(owningInstSet, "OwningInstitution");
                reportDataEntities.add(owningInstReportDataEntity);
            }

            if(CollectionUtils.isNotEmpty(materialTypes)) {
                ReportDataEntity materialTypeReportDataEntity = getReportDataEntityForCollectionValues(materialTypes, "MaterialType");
                reportDataEntities.add(materialTypeReportDataEntity);
            }

            ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
            criteriaReportDataEntity.setHeaderName(criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC) ? RecapConstants.OCLC_CRITERIA : criteria);
            criteriaReportDataEntity.setHeaderValue(criteriaValue);
            reportDataEntities.add(criteriaReportDataEntity);

            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
    }

    private ReportDataEntity getReportDataEntityForCollectionValues(Collection headerValues, String headerName) {
        ReportDataEntity bibIdReportDataEntity = new ReportDataEntity();
        bibIdReportDataEntity.setHeaderName(headerName);
        bibIdReportDataEntity.setHeaderValue(StringUtils.join(headerValues, ","));
        return bibIdReportDataEntity;
    }

    public void populateBibIdWithMatchingCriteriaValue(Map<String, Set<Integer>> criteria1Map, List<MatchingBibEntity> matchingBibEntities, String matchCriteria1, String matchCriteria2, Map<Integer, MatchingBibEntity> bibEntityMap) {
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

    public void populateAndSaveReportEntity(Set<Integer> bibIds, Map<Integer, MatchingBibEntity> bibEntityMap, String header1, String header2, String oclcNumbers, String isbns) {
        ReportEntity reportEntity = new ReportEntity();
        Set<String> owningInstSet = new HashSet<>();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        reportEntity.setFileName(header1 + "," + header2);
        reportEntity.setType("MultiMatch");
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        List<Integer> bibIdList = new ArrayList<>();
        List<String> materialTypes = new ArrayList<>();

        for (Iterator<Integer> integerIterator = bibIds.iterator(); integerIterator.hasNext(); ) {
            Integer bibId = integerIterator.next();
            MatchingBibEntity matchingBibEntity = bibEntityMap.get(bibId);
            if(!owningInstSet.contains(matchingBibEntity.getOwningInstitution())) {
                owningInstSet.add(matchingBibEntity.getOwningInstitution());
                bibIdList.add(matchingBibEntity.getBibId());
                materialTypes.add(matchingBibEntity.getMaterialType());
            }
        }
        if(owningInstSet.size() > 1) {
            if(CollectionUtils.isNotEmpty(bibIdList)) {
                ReportDataEntity bibIdReportDataEntity = getReportDataEntityForCollectionValues(bibIdList, "BibId");
                reportDataEntities.add(bibIdReportDataEntity);
            }

            if(CollectionUtils.isNotEmpty(owningInstSet)) {
                ReportDataEntity owningInstReportDataEntity = getReportDataEntityForCollectionValues(owningInstSet, "OwningInstitution");
                reportDataEntities.add(owningInstReportDataEntity);
            }

            if(CollectionUtils.isNotEmpty(materialTypes)) {
                ReportDataEntity materialTypeReportDataEntity = getReportDataEntityForCollectionValues(materialTypes, "MaterialType");
                reportDataEntities.add(materialTypeReportDataEntity);
            }

            if(StringUtils.isNotBlank(oclcNumbers)) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderName(header1);
                criteriaReportDataEntity.setHeaderValue(oclcNumbers);
                reportDataEntities.add(criteriaReportDataEntity);
            }

            if(StringUtils.isNotBlank(isbns)) {
                ReportDataEntity criteriaReportDataEntity = new ReportDataEntity();
                criteriaReportDataEntity.setHeaderValue(isbns);
                criteriaReportDataEntity.setHeaderName(header2);
                reportDataEntities.add(criteriaReportDataEntity);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
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
        logger.info("Total Time Taken to get "+ fieldName +" duplicates from solr : " + stopWatch.getTotalTimeSeconds());
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



}
