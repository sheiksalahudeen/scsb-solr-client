package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peris on 9/30/16.
 */
public class SolrQureyBuilder {

    String fq = "&fq=";

    String fl = "&fl=";

    String flQ = "*,[child parentFilter=DocType:Bib]";

    String all = "*:*";

    String and = " AND ";

    String coreFilterQuery = "{!parent which=\"DocType:Bib\"}";

    public SolrQuery getQuryForSpecificFieldSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldName())
                .append(":")
                .append(searchRecordsRequest.getFieldValue())
                .append(and).append(queryStringForBibCriteria);

        return getSolrQuery(searchRecordsRequest, strBuilder.toString() );
    }

    public SolrQuery getQuryForAllFieldsSpecificValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(searchRecordsRequest.getFieldValue()).append(and).append(queryStringForBibCriteria);

        return getSolrQuery(searchRecordsRequest, strBuilder.toString() );
    }

    public SolrQuery getQuryForAllFieldsNoValue(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strBuilder = new StringBuilder();
        String queryStringForBibCriteria = getQueryStringForBibCriteria(searchRecordsRequest);
        strBuilder.append(all).append(and).append(queryStringForBibCriteria);

        return getSolrQuery(searchRecordsRequest, strBuilder.toString() );
    }

    private SolrQuery getSolrQuery(SearchRecordsRequest searchRecordsRequest, String queryString) {
        SolrQuery solrQuery = new SolrQuery(queryString);
        solrQuery.addFilterQuery(coreFilterQuery + getQueryStringForItemCriteria(searchRecordsRequest));

        solrQuery.setParam("fl", flQ);
        return solrQuery;
    }

    private String getQueryStringForItemCriteria(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> availability = searchRecordsRequest.getAvailability();
        List<String> collectionGroupDesignations = searchRecordsRequest.getCollectionGroupDesignations();
        List<String> useRestrictions = searchRecordsRequest.getUseRestrictions();

        stringBuilder.append(buildQueryForCriteriaField(RecapConstants.AVAILABILITY, availability))
        .append(and).append(buildQueryForCriteriaField(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations))
        .append(and).append(buildQueryForCriteriaField(RecapConstants.USE_RESTRICTION, useRestrictions));

        return stringBuilder.toString();
    }

    private String getQueryStringForBibCriteria(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder strinBuilder = new StringBuilder();
        List<String> owningInstitutions = searchRecordsRequest.getOwningInstitutions();
        strinBuilder.append(buildQueryForCriteriaField(RecapConstants.BIB_OWNING_INSTITUTION, owningInstitutions));
        List<String> materialTypes = searchRecordsRequest.getMaterialTypes();
        strinBuilder.append(and).append(buildQueryForCriteriaField(RecapConstants.LEADER_MATERIAL_TYPE, materialTypes));
        return strinBuilder.toString();
    }


    private String buildQueryForCriteriaField(String fieldName, List<String> values) {
        List<String> modifiedValues = new ArrayList<>();
        if (!CollectionUtils.isEmpty(values)) {
            for (String value : values) {
                modifiedValues.add("\"" + value + "\"");
            }
        }
        return fieldName + ":" + "(" + StringUtils.join(modifiedValues, " ") + ")";
    }

    public SolrQuery getSolrQueryForCriteria(SearchRecordsRequest searchRecordsRequest) {
        if(StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isBlank(searchRecordsRequest.getFieldValue())){
            return getQuryForAllFieldsNoValue(searchRecordsRequest);
        } else if (StringUtils.isBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())){
            return getQuryForAllFieldsSpecificValue(searchRecordsRequest);
        } else if (StringUtils.isNotBlank(searchRecordsRequest.getFieldName()) && StringUtils.isNotBlank(searchRecordsRequest.getFieldValue())){
            return getQuryForSpecificFieldSpecificValue(searchRecordsRequest);
        }
        return null;
    }
}
