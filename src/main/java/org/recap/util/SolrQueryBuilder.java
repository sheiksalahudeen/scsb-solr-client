package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peris on 9/30/16.
 */

@Component
public class SolrQueryBuilder {

    String all = "*:*";

    String and = " AND ";

    String coreParentFilterQuery = "{!parent which=\"ContentType:parent\"}";

    String coreChildFilterQuery = "{!child of=\"ContentType:parent\"}";

    public String getQueryStringForItemCriteriaForParent(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> availability = searchRecordsRequest.getAvailability();
        List<String> collectionGroupDesignations = searchRecordsRequest.getCollectionGroupDesignations();
        List<String> useRestrictions = searchRecordsRequest.getUseRestrictions();

        if (CollectionUtils.isNotEmpty(availability)) {
            stringBuilder.append(buildQueryForParentGivenChild(RecapConstants.AVAILABILITY, availability, coreParentFilterQuery));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(and).append(buildQueryForParentGivenChild(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations, coreParentFilterQuery));
        } else if (CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(buildQueryForParentGivenChild(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations, coreParentFilterQuery));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(and).append(buildQueryForParentGivenChild(RecapConstants.USE_RESTRICTION, useRestrictions, coreParentFilterQuery));
        } else if (CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(buildQueryForParentGivenChild(RecapConstants.USE_RESTRICTION, useRestrictions, coreParentFilterQuery));
        }

        return stringBuilder.toString();
    }

    public String getQueryStringForParentCriteriaForChild(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> owningInstitutions = searchRecordsRequest.getOwningInstitutions();
        List<String> materialTypes = searchRecordsRequest.getMaterialTypes();

        if (CollectionUtils.isNotEmpty(owningInstitutions)) {
            stringBuilder.append(buildQueryForParentGivenChild(RecapConstants.BIB_OWNING_INSTITUTION, owningInstitutions, coreChildFilterQuery));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(materialTypes)) {
            stringBuilder.append(and).append(buildQueryForParentGivenChild(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes, coreChildFilterQuery));
        } else if (CollectionUtils.isNotEmpty(materialTypes)) {
            stringBuilder.append(buildQueryForParentGivenChild(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes, coreChildFilterQuery));
        }

        return stringBuilder.toString();
    }

    public String getQueryStringForMatchChildReturnParent(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> owningInstitutions = searchRecordsRequest.getOwningInstitutions();
        if (CollectionUtils.isNotEmpty(owningInstitutions)) {
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.BIB_OWNING_INSTITUTION, owningInstitutions));
        }

        List<String> materialTypes = searchRecordsRequest.getMaterialTypes();
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(materialTypes)) {
            stringBuilder.append(and).append(buildQueryForMatchChildReturnParent(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes));
        } else if (CollectionUtils.isNotEmpty(materialTypes)) {
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes));
        }
        return stringBuilder.toString();
    }

    public String getQueryStringForMatchParentReturnChild(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> availability = searchRecordsRequest.getAvailability();
        if (CollectionUtils.isNotEmpty(availability)) {
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.AVAILABILITY, availability));
        }

        List<String> collectionGroupDesignations = searchRecordsRequest.getCollectionGroupDesignations();
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(and).append(buildQueryForMatchChildReturnParent(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        } else if (CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        }

        List<String> useRestrictions = searchRecordsRequest.getUseRestrictions();
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(and).append(buildQueryForMatchChildReturnParent(RecapConstants.USE_RESTRICTION, useRestrictions));
        } else if (CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(and).append(buildQueryForMatchChildReturnParent(RecapConstants.USE_RESTRICTION, useRestrictions));
        }
        return stringBuilder.toString();
    }


    private String buildQueryForParentGivenChild(String fieldName, List<String> values, String parentQuery) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<String> iterator = values.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            stringBuilder.append(parentQuery).append(fieldName).append(":").append(value);
            if (iterator.hasNext()) {
                stringBuilder.append(" OR ");
            }
        }
        return "(" + stringBuilder.toString() + ")";
    }

    private String buildQueryForMatchChildReturnParent(String fieldName, List<String> values) {
        List<String> modifiedValues = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(values)) {
            for (String value : values) {
                modifiedValues.add("\"" + value + "\"");
            }
        }
        return fieldName + ":" + "(" + StringUtils.join(modifiedValues, " ") + ")";
    }

    /**
     * IF the getQueryForFieldCriteria() is called with Item field/value combination, the query would still return
     * only Bib Criteria. You will need to call getItemSolrQueryForCriteria()
     *
     * @throws Exception
     */
    public String getQueryForFieldCriteria(SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            return searchRecordsRequest.getFieldName() + ":" + searchRecordsRequest.getFieldValue() + and;
        }
        return "";
    }

    public String getCountQueryForFieldCriteria(SearchRecordsRequest searchRecordsRequest, String parentQuery) {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            stringBuilder.append(and).append(parentQuery).append(searchRecordsRequest.getFieldName()).append(":").append(searchRecordsRequest.getFieldValue());
            return stringBuilder.toString();
        }
        return "";
    }

    public SolrQuery getSolrQueryForBibItem(String parentQueryString, String docType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parentQueryString);
        stringBuilder.append(and).append("DocType:").append(docType);
        return new SolrQuery(stringBuilder.toString());
    }

    public SolrQuery getQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {

        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);

        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);

        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);

        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria + and + queryForFieldCriteria + queryStringForItemCriteriaForParent);

        return solrQuery;
    }

    public SolrQuery getQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {

        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);

        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);

        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);

        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria + and + queryForFieldCriteria + queryStringForParentCriteriaForChild);

        return solrQuery;
    }

    public SolrQuery getCountQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {

        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreParentFilterQuery);

        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);

        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);

        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria + and + queryStringForItemCriteriaForParent + countQueryForFieldCriteria);

        return solrQuery;
    }

    public SolrQuery getCountQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {

        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreChildFilterQuery);

        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);

        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);

        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria + and + queryStringForParentCriteriaForChild + countQueryForFieldCriteria);

        return solrQuery;
    }
}
