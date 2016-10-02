package org.recap.util;

import org.apache.camel.language.Bean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peris on 9/30/16.
 */

@Component
public class SolrQureyBuilder {

    String all = "*:*";

    String and = " AND ";

    public SolrQuery getQuryForBibSpecificFieldSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldName())
                .append(":")
                .append(searchRecordsRequest.getFieldValue())
                .append(and).append(queryStringForBibCriteria);
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber());

        return solrQuery;
    }

    public SolrQuery getQuryForItemSpecificFieldSpecificValue(String parentQueryString, SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForItemCriteria = getQueryStringForItemCriteria(searchRecordsRequest);
        strBuilder
                .append(parentQueryString)
                .append(and)
                .append(searchRecordsRequest.getFieldName())
                .append(":")
                .append(searchRecordsRequest.getFieldValue())
                .append(and)
                .append(queryStringForItemCriteria);

        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        return solrQuery;
    }

    public SolrQuery getQuryForAllFieldsSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldValue()).append(and).append(queryStringForBibCriteria);
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber());
        return solrQuery;
    }

    public SolrQuery getQuryForAllFieldsNoValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(all);
        if (StringUtils.isNotBlank(queryStringForBibCriteria)) {
            strBuilder.append(and).append(queryStringForBibCriteria);
        }
        SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
        solrQuery.setRows(searchRecordsRequest.getPageSize());
        solrQuery.setStart(searchRecordsRequest.getPageNumber());
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

    private String getQueryStringForBibCriteria(SearchRecordsRequest searchRecordsRequest) {
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
            return getQuryForAllFieldsNoValue(searchRecordsRequest);
        } else if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            return getQuryForAllFieldsSpecificValue(searchRecordsRequest);
        } else if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())
                && (!searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.BARCODE) && !searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.CALL_NUMBER))) {
            return getQuryForBibSpecificFieldSpecificValue(searchRecordsRequest);
        } else {
            StringBuilder strBuilder = new StringBuilder();
            String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
            strBuilder.append(queryStringForBibCriteria);
            SolrQuery solrQuery = new SolrQuery(strBuilder.toString());
            solrQuery.setRows(searchRecordsRequest.getPageSize());
            solrQuery.setStart(searchRecordsRequest.getPageNumber());
            return solrQuery;
        }
    }

    public SolrQuery getItemSolrQueryForCriteria(String parentQueryString, SearchRecordsRequest searchRecordsRequest) {
        if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isBlank(searchRecordsRequest.getFieldValue())) {
            String queryStringForItemCriteria = getQueryStringForItemCriteria(searchRecordsRequest);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(parentQueryString);
            if(StringUtils.isNotBlank(queryStringForItemCriteria)){
                stringBuilder.append(and).append(queryStringForItemCriteria);
            }
            SolrQuery solrQuery = new SolrQuery(stringBuilder.toString());
            return solrQuery;
        } else if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())) {
            String queryStringForItemCriteria = getQueryStringForItemCriteria(searchRecordsRequest);
            SolrQuery solrQuery = new SolrQuery(parentQueryString+and+queryStringForItemCriteria);
            return solrQuery;
        } else if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName())
                && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())
                && (searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.BARCODE) || searchRecordsRequest.getFieldName().equalsIgnoreCase(RecapConstants.CALL_NUMBER))) {
            return getQuryForItemSpecificFieldSpecificValue(parentQueryString, searchRecordsRequest);
        }
        return null;
    }
}
