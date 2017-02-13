package org.recap.util;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.recap.RecapConstants;
import org.recap.camel.activemq.JmxHelper;
import org.recap.matchingAlgorithm.MatchingAlgorithmCGDProcessor;
import org.recap.model.jpa.*;
import org.recap.model.search.resolver.BibValueResolver;
import org.recap.model.search.resolver.impl.Bib.*;
import org.recap.model.solr.BibItem;
import org.recap.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by angelind on 2/2/17.
 */
@Component
public class OngoingMatchingAlgorithmUtil {

    Logger logger = LoggerFactory.getLogger(OngoingMatchingAlgorithmUtil.class);

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    MatchingAlgorithmUtil matchingAlgorithmUtil;

    @Autowired
    JmxHelper jmxHelper;

    @Autowired
    UpdateCgdUtil updateCgdUtil;

    private List<BibValueResolver> bibValueResolvers;
    private Map collectionGroupMap;
    private Map institutionMap;


    public void processMatchingForBib(SolrInputDocument solrInputDocument) {
        logger.info("Ongoing Matching Started");
        Map<Integer, BibItem> bibItemMap = new HashMap<>();
        Map<Integer, BibItem> tempMap;
        Set<String> matchPointString = new HashSet<>();
        List<Integer> itemIds = new ArrayList<>();

        tempMap = findMatchingBibs(solrInputDocument, matchPointString, RecapConstants.OCLC_NUMBER);
        if(tempMap != null && tempMap.size() > 0) bibItemMap.putAll(tempMap);

        tempMap = findMatchingBibs(solrInputDocument, matchPointString, RecapConstants.ISBN_CRITERIA);
        if(tempMap != null && tempMap.size() > 0) bibItemMap.putAll(tempMap);

        tempMap = findMatchingBibs(solrInputDocument, matchPointString, RecapConstants.ISSN_CRITERIA);
        if(tempMap != null && tempMap.size() > 0) bibItemMap.putAll(tempMap);

        tempMap = findMatchingBibs(solrInputDocument, matchPointString, RecapConstants.LCCN_CRITERIA);
        if(tempMap != null && tempMap.size() > 0) bibItemMap.putAll(tempMap);

        if(bibItemMap != null && bibItemMap.size() > 0) {
            if(matchPointString.size() > 1) {
                // Multi Match
                logger.info("Multi Match Found.");
                try {
                    itemIds = saveReportAndUpdateCGDForMultiMatch(bibItemMap);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } catch (SolrServerException e) {
                    logger.error(e.getMessage());
                }
            } else if(matchPointString.size() == 1) {
                // Single Match
                logger.info("Single Match Found.");
                try {
                    itemIds = saveReportAndUpdateCGDForSingleMatch(bibItemMap, matchPointString.iterator().next());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            DestinationViewMBean updateItemsQ = jmxHelper.getBeanForQueueName("updateItemsQ");

            while (updateItemsQ.getQueueSize() != 0) {
                //Waiting for the updateItemQ messages finish processing
            }

            if(CollectionUtils.isNotEmpty(itemIds)) {
                updateCGDForItemInSolr(itemIds);
            }
        }
    }

    public void updateCGDForItemInSolr(List<Integer> itemIds) {
        if (CollectionUtils.isNotEmpty(itemIds)) {
            List<ItemEntity> itemEntities = itemDetailsRepository.findByItemIdIn(itemIds);
            updateCgdUtil.updateCGDForItemInSolr(itemEntities);
        }
    }

    private List<Integer> saveReportAndUpdateCGDForSingleMatch(Map<Integer, BibItem> bibItemMap, String matchPointString) {
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Set<String> owningInstSet = new HashSet<>();
        Set<String> materialTypeSet = new HashSet<>();
        List<Integer> bibIds = new ArrayList<>();
        List<String> owningInstList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        Map<String,String> titleMap = new HashMap<>();
        Set<String> unMatchingTitleHeaderSet = new HashSet<>();
        List<String> owningInstBibIds = new ArrayList<>();
        List<Integer> itemIds = new ArrayList<>();
        List<String> criteriaValues = new ArrayList<>();

        int index=0;
        for (Iterator<Integer> iterator = bibItemMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            BibItem bibItem = bibItemMap.get(bibId);
            owningInstSet.add(bibItem.getOwningInstitution());
            owningInstList.add(bibItem.getOwningInstitution());
            owningInstBibIds.add(bibItem.getOwningInstitutionBibId());
            bibIds.add(bibId);
            materialTypeList.add(bibItem.getMaterialType());
            materialTypeSet.add(bibItem.getMaterialType());
            if(matchPointString.equalsIgnoreCase(RecapConstants.OCLC_NUMBER)) {
                criteriaValues.addAll(bibItem.getOclcNumber());
            } else if(matchPointString.equalsIgnoreCase(RecapConstants.ISBN_CRITERIA)) {
                criteriaValues.addAll(bibItem.getIsbn());
            } else if(matchPointString.equalsIgnoreCase(RecapConstants.ISSN_CRITERIA)) {
                criteriaValues.addAll(bibItem.getIssn());
            } else if(matchPointString.equalsIgnoreCase(RecapConstants.LCCN_CRITERIA)) {
                criteriaValues.add(bibItem.getLccn());
            }
            index = index + 1;
            if(StringUtils.isNotBlank(bibItem.getTitleDisplay())) {
                String titleHeader = "Title" + index;
                matchingAlgorithmUtil.getReportDataEntity(titleHeader, bibItem.getTitleDisplay(), reportDataEntities);
                titleMap.put(titleHeader, bibItem.getTitleDisplay());
            }
        }

        if(owningInstSet.size() > 1) {
            ReportEntity reportEntity = new ReportEntity();
            String fileName = RecapConstants.ONGOING_MATCHING_ALGORITHM;
            reportEntity.setFileName(fileName);
            reportEntity.setInstitutionName(RecapConstants.ALL_INST);
            reportEntity.setCreatedDate(new Date());
            if(materialTypeSet.size() == 1) {
                Set<String> matchingTitleHeaderSet = matchingAlgorithmUtil.getMatchingAndUnMatchingBibsOnTitleVerification(titleMap, unMatchingTitleHeaderSet);
                if(CollectionUtils.isNotEmpty(matchingTitleHeaderSet) && CollectionUtils.isNotEmpty(unMatchingTitleHeaderSet) && matchingTitleHeaderSet.size() != titleMap.size()) {
                    reportEntity.setType("TitleException");

                    itemIds = processCGDAndReportsForMatchingTitles(fileName, titleMap, StringUtils.join(bibIds).split(","),
                            StringUtils.join(materialTypeList).split(","), StringUtils.join(owningInstSet).split(","), StringUtils.join(owningInstBibIds).split(","),
                            StringUtils.join(criteriaValues, ","), matchingTitleHeaderSet, matchPointString);
                } else if(CollectionUtils.isEmpty(matchingTitleHeaderSet) && CollectionUtils.isNotEmpty(unMatchingTitleHeaderSet)) {
                    reportEntity.setType("TitleException");
                } else {
                    reportEntity.setType("SingleMatch");
                    try {
                        itemIds = checkForMonographAndUpdateCGD(reportEntity, bibIds, materialTypeList, materialTypeSet);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } else {
                reportEntity.setType("MaterialTypeException");
            }
            matchingAlgorithmUtil.getReportDataEntityList(reportDataEntities, owningInstList, bibIds, materialTypeList, owningInstBibIds);
            matchingAlgorithmUtil.getReportDataEntity(StringUtils.join(criteriaValues, ","), matchPointString, reportDataEntities);
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
        return itemIds;
    }

    public List<Integer> processCGDAndReportsForMatchingTitles(String fileName, Map<String, String> titleMap, String[] bibIds, String[] materialTypes, String[] owningInstitutions,
                                                                      String[] owningInstBibIds, String matchPointValue, Set<String> matchingTitleHeaderSet, String matchPointString) {
        List<Integer> itemIds = new ArrayList<>();
        ReportEntity matchingReportEntity = new ReportEntity();
        matchingReportEntity.setType("SingleMatch");
        matchingReportEntity.setCreatedDate(new Date());
        matchingReportEntity.setInstitutionName(RecapConstants.ALL_INST);
        matchingReportEntity.setFileName(fileName);
        List<ReportDataEntity> reportDataEntityList = new ArrayList<>();
        List<String> bibIdList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        List<String> owningInstitutionList = new ArrayList<>();
        List<String> owningInstBibIdList = new ArrayList<>();
        Set<String> materialTypesSet = new HashSet<>();

        matchingAlgorithmUtil.prepareReportForMatchingTitles(titleMap, bibIds, materialTypes, owningInstitutions, owningInstBibIds, matchingTitleHeaderSet, reportDataEntityList, bibIdList, materialTypeList, owningInstitutionList, owningInstBibIdList);

        List<Integer> bibliographicIds = new ArrayList<>();
        for (Iterator<String> iterator = bibIdList.iterator(); iterator.hasNext(); ) {
            String bibId = iterator.next();
            bibliographicIds.add(Integer.valueOf(bibId));
        }

        try {
            itemIds = checkForMonographAndUpdateCGD(matchingReportEntity, bibliographicIds, materialTypeList, materialTypesSet);
            matchingAlgorithmUtil.getReportDataEntityList(reportDataEntityList, owningInstitutionList, bibIdList, materialTypeList, owningInstBibIdList);
            matchingAlgorithmUtil.getReportDataEntity(matchPointValue, matchPointString, reportDataEntityList);
            matchingReportEntity.addAll(reportDataEntityList);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(matchingReportEntity));
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (SolrServerException e) {
            logger.error(e.getMessage());
        }
        return itemIds;
    }

    private List<Integer> saveReportAndUpdateCGDForMultiMatch(Map<Integer, BibItem> bibItemMap) throws IOException, SolrServerException {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(RecapConstants.ONGOING_MATCHING_ALGORITHM);
        reportEntity.setCreatedDate(new Date());
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();
        Set<String> owningInstSet = new HashSet<>();
        List<String> owningInstList = new ArrayList<>();
        List<Integer> bibIdList = new ArrayList<>();
        List<String> materialTypeList = new ArrayList<>();
        Set<String> materialTypes = new HashSet<>();
        List<String> owningInstBibIds = new ArrayList<>();
        Set<String> oclcNumbers = new HashSet<>();
        Set<String> isbns = new HashSet<>();
        Set<String> issns = new HashSet<>();
        Set<String> lccns = new HashSet<>();
        List<Integer> itemIds = new ArrayList<>();

        for (Iterator<Integer> iterator = bibItemMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            BibItem bibItem = bibItemMap.get(bibId);
            owningInstSet.add(bibItem.getOwningInstitution());
            owningInstList.add(bibItem.getOwningInstitution());
            bibIdList.add(bibItem.getBibId());
            materialTypes.add(bibItem.getLeaderMaterialType());
            materialTypeList.add(bibItem.getLeaderMaterialType());
            owningInstBibIds.add(bibItem.getOwningInstitutionBibId());
            if(CollectionUtils.isNotEmpty(bibItem.getOclcNumber())) oclcNumbers.addAll(bibItem.getOclcNumber());
            if(CollectionUtils.isNotEmpty(bibItem.getIsbn())) isbns.addAll(bibItem.getIsbn());
            if(CollectionUtils.isNotEmpty(bibItem.getIssn())) issns.addAll(bibItem.getIssn());
            if(StringUtils.isNotBlank(bibItem.getLccn())) lccns.add(bibItem.getLccn());
        }

        if(owningInstSet.size() > 1) {
            itemIds = checkForMonographAndUpdateCGD(reportEntity, bibIdList, materialTypeList, materialTypes);
            matchingAlgorithmUtil.getReportDataEntityList(reportDataEntities, owningInstList, bibIdList, materialTypeList, owningInstBibIds);

            if(CollectionUtils.isNotEmpty(oclcNumbers)) {
                ReportDataEntity oclcNumberReportDataEntity = matchingAlgorithmUtil.getReportDataEntityForCollectionValues(oclcNumbers, RecapConstants.OCLC_NUMBER);
                reportDataEntities.add(oclcNumberReportDataEntity);
            }
            if(CollectionUtils.isNotEmpty(isbns)) {
                ReportDataEntity isbnReportDataEntity = matchingAlgorithmUtil.getReportDataEntityForCollectionValues(isbns, RecapConstants.ISBN_CRITERIA);
                reportDataEntities.add(isbnReportDataEntity);
            }
            if(CollectionUtils.isNotEmpty(issns)) {
                ReportDataEntity issnReportDataEntity = matchingAlgorithmUtil.getReportDataEntityForCollectionValues(issns, RecapConstants.ISSN_CRITERIA);
                reportDataEntities.add(issnReportDataEntity);
            }
            if(CollectionUtils.isNotEmpty(lccns)) {
                ReportDataEntity lccnReportDataEntity = matchingAlgorithmUtil.getReportDataEntityForCollectionValues(lccns, RecapConstants.LCCN_CRITERIA);
                reportDataEntities.add(lccnReportDataEntity);
            }
            reportEntity.addAll(reportDataEntities);
            producerTemplate.sendBody("scsbactivemq:queue:saveMatchingReportsQ", Arrays.asList(reportEntity));
        }
        return itemIds;
    }

    private List<Integer> checkForMonographAndUpdateCGD(ReportEntity reportEntity, List<Integer> bibIdList, List<String> materialTypeList, Set<String> materialTypes) throws IOException, SolrServerException {
        List<Integer> itemIds = new ArrayList<>();
        if(materialTypes.size() == 1) {
            reportEntity.setType("MultiMatch");
            matchingAlgorithmUtil.populateMatchingCounter();
            Map<Integer, Map<Integer, List<ItemEntity>>> useRestrictionMap = new HashMap<>();
            Map<Integer, ItemEntity> itemEntityMap = new HashMap<>();
            MatchingAlgorithmCGDProcessor matchingAlgorithmCGDProcessor = new MatchingAlgorithmCGDProcessor(bibliographicDetailsRepository, producerTemplate,
                    getCollectionGroupMap(), getInstitutionEntityMap(), itemChangeLogDetailsRepository, RecapConstants.ONGOING_MATCHING_OPERATION_TYPE, collectionGroupDetailsRepository);
            boolean isMonograph = matchingAlgorithmCGDProcessor.checkForMonographAndPopulateValues(materialTypes, useRestrictionMap, itemEntityMap, bibIdList);
            if(isMonograph) {
                matchingAlgorithmCGDProcessor.updateCGDProcess(useRestrictionMap, itemEntityMap);
                itemIds.addAll(itemEntityMap.keySet());
            } else {
                if(materialTypes.size() > 1) {
                    reportEntity.setType("MaterialTypeException");
                } else if(materialTypes.size() == 1){
                    reportEntity.setType("MultiMatch");
                    if(materialTypes.contains(RecapConstants.MONOGRAPHIC_SET)) {
                        int size = materialTypeList.size();
                        materialTypeList = new ArrayList<>();
                        for(int i = 0; i < size; i++) {
                            materialTypeList.add(RecapConstants.MONOGRAPHIC_SET);
                        }
                    }
                }
            }
        } else {
            reportEntity.setType("MaterialTypeException");
        }
        return itemIds;
    }


    private Map<Integer, BibItem> findMatchingBibs(SolrInputDocument solrInputDocument, Set<String> matchPointString, String fieldName) {
        Map<Integer, BibItem> bibItemMap = null;
        SolrInputField solrInputField = solrInputDocument.get(fieldName);
        if(solrInputField != null) {
            Object value = solrInputField.getValue();
            if(value instanceof String) {
                String fieldValue = (String) value;
                if(StringUtils.isNotBlank(fieldValue)) {
                    String query = solrQueryBuilder.solrQueryForOngoingMatching(fieldName, fieldValue);
                    bibItemMap = getBibsFromSolr(matchPointString, fieldName, query);
                }
            } else if(value instanceof List) {
                List<String> fieldValues = (List<String>) value;
                if(CollectionUtils.isNotEmpty(fieldValues)) {
                    String query = solrQueryBuilder.solrQueryForOngoingMatching(fieldName, fieldValues);
                    bibItemMap = getBibsFromSolr(matchPointString, fieldName, query);
                }
            }
        }
        return bibItemMap;
    }

    private Map<Integer, BibItem> getBibsFromSolr(Set<String> matchPointString, String fieldName, String query) {
        Map<Integer, BibItem> bibItemMap = new HashMap<>();
        SolrQuery solrQuery = new SolrQuery(query);
        try {
            QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
            SolrDocumentList solrDocumentList = queryResponse.getResults();
            long numFound = solrDocumentList.getNumFound();
            if(numFound > 1) {
                matchPointString.add(fieldName);
                if(numFound > solrDocumentList.size()) {
                    solrQuery.setRows((int) numFound);
                    queryResponse = solrTemplate.getSolrClient().query(solrQuery);
                    solrDocumentList = queryResponse.getResults();
                }
                for (Iterator<SolrDocument> iterator = solrDocumentList.iterator(); iterator.hasNext(); ) {
                    SolrDocument solrDocument = iterator.next();
                    BibItem bibItem = populateBibItem(solrDocument);
                    bibItemMap.put(bibItem.getBibId(), bibItem);
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bibItemMap;
    }

    public BibItem populateBibItem(SolrDocument solrDocument) {
        Collection<String> fieldNames = solrDocument.getFieldNames();
        BibItem bibItem = new BibItem();
        for (Iterator<String> stringIterator = fieldNames.iterator(); stringIterator.hasNext(); ) {
            String fieldName = stringIterator.next();
            Object fieldValue = solrDocument.getFieldValue(fieldName);
            for (Iterator<BibValueResolver> valueResolverIterator = getBibValueResolvers().iterator(); valueResolverIterator.hasNext(); ) {
                BibValueResolver valueResolver = valueResolverIterator.next();
                if (valueResolver.isInterested(fieldName)) {
                    valueResolver.setValue(bibItem, fieldValue);
                }
            }
        }
        return bibItem;
    }

    public List<BibValueResolver> getBibValueResolvers() {
        if (null == bibValueResolvers) {
            bibValueResolvers = new ArrayList<>();
            bibValueResolvers.add(new BibIdValueResolver());
            bibValueResolvers.add(new IdValueResolver());
            bibValueResolvers.add(new ISBNValueResolver());
            bibValueResolvers.add(new ISSNValueResolver());
            bibValueResolvers.add(new LCCNValueResolver());
            bibValueResolvers.add(new LeaderMaterialTypeValueResolver());
            bibValueResolvers.add(new OCLCValueResolver());
            bibValueResolvers.add(new OwningInstitutionBibIdValueResolver());
            bibValueResolvers.add(new OwningInstitutionValueResolver());
            bibValueResolvers.add(new TitleDisplayValueResolver());
            bibValueResolvers.add(new IsDeletedBibValueResolver());
        }
        return bibValueResolvers;
    }

    public Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
            for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                CollectionGroupEntity collectionGroupEntity = iterator.next();
                collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
            }
        }
        return collectionGroupMap;
    }

    public Map getInstitutionEntityMap() {
        if (null == institutionMap) {
            institutionMap = new HashMap();
            Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
            for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                InstitutionEntity institutionEntity = iterator.next();
                institutionMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getInstitutionId());
            }
        }
        return institutionMap;
    }
}
