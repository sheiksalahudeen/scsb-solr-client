package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peris on 9/30/16.
 */

@Component
public class SolrQueryBuilder {

    String all = "*:*";

    String and = " AND ";

    public SolrQuery getQueryForBibSpecificFieldSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldName())
                .append(":")
                .append(searchRecordsRequest.getFieldValue())
                .append(and).append(queryStringForBibCriteria);
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());

        return solrQuery;
    }

    public SolrQuery getQueryForItemSpecificFieldSpecificValue(String parentQueryString, SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForItemCriteria = getQueryStringForItemCriteria(searchRecordsRequest);
        if (StringUtils.isNotBlank(parentQueryString)) {
            strBuilder
                    .append(parentQueryString)
                    .append(and);
        }
        strBuilder
                .append(searchRecordsRequest.getFieldName())
                .append(":")
                .append(searchRecordsRequest.getFieldValue())
                .append(and)
                .append(queryStringForItemCriteria);

        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        return solrQuery;
    }

    public SolrQuery getQueryForAllFieldsSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldValue()).append(and).append(queryStringForBibCriteria);
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        return solrQuery;
    }

    public SolrQuery getQueryForAllFieldsNoValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(all);
        if (StringUtils.isNotBlank(queryStringForBibCriteria)) {
            strBuilder.append(and).append(queryStringForBibCriteria);
        }
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber() * searchRecordsRequest.getPageSize());
        return solrQuery;
    }


    public String getQueryStringForItemCriteria(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> availability = searchRecordsRequest.getAvailability();
        List<String> collectionGroupDesignations = searchRecordsRequest.getCollectionGroupDesignations();
        List<String> useRestrictions = searchRecordsRequest.getUseRestrictions();

        if (CollectionUtils.isNotEmpty(availability)) {
            stringBuilder.append(buildQueryForCriteriaField(RecapConstants.AVAILABILITY, availability));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(and).append(buildQueryForCriteriaField(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        } else if (CollectionUtils.isNotEmpty(collectionGroupDesignations)){
            stringBuilder.append(buildQueryForCriteriaField(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(and).append(buildQueryForCriteriaField(RecapConstants.USE_RESTRICTION, useRestrictions));
        } else if (CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(buildQueryForCriteriaField(RecapConstants.USE_RESTRICTION, useRestrictions));
        }

        return stringBuilder.toString();
    }

    public String getQueryStringForBibCriteria(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strinBuilder = new StringBuilder();
        List<String> owningInstitutions = searchRecordsRequest.getOwningInstitutions();
        if (CollectionUtils.isNotEmpty(owningInstitutions)) {
            strinBuilder.append(buildQueryForCriteriaField(RecapConstants.BIB_OWNING_INSTITUTION, owningInstitutions));
        }

        List<String> materialTypes = searchRecordsRequest.getMaterialTypes();
        if (StringUtils.isNotBlank(strinBuilder.toString()) && CollectionUtils.isNotEmpty(materialTypes)) {
            strinBuilder.append(and).append(buildQueryForCriteriaField(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes));
        }else if (CollectionUtils.isNotEmpty(materialTypes)) {
            strinBuilder.append(buildQueryForCriteriaField(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes));
        }
        return strinBuilder.toString();
    }


    private String buildQueryForCriteriaField(String fieldName, List<String> values) {
        List<String> modifiedValues = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(values)) {
            for (String value : values) {
                modifiedValues.add("\"" + value + "\"");
            }
        }
        return fieldName + ":" + "(" + StringUtils.join(modifiedValues, " ") + ")";
    }

    /**
     * IF the getSolrQueryForCriteria() is called with Item field/value combination, the query would still return
     * only Bib Criteria. You will need to call getItemSolrQueryForCriteria()
     * @throws Exception
     */
    public SolrQuery getSolrQueryForCriteria(SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isBlank(searchRecordsRequest.getFieldValue())) {
            return getQueryForAllFieldsNoValue(searchRecordsRequest);
        } else if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            return getQueryForAllFieldsSpecificValue(searchRecordsRequest);
        } else if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            return getQueryForBibSpecificFieldSpecificValue(searchRecordsRequest);
        }

        return null;
    }

    public SolrQuery getItemSolrQueryForCriteria(String parentQueryString, SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isBlank(searchRecordsRequest.getFieldValue())) {
            SolrQuery solrQuery = getSolrQueryForItem(parentQueryString, searchRecordsRequest);
            return solrQuery;
        } else if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            SolrQuery solrQuery = getSolrQueryForItem(parentQueryString, searchRecordsRequest);
            return solrQuery;
        } else if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            SolrQuery quryForItemSpecificFieldSpecificValue = getQueryForItemSpecificFieldSpecificValue(parentQueryString, searchRecordsRequest);

            return quryForItemSpecificFieldSpecificValue;
        }
        return null;
    }

    private SolrQuery getSolrQueryForItem(String parentQueryString, SearchRecordsRequest searchRecordsRequest) {
        String queryStringForItemCriteria = getQueryStringForItemCriteria(searchRecordsRequest);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parentQueryString);
        if(StringUtils.isNotBlank(queryStringForItemCriteria)){
            stringBuilder.append(and).append(queryStringForItemCriteria);
        }
        return new SolrQuery(stringBuilder.toString());
    }
}
