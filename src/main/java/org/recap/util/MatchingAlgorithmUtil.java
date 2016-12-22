package org.recap.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.recap.RecapConstants;
import org.recap.matchingAlgorithm.service.MatchingAlgorithmHelperService;
import org.recap.model.jpa.MatchingBibEntity;
import org.recap.model.jpa.ReportDataEntity;
import org.recap.model.jpa.ReportEntity;
import org.recap.model.solr.BibItem;
import org.recap.model.solr.Holdings;
import org.recap.model.solr.Item;
import org.recap.repository.solr.impl.BibSolrDocumentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created by angelind on 4/11/16.
 */
@Component
public class MatchingAlgorithmUtil {

    @Autowired
    SolrQueryBuilder solrQueryBuilder;

    @Autowired
    SolrTemplate solrTemplate;

    @Autowired
    BibSolrDocumentRepositoryImpl bibSolrDocumentRepository;

    @Autowired
    MatchingAlgorithmHelperService matchingAlgorithmHelperService;

    String or = " OR ";

    public List<ReportEntity> populateReportEntities(List<MatchingBibEntity> matchingBibEntities, String type) throws IOException, SolrServerException {

        List<ReportEntity> reportEntityList = new ArrayList<>();
        Map<Integer, List<ReportEntity>> reportEntityMap = new HashMap<>();
        Map<String, List<Item>> itemMap = new HashMap<>();
        Map<String, List<Holdings>> holdingsMap = new HashMap<>();
        List<List<MatchingBibEntity>> partitionedBibItems = Lists.partition(matchingBibEntities, 300);
        for (Iterator<List<MatchingBibEntity>> iterator = partitionedBibItems.iterator(); iterator.hasNext(); ) {
            List<MatchingBibEntity> matchingBibEntityList = iterator.next();
            SolrQuery solrQuery = solrQueryBuilder.getSolrQueryForBibItem("_root_:" + getRootIds(matchingBibEntityList));
            QueryResponse queryResponse = solrTemplate.getSolrClient().query(solrQuery);
            SolrDocumentList solrDocuments = queryResponse.getResults();
            if (solrDocuments.getNumFound() > 10) {
                solrQuery.setRows((int) solrDocuments.getNumFound());
                queryResponse = solrTemplate.getSolrClient().query(solrQuery);
                solrDocuments = queryResponse.getResults();
            }
            populateItemHoldingsInfo(itemMap, holdingsMap, solrDocuments);
        }

        getReportEntityMap(matchingBibEntities, reportEntityMap, itemMap, holdingsMap, type);

        for (Iterator<Integer> iterator = reportEntityMap.keySet().iterator(); iterator.hasNext(); ) {
            Integer bibId = iterator.next();
            reportEntityList.addAll(reportEntityMap.get(bibId));
        }
        return reportEntityList;
    }

    public void getReportEntityMap(List<MatchingBibEntity> matchingBibEntities, Map<Integer, List<ReportEntity>> reportEntityMap, Map<String, List<Item>> itemMap, Map<String, List<Holdings>> holdingsMap, String type) {
        for(MatchingBibEntity matchingBibEntity : matchingBibEntities) {
            List<Item> itemList = itemMap.get(matchingBibEntity.getRoot());
            List<Holdings> holdingsList = holdingsMap.get(matchingBibEntity.getRoot());
            Integer bibId = matchingBibEntity.getBibId();
            if(reportEntityMap.containsKey(bibId)) {
                List<ReportEntity> reportEntities = reportEntityMap.get(bibId);
                for(ReportEntity reportEntity : reportEntities) {
                    ReportDataEntity reportDataEntityForCriteria = getReportDataEntityForCriteria(matchingBibEntity);
                    reportEntity.addAll(Arrays.asList(reportDataEntityForCriteria));
                }
            } else {
                List<ReportEntity> reportEntities = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(itemList)) {
                    Holdings holdings = CollectionUtils.isNotEmpty(holdingsList) ? holdingsList.get(0) : new Holdings();
                    for(Item item : itemList) {
                        reportEntities.add(populateReportEntity(matchingBibEntity, item, holdings, type));
                    }
                    reportEntityMap.put(bibId, reportEntities);
                }
            }
        }
    }

    public String getRootIds(List<MatchingBibEntity> matchingBibEntities) {
        StringBuilder rootIds = new StringBuilder();
        rootIds.append("(");
        for (Iterator<MatchingBibEntity> iterator = matchingBibEntities.iterator(); iterator.hasNext(); ) {
            MatchingBibEntity matchingBibEntity = iterator.next();
            rootIds.append(matchingBibEntity.getRoot());
            if(iterator.hasNext()){
                rootIds.append(or);
            }
        }
        rootIds.append(")");
        return rootIds.toString();
    }

    public ReportEntity populateReportEntity(MatchingBibEntity matchingBibEntity, Item item, Holdings holdings, String type) {
        ReportEntity reportEntity = new ReportEntity();
        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity localBibIdReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_LOCAL_BIB_ID, String.valueOf(matchingBibEntity.getBibId()));
        if (localBibIdReportDataEntity != null) reportDataEntities.add(localBibIdReportDataEntity);

        ReportDataEntity owningInstBibIdReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_BIB_ID, matchingBibEntity.getOwningInstBibId());
        if (owningInstBibIdReportDataEntity != null) reportDataEntities.add(owningInstBibIdReportDataEntity);

        ReportDataEntity titleReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_TITLE, checkAndTruncateHeaderValue(matchingBibEntity.getTitle()));
        if (titleReportDataEntity != null) reportDataEntities.add(titleReportDataEntity);

        ReportDataEntity barcodeReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_BARCODE, item.getBarcode());
        if (barcodeReportDataEntity != null) reportDataEntities.add(barcodeReportDataEntity);

        ReportDataEntity volumePartYearReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_VOLUME_PART_YEAR, item.getVolumePartYear());
        if (volumePartYearReportDataEntity != null) reportDataEntities.add(volumePartYearReportDataEntity);

        ReportDataEntity institutionReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_INSTITUTION_ID, matchingBibEntity.getOwningInstitution());
        if (institutionReportDataEntity != null) reportDataEntities.add(institutionReportDataEntity);

        ReportDataEntity useRestrictionsReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_USE_RESTRICTIONS, item.getUseRestriction());
        if (useRestrictionsReportDataEntity != null) reportDataEntities.add(useRestrictionsReportDataEntity);

        ReportDataEntity summaryHoldingsReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_SUMMARY_HOLDINGS, checkAndTruncateHeaderValue(holdings.getSummaryHoldings()));
        if (summaryHoldingsReportDataEntity != null) reportDataEntities.add(summaryHoldingsReportDataEntity);

        ReportDataEntity materialTypeReportDataEntity = getReportDataEntity(RecapConstants.MATCHING_MATERIAL_TYPE, matchingBibEntity.getMaterialType());
        if (materialTypeReportDataEntity != null) reportDataEntities.add(materialTypeReportDataEntity);

        ReportDataEntity matchingFieldReportDataEntity = getReportDataEntityForCriteria(matchingBibEntity);
        if (matchingFieldReportDataEntity != null) reportDataEntities.add(matchingFieldReportDataEntity);
        reportEntity.setFileName(type.equalsIgnoreCase(RecapConstants.MATCHING_TYPE) ? RecapConstants.MATCHING_ALGO_FULL_FILE_NAME : RecapConstants.EXCEPTION_REPORT_FILE_NAME);
        reportEntity.setInstitutionName(RecapConstants.ALL_INST);
        reportEntity.setType(type);
        reportEntity.setCreatedDate(new Date());
        reportEntity.addAll(reportDataEntities);
        return reportEntity;
    }

    public void getMatchingEntitiesMap(Map<String, List<MatchingBibEntity>> matchingMap, MatchingBibEntity matchingBibEntity, String matchingValue) {
        if(matchingMap.containsKey(matchingValue)) {
            List<MatchingBibEntity> matchingBibEntityList = new ArrayList<>();
            matchingBibEntityList.addAll(matchingMap.get(matchingValue));
            matchingBibEntityList.add(matchingBibEntity);
            matchingMap.put(matchingValue, matchingBibEntityList);
        } else {
            matchingMap.put(matchingValue, Arrays.asList(matchingBibEntity));
        }
    }

    public Set<MatchingBibEntity> getUnMatchingBibsAfterTitleVerification(List<MatchingBibEntity> matchingBibEntities) {
        Set<MatchingBibEntity> unMatchedBibEntitySet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(matchingBibEntities)) {
            for (int i = 0; i < matchingBibEntities.size(); i++) {
                for (int j = 0; j < matchingBibEntities.size(); j++) {
                    if (i != j) {
                        MatchingBibEntity tempMatchingBibEntity1 = matchingBibEntities.get(i);
                        MatchingBibEntity tempMatchingBibEntity2 = matchingBibEntities.get(j);
                        String tempTitle1 = tempMatchingBibEntity1.getTitle();
                        String tempTitle2 = tempMatchingBibEntity2.getTitle();
                        if (StringUtils.isNotBlank(tempTitle1) && StringUtils.isNotBlank(tempTitle2)) {
                            tempTitle1 = tempTitle1.replaceAll("[^\\w\\s]", " ").trim();
                            tempTitle2 = tempTitle2.replaceAll("[^\\w\\s]", " ").trim();
                            String title1 = matchingAlgorithmHelperService.getTitleToMatch(tempTitle1.replaceAll("\\s{2,}", " "));
                            String title2 = matchingAlgorithmHelperService.getTitleToMatch(tempTitle2.replaceAll("\\s{2,}", " "));
                            if (!title1.equalsIgnoreCase(title2)) {
                                unMatchedBibEntitySet.add(tempMatchingBibEntity2);
                                unMatchedBibEntitySet.add(tempMatchingBibEntity1);
                            }
                        }
                    }
                }
            }
        }
        return unMatchedBibEntitySet;
    }

    private ReportDataEntity getReportDataEntity(String headerName, String headerValue) {
        if (StringUtils.isNotBlank(headerName) && StringUtils.isNotBlank(headerValue)) {
            ReportDataEntity reportDataEntity = new ReportDataEntity();
            reportDataEntity.setHeaderName(headerName);
            reportDataEntity.setHeaderValue(headerValue);
            return reportDataEntity;
        }
        return null;
    }

    private ReportDataEntity getReportDataEntityForCriteria(MatchingBibEntity matchingBibEntity) {
        String criteria = matchingBibEntity.getMatching();
        ReportDataEntity reportDataEntity = null;
        if (criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_OCLC)) {
            reportDataEntity = getReportDataEntity(RecapConstants.MATCHING_OCLC, matchingBibEntity.getOclc());
        } else if (criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISBN)) {
            reportDataEntity = getReportDataEntity(RecapConstants.MATCHING_ISBN, matchingBibEntity.getIsbn());
        } else if (criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_ISSN)) {
            reportDataEntity = getReportDataEntity(RecapConstants.MATCHING_ISSN, matchingBibEntity.getIssn());
        } else if (criteria.equalsIgnoreCase(RecapConstants.MATCH_POINT_FIELD_LCCN)) {
            reportDataEntity = getReportDataEntity(RecapConstants.MATCHING_LCCN, matchingBibEntity.getLccn());
        }
        return reportDataEntity;
    }

    private void populateItemHoldingsInfo(Map<String, List<Item>> itemMap, Map<String, List<Holdings>> holdingsMap, SolrDocumentList solrDocuments) {
        for (Iterator<SolrDocument> iterator = solrDocuments.iterator(); iterator.hasNext(); ) {
            SolrDocument solrDocument = iterator.next();
            String docType = (String) solrDocument.getFieldValue(RecapConstants.DOCTYPE);
            if (docType.equalsIgnoreCase(RecapConstants.ITEM)) {
                String fieldValue = (String) solrDocument.getFieldValue(RecapConstants.COLLECTION_GROUP_DESIGNATION);
                if (fieldValue.equalsIgnoreCase(RecapConstants.SHARED_CGD)) {
                    Item item = bibSolrDocumentRepository.getItem(solrDocument);
                    String itemRoot = item.getRoot();
                    if(itemMap.containsKey(itemRoot)) {
                        List<Item> itemList = new ArrayList<>();
                        itemList.addAll(itemMap.get(itemRoot));
                        if(!itemList.contains(item.getItemId())) {
                            itemList.add(item);
                            itemMap.put(itemRoot, itemList);
                        }
                    } else {
                        itemMap.put(itemRoot, Arrays.asList(item));
                    }
                }
            }
            if (docType.equalsIgnoreCase(RecapConstants.HOLDINGS)) {
                Holdings holdings = bibSolrDocumentRepository.getHoldings(solrDocument);
                String holdingsRoot = holdings.getRoot();
                if(holdingsMap.containsKey(holdingsRoot)) {
                    List<Holdings> holdingsList = new ArrayList<>();
                    holdingsList.addAll(holdingsMap.get(holdingsRoot));
                    if(!holdingsList.contains(holdings.getHoldingsId())) {
                        holdingsList.add(holdings);
                        holdingsMap.put(holdingsRoot, holdingsList);
                    }
                } else {
                    holdingsMap.put(holdingsRoot, Arrays.asList(holdings));
                }
            }
        }
    }

    private String checkAndTruncateHeaderValue(String headerValue) {
        if (StringUtils.isNotBlank(headerValue) && headerValue.length() > 7999) {
            String headerValueSubString = headerValue.substring(0, 7996);
            headerValueSubString = headerValueSubString.concat("...");
            return headerValueSubString;
        }
        return headerValue;
    }
}
