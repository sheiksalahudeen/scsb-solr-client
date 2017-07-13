package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.springframework.stereotype.Component;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peris on 9/30/16.
 */
@Component
public class SolrQueryBuilder {

    private String and = " AND ";

    private String or = " OR ";

    private String coreParentFilterQuery = "{!parent which=\"ContentType:parent\"}";

    private String coreChildFilterQuery = "{!child of=\"ContentType:parent\"}";

    /**
     * Gets query string for item criteria for parent.
     *
     * @param searchRecordsRequest the search records request
     * @return the query string for item criteria for parent
     */
    public String getQueryStringForItemCriteriaForParent(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> availability = searchRecordsRequest.getAvailability();
        List<String> collectionGroupDesignations = searchRecordsRequest.getCollectionGroupDesignations();
        List<String> useRestrictions = searchRecordsRequest.getUseRestrictions();

        if (CollectionUtils.isNotEmpty(availability)) {
            stringBuilder.append(buildQueryForFilterGivenChild(RecapConstants.AVAILABILITY, availability));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(and).append(buildQueryForFilterGivenChild(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        } else if (CollectionUtils.isNotEmpty(collectionGroupDesignations)) {
            stringBuilder.append(buildQueryForFilterGivenChild(RecapConstants.COLLECTION_GROUP_DESIGNATION, collectionGroupDesignations));
        }
        if (StringUtils.isNotBlank(stringBuilder.toString()) && CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(and).append(buildQueryForFilterGivenChild(RecapConstants.USE_RESTRICTION, useRestrictions));
        } else if (CollectionUtils.isNotEmpty(useRestrictions)) {
            stringBuilder.append(buildQueryForFilterGivenChild(RecapConstants.USE_RESTRICTION, useRestrictions));
        }
        stringBuilder
                .append(and)
                .append(RecapConstants.IS_DELETED_ITEM).append(":").append(searchRecordsRequest.isDeleted())
                .append(and)
                .append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(searchRecordsRequest.getCatalogingStatus());

        return coreParentFilterQuery + "(" + stringBuilder.toString() + ")";
    }

    private String buildQueryForFilterGivenChild(String fieldName, List<String> values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<String> iterator = values.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            stringBuilder.append(fieldName).append(":").append(value);
            if (iterator.hasNext()) {
                stringBuilder.append(or);
            }
        }
        return "(" + stringBuilder.toString() + ")";
    }

    /**
     * Gets query string for parent criteria for child.
     *
     * @param searchRecordsRequest the search records request
     * @return the query string for parent criteria for child
     */
    public String getQueryStringForParentCriteriaForChild(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(buildQueryForBibFacetCriteria(searchRecordsRequest));
        stringBuilder
                .append(and).append(coreChildFilterQuery)
                .append(RecapConstants.IS_DELETED_BIB).append(":").append(searchRecordsRequest.isDeleted())
                .append(and).append(coreChildFilterQuery)
                .append(RecapConstants.BIB_CATALOGING_STATUS).append(":").append(searchRecordsRequest.getCatalogingStatus());
        return stringBuilder.toString();
    }

    private String buildQueryForBibFacetCriteria(SearchRecordsRequest searchRecordsRequest) {
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

    /**
     * Gets query string for match child return parent.
     *
     * @param searchRecordsRequest the search records request
     * @return the query string for match child return parent
     */
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

    /**
     * Gets query string for match parent return child.
     *
     * @param searchRecordsRequest the search records request
     * @return the query string for match parent return child
     */
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
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.USE_RESTRICTION, useRestrictions));
        }
        return stringBuilder.toString();
    }

    /**
     * Gets query string for match parent return child for deleted data dump cgd to private.
     *
     * @return the query string for match parent return child for deleted data dump cgd to private
     */
    public String getQueryStringForMatchParentReturnChildForDeletedDataDumpCGDToPrivate() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.COLLECTION_GROUP_DESIGNATION, Arrays.asList(RecapConstants.PRIVATE)));
        return stringBuilder.toString();
    }

    /**
     * This method is used to build query for parent using the given field name,list of values and child query.
     * @param fieldName
     * @param values
     * @param parentQuery
     * @return
     */
    private String buildQueryForParentGivenChild(String fieldName, List<String> values, String parentQuery) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Iterator<String> iterator = values.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            stringBuilder.append(parentQuery).append(fieldName).append(":").append(value);
            if (iterator.hasNext()) {
                stringBuilder.append(or);
            }
        }
        return "(" + stringBuilder.toString() + ")";
    }

    /**
     * This method is used to build query for the given fieldName and list of values.
     * @param fieldName
     * @param values
     * @return
     */
    private String buildQueryForMatchChildReturnParent(String fieldName, List<String> values) {
        List<String> modifiedValues = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(values)) {
            for (String value : values) {
                modifiedValues.add("\"" + value.trim() + "\"");
            }
        }
        return fieldName + ":" + "(" + StringUtils.join(modifiedValues, " ") + ")";
    }

    /**
     * IF the getQueryForFieldCriteria() is called with Item field/value combination, the query would still return
     * only Bib Criteria. You will need to call getItemSolrQueryForCriteria()
     *
     * @param searchRecordsRequest the search records request
     * @return the query for field criteria
     * @throws Exception
     */
    public String getQueryForFieldCriteria(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        String fieldValue = parseSearchRequest(searchRecordsRequest.getFieldValue().trim());
        String fieldName = searchRecordsRequest.getFieldName();

        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(fieldValue)) {
            //The following "if" condition is for exact match (i.e string data type fields in Solr)
            //Author, Title, Publisher, Publication Place, Publication date, Subjet & Notes.
            if(!(fieldName.equalsIgnoreCase(RecapConstants.BARCODE) || fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.CUSTOMER_CODE)
                    || fieldName.equalsIgnoreCase(RecapConstants.ISBN_CRITERIA) || fieldName.equalsIgnoreCase(RecapConstants.OCLC_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISSN_CRITERIA))) {

                if(fieldName.contains(RecapConstants.DATE) && !fieldName.equalsIgnoreCase(RecapConstants.PUBLICATION_DATE)){
                    stringBuilder.append(fieldName).append(":").append("[");
                    stringBuilder.append(fieldValue).append("]");
                    return stringBuilder.toString();
                }

                String[] fieldValues = fieldValue.split("\\s+");

                if(fieldName.equalsIgnoreCase(RecapConstants.TITLE_STARTS_WITH)) {
                    stringBuilder.append(fieldName).append(":").append("(");
                    stringBuilder.append("\"").append(fieldValues[0]).append("\"").append(")");
                } else {
                    if(fieldValues.length > 1) {
                        List<String> fieldValuesList = Arrays.asList(fieldValues);
                        for (Iterator<String> iterator = fieldValuesList.iterator(); iterator.hasNext(); ) {
                            String value = iterator.next();
                            stringBuilder.append(fieldName).append(":").append("(").append("\"");
                            stringBuilder.append(value).append("\"").append(")");
                            if (iterator.hasNext()) {
                                stringBuilder.append(and);
                            }
                        }
                    } else {
                        stringBuilder.append(fieldName).append(":").append("(");
                        stringBuilder.append("\"").append(fieldValue).append("\"").append(")");
                    }
                }
            } else {
                //Check for item fields.
                if(fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER)){
                    fieldValue = fieldValue.replaceAll(" ", "");
                }
                if (fieldName.equalsIgnoreCase(RecapConstants.BARCODE)) {
                    String[] fieldValues = fieldValue.split(",");
                    if (ArrayUtils.isNotEmpty(fieldValues)) {
                        stringBuilder.append(buildQueryForMatchChildReturnParent(fieldName, Arrays.asList(fieldValues)));
                    }
                }
                //Check for Bib fields.
                else {
                    stringBuilder.append(fieldName).append(":").append("(");
                    stringBuilder.append("\"").append(fieldValue).append("\"").append(")");
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * Gets count query for field criteria.
     *
     * @param searchRecordsRequest the search records request
     * @param parentQuery          the parent query
     * @return the count query for field criteria
     */
    public String getCountQueryForFieldCriteria(SearchRecordsRequest searchRecordsRequest, String parentQuery) {
        StringBuilder stringBuilder = new StringBuilder();
        String fieldValue = parseSearchRequest(searchRecordsRequest.getFieldValue().trim());
        String fieldName = searchRecordsRequest.getFieldName();
        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(fieldValue)) {
            if(!(fieldName.equalsIgnoreCase(RecapConstants.BARCODE) || fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.CUSTOMER_CODE)
                    || fieldName.equalsIgnoreCase(RecapConstants.ISBN_CRITERIA) || fieldName.equalsIgnoreCase(RecapConstants.OCLC_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISSN_CRITERIA))) {
                String[] fieldValues = fieldValue.split("\\s+");
                if(fieldName.equalsIgnoreCase(RecapConstants.TITLE_STARTS_WITH)) {
                    stringBuilder.append(parentQuery).append(fieldName).append(":").append(fieldValues[0]);
                } else {
                    if(fieldValues.length > 1) {
                        List<String> fieldValuesList = Arrays.asList(fieldValues);
                        stringBuilder.append(parentQuery);
                        for (Iterator<String> iterator = fieldValuesList.iterator(); iterator.hasNext(); ) {
                            String value = iterator.next();
                            stringBuilder.append(fieldName).append(":").append(value);
                            if (iterator.hasNext()) {
                                stringBuilder.append(and);
                            }
                        }
                    } else {
                        stringBuilder.append(parentQuery).append(fieldName).append(":").append(fieldValue);
                    }
                }
            } else {
                if(fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER)) {
                    fieldValue = fieldValue.replaceAll(" ", "");
                }
                stringBuilder.append(parentQuery).append(fieldName).append(":").append(fieldValue);
            }
            return stringBuilder.toString();
        }
        return "";
    }

    /**
     * Gets solr query for bib item.
     *
     * @param parentQueryString the parent query string
     * @return the solr query for bib item
     */
    public SolrQuery getSolrQueryForBibItem(String parentQueryString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parentQueryString);
        return new SolrQuery(stringBuilder.toString());
    }

    /**
     * Gets query for parent and child criteria.
     *
     * @param searchRecordsRequest the search records request
     * @return the query for parent and child criteria
     */
    public SolrQuery getQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);
        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria
                + and + RecapConstants.IS_DELETED_BIB + ":" + searchRecordsRequest.isDeleted()
                + and + RecapConstants.BIB_CATALOGING_STATUS + ":" + searchRecordsRequest.getCatalogingStatus()
                + (StringUtils.isNotBlank(queryForFieldCriteria) ? and + queryForFieldCriteria : ""));
        solrQuery.setFilterQueries(queryStringForItemCriteriaForParent);
        return solrQuery;
    }

    /**
     * Gets query for parent and child criteria for data dump.
     *
     * @param searchRecordsRequest the search records request
     * @return the query for parent and child criteria for data dump
     */
    public SolrQuery getQueryForParentAndChildCriteriaForDataDump(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);
        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria
                + and + RecapConstants.IS_DELETED_BIB + ":" + searchRecordsRequest.isDeleted()
                + and + RecapConstants.BIB_CATALOGING_STATUS + ":" + RecapConstants.COMPLETE_STATUS
                + (StringUtils.isNotBlank(queryForFieldCriteria) ? and + queryForFieldCriteria : ""));
        solrQuery.setFilterQueries(queryStringForItemCriteriaForParent);
        return solrQuery;
    }

    /**
     * Gets query for child and parent criteria.
     *
     * @param searchRecordsRequest the search records request
     * @return the query for child and parent criteria
     */
    public SolrQuery getQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);
        return new SolrQuery(queryStringForItemCriteria
                + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted()
                + and + RecapConstants.ITEM_CATALOGING_STATUS + ":" + searchRecordsRequest.getCatalogingStatus()
                + and + queryForFieldCriteria + queryStringForParentCriteriaForChild);
    }

    /**
     * Gets deleted query for data dump.
     *
     * @param searchRecordsRequest  the search records request
     * @param isCGDChangedToPrivate the is cgd changed to private
     * @return the deleted query for data dump
     */
    public SolrQuery getDeletedQueryForDataDump(SearchRecordsRequest searchRecordsRequest,boolean isCGDChangedToPrivate) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryForBibCriteria = buildQueryForBibFacetCriteria(searchRecordsRequest);
        String queryStringForItemCriteria;
        SolrQuery solrQuery;
        if (isCGDChangedToPrivate) {
            queryStringForItemCriteria = getQueryStringForMatchParentReturnChildForDeletedDataDumpCGDToPrivate();
            solrQuery = new SolrQuery(queryStringForItemCriteria + and +"("+ ("("+RecapConstants.IS_DELETED_ITEM + ":" + false + and +RecapConstants.CGD_CHANAGE_LOG + ":" + "\"" +RecapConstants.CGD_CHANAGE_LOG_SHARED_TO_PRIVATE + "\"" +")")
                    + or + ("("+RecapConstants.IS_DELETED_ITEM + ":" + false + and +RecapConstants.CGD_CHANAGE_LOG + ":" + "\"" +RecapConstants.CGD_CHANAGE_LOG_OPEN_TO_PRIVATE +"\"" +")") +")"
                    + (StringUtils.isNotBlank(queryForFieldCriteria) ? and + queryForFieldCriteria : "")
                    + (StringUtils.isNotBlank(queryForBibCriteria) ? and + queryForBibCriteria : ""));//to include items that got changed from shared to private, open to private for deleted export
        } else{
            queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
            solrQuery = new SolrQuery(queryStringForItemCriteria + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted()
                    + (StringUtils.isNotBlank(queryForFieldCriteria) ? and + queryForFieldCriteria : "")
                    + (StringUtils.isNotBlank(queryForBibCriteria) ? and + queryForBibCriteria : ""));
        }
        return solrQuery;
    }

    /**
     * Gets count query for parent and child criteria.
     *
     * @param searchRecordsRequest the search records request
     * @return the count query for parent and child criteria
     */
    public SolrQuery getCountQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {
        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreParentFilterQuery);
        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);
        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria
                + and + RecapConstants.IS_DELETED_BIB + ":" + searchRecordsRequest.isDeleted()
                + and + RecapConstants.BIB_CATALOGING_STATUS + ":" + searchRecordsRequest.getCatalogingStatus());
        solrQuery.setFilterQueries(queryStringForItemCriteriaForParent, countQueryForFieldCriteria);
        return solrQuery;
    }

    /**
     * Gets count query for child and parent criteria.
     *
     * @param searchRecordsRequest the search records request
     * @return the count query for child and parent criteria
     */
    public SolrQuery getCountQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {
        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreChildFilterQuery);
        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria
                + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted()
                + and + RecapConstants.ITEM_CATALOGING_STATUS + ":" + searchRecordsRequest.getCatalogingStatus()
                + and + queryStringForParentCriteriaForChild);
        solrQuery.setFilterQueries(countQueryForFieldCriteria);
        return solrQuery;
    }

    /**
     * This method escapes the special characters.
     *
     * @param searchText the search text
     * @return string
     */
    public String parseSearchRequest(String searchText) {
        StringBuilder modifiedText = new StringBuilder();
        StringCharacterIterator stringCharacterIterator = new StringCharacterIterator(searchText);
        char character = stringCharacterIterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '\\') {
                modifiedText.append("\\\\");
            } else if (character == '?') {
                modifiedText.append("\\?");
            } else if (character == '*') {
                modifiedText.append("\\*");
            } else if (character == '+') {
                modifiedText.append("\\+");
            } else if (character == ':') {
                modifiedText.append("\\:");
            } else if (character == '{') {
                modifiedText.append("\\{");
            } else if (character == '}') {
                modifiedText.append("\\}");
            } else if (character == '[') {
                modifiedText.append("\\[");
            } else if (character == ']') {
                modifiedText.append("\\]");
            } else if (character == '(') {
                modifiedText.append("\\(");
            } else if (character == ')') {
                modifiedText.append("\\)");
            } else if (character == '^') {
                modifiedText.append("\\^");
            } else if (character == '~') {
                modifiedText.append("\\~");
            } else if (character == '-') {
                modifiedText.append("\\-");
            } else if (character == '!') {
                modifiedText.append("\\!");
            } else if (character == '\'') {
                modifiedText.append("\\'");
            } else if (character == '@') {
                modifiedText.append("\\@");
            } else if (character == '#') {
                modifiedText.append("\\#");
            } else if (character == '$') {
                modifiedText.append("\\$");
            } else if (character == '%') {
                modifiedText.append("\\%");
            } else if (character == '/') {
                modifiedText.append("\\/");
            } else if (character == '"') {
                modifiedText.append("\\\"");
            } else if (character == '.') {
                modifiedText.append("\\.");
            }
            else {
                modifiedText.append(character);
            }
            character = stringCharacterIterator.next();
        }
        return modifiedText.toString();
    }

    /**
     * Solr query to fetch bib details.
     *
     * @param matchingMatchPointsEntities the matching match points entities
     * @param matchCriteriaValues         the match criteria values
     * @param matchingCriteria            the matching criteria
     * @return the solr query
     */
    public SolrQuery solrQueryToFetchBibDetails(List<MatchingMatchPointsEntity> matchingMatchPointsEntities, List<String> matchCriteriaValues, String matchingCriteria) {
        Integer rows = 0;
        for (MatchingMatchPointsEntity matchingMatchPointsEntity : matchingMatchPointsEntities) {
            String criteriaValue = matchingMatchPointsEntity.getCriteriaValue();
            if(criteriaValue.contains("\\")) {
                criteriaValue = criteriaValue.replaceAll("\\\\", "\\\\\\\\");
            }
            matchCriteriaValues.add(criteriaValue);
            rows = rows + matchingMatchPointsEntity.getCriteriaValueCount();
        }
        StringBuilder query = new StringBuilder();
        if (CollectionUtils.isNotEmpty(matchCriteriaValues)) {
            query.append(buildQueryForMatchChildReturnParent(matchingCriteria, matchCriteriaValues));
        }
        query.append(and).append(RecapConstants.IS_DELETED_BIB).append(":").append(RecapConstants.FALSE)
                .append(and).append(RecapConstants.BIB_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(RecapConstants.SHARED_CGD)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.IS_DELETED_ITEM).append(":").append(RecapConstants.FALSE)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        SolrQuery solrQuery = new SolrQuery(query.toString());
        solrQuery.setRows(rows);
        return solrQuery;
    }

    /**
     * Solr query for ongoing matching.
     *
     * @param fieldName           the field name
     * @param matchingPointValues the matching point values
     * @return the string
     */
    public String solrQueryForOngoingMatching(String fieldName, List<String> matchingPointValues) {
        StringBuilder query = new StringBuilder();
        query.append(buildQueryForMatchChildReturnParent(fieldName, matchingPointValues));
        query.append(and).append(RecapConstants.IS_DELETED_BIB).append(":").append(RecapConstants.FALSE)
                .append(and).append(RecapConstants.BIB_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(RecapConstants.SHARED_CGD)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.IS_DELETED_ITEM).append(":").append(RecapConstants.FALSE)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        return query.toString();
    }

    /**
     * Solr query for ongoing matching.
     *
     * @param fieldName          the field name
     * @param matchingPointValue the matching point value
     * @return the string
     */
    public String solrQueryForOngoingMatching(String fieldName, String matchingPointValue) {
        StringBuilder query = new StringBuilder();
        if(matchingPointValue.contains("\\")) {
            matchingPointValue = matchingPointValue.replaceAll("\\\\", "\\\\\\\\");
        }
        query.append(fieldName).append(":").append("\"").append(matchingPointValue).append("\"");
        query.append(and).append(RecapConstants.IS_DELETED_BIB).append(":").append(RecapConstants.FALSE)
                .append(and).append(RecapConstants.BIB_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(RecapConstants.SHARED_CGD)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.IS_DELETED_ITEM).append(":").append(RecapConstants.FALSE)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        return query.toString();
    }

    /**
     * This query is used to Fetch created or updated bibs based on the date.
     *
     * @param date the date
     * @return the string
     */
    public String fetchCreatedOrUpdatedBibs(String date) {
        StringBuilder query = new StringBuilder();
        query.append("(").append(RecapConstants.BIB_CREATED_DATE).append(":").append("[").append(date).append("]")
                .append(or).append(RecapConstants.BIB_LAST_UPDATED_DATE).append(":").append("[").append(date).append("]").append(")")
                .append(and).append(RecapConstants.IS_DELETED_BIB).append(":").append(RecapConstants.FALSE)
                .append(and).append(RecapConstants.BIB_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        query.append(and).append(coreParentFilterQuery).append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(RecapConstants.SHARED_CGD)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.IS_DELETED_ITEM).append(":").append(RecapConstants.FALSE)
                .append(and).append(coreParentFilterQuery).append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        return query.toString();
    }

    /**
     * Build solr query for accession reports.
     *
     * @param date                       the date
     * @param owningInstitution          the owning institution
     * @param isDeleted                  the is deleted
     * @param collectionGroupDesignation the collection group designation
     * @return the solr query
     */
    public SolrQuery buildSolrQueryForAccessionReports(String date, String owningInstitution, boolean isDeleted, String collectionGroupDesignation) {
        StringBuilder query = new StringBuilder();
        query.append(RecapConstants.DOCTYPE).append(":").append(RecapConstants.ITEM).append(and);
        query.append(RecapConstants.ITEM_CREATED_DATE).append(":").append("[").append(date).append("]").append(and);
        query.append(RecapConstants.IS_DELETED_ITEM).append(":").append(isDeleted).append(and);
        query.append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS).append(and);
        query.append(RecapConstants.ITEM_OWNING_INSTITUTION).append(":").append(owningInstitution).append(and);
        query.append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(collectionGroupDesignation);
        return new SolrQuery(query.toString());
    }

    /**
     * Build solr query for deaccession reports.
     *
     * @param date                       the date
     * @param owningInstitution          the owning institution
     * @param isDeleted                  the is deleted
     * @param collectionGroupDesignation the collection group designation
     * @return the solr query
     */
    public SolrQuery buildSolrQueryForDeaccessionReports(String date, String owningInstitution, boolean isDeleted, String collectionGroupDesignation) {
        StringBuilder query = new StringBuilder();
        query.append(RecapConstants.DOCTYPE).append(":").append(RecapConstants.ITEM).append(and);
        query.append(RecapConstants.ITEM_LASTUPDATED_DATE).append(":").append("[").append(date).append("]").append(and);
        query.append(RecapConstants.IS_DELETED_ITEM).append(":").append(isDeleted).append(and);
        query.append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS).append(and);
        query.append(RecapConstants.ITEM_OWNING_INSTITUTION).append(":").append(owningInstitution).append(and);
        query.append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(collectionGroupDesignation);
        return new SolrQuery(query.toString());
    }


    /**
     * Build solr query for cgd reports.
     *
     * @param owningInstitution          the owning institution
     * @param collectionGroupDesignation the collection group designation
     * @return the solr query
     */
    public SolrQuery buildSolrQueryForCGDReports(String owningInstitution , String collectionGroupDesignation){
        StringBuilder query = new StringBuilder();
        query.append(RecapConstants.DOCTYPE).append(":").append(RecapConstants.ITEM).append(and);
        query.append(RecapConstants.ITEM_OWNING_INSTITUTION).append(":").append(owningInstitution).append(and);
        query.append(RecapConstants.COLLECTION_GROUP_DESIGNATION).append(":").append(collectionGroupDesignation).append(and);
        query.append(RecapConstants.IS_DELETED_ITEM).append(":").append(RecapConstants.FALSE).append(and);
        query.append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS);
        return new SolrQuery(query.toString());
    }


    /**
     * Build solr query for deaccesion report information.
     *
     * @param date              the date
     * @param owningInstitution the owning institution
     * @param isDeleted         the is deleted
     * @return the solr query
     */
    public SolrQuery buildSolrQueryForDeaccesionReportInformation(String date, String owningInstitution, boolean isDeleted) {
        StringBuilder query = new StringBuilder();
        query.append(RecapConstants.DOCTYPE).append(":").append(RecapConstants.ITEM).append(and);
        query.append(RecapConstants.ITEM_LASTUPDATED_DATE).append(":").append("[").append(date).append("]").append(and);
        query.append(RecapConstants.IS_DELETED_ITEM).append(":").append(isDeleted).append(and);
        query.append(RecapConstants.ITEM_CATALOGING_STATUS).append(":").append(RecapConstants.COMPLETE_STATUS).append(and);
        query.append(RecapConstants.ITEM_OWNING_INSTITUTION).append(":").append(owningInstitution);
        return new SolrQuery(query.toString());
    }


    public SolrQuery buildSolrQueryForIncompleteReports(String owningInstitution){
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.DOC_TYPE_ITEM);
        solrQuery.addFilterQuery(RecapConstants.ITEM_STATUS_INCOMPLETE);
        solrQuery.addFilterQuery(RecapConstants.ITEM_OWNING_INSTITUTION+":"+owningInstitution);
        solrQuery.addFilterQuery(RecapConstants.IS_DELETED_ITEM_FALSE);
        solrQuery.setFields(RecapConstants.ITEM_ID,RecapConstants.BARCODE,RecapConstants.CUSTOMER_CODE,RecapConstants.ITEM_CREATED_DATE,RecapConstants.ITEM_CATALOGING_STATUS,RecapConstants.ITEM_BIB_ID,RecapConstants.ITEM_OWNING_INSTITUTION);
        return solrQuery;
    }

    public SolrQuery buildSolrQueryToGetBibDetails(List<Integer> bibIdList,int rows){
        String bibIds = StringUtils.join(bibIdList, ",");
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(RecapConstants.BIB_DOC_TYPE);
        solrQuery.setRows(rows);
        solrQuery.addFilterQuery(RecapConstants.SOLR_BIB_ID+StringEscapeUtils.escapeJava(bibIds).replace(",","\" \""));
        solrQuery.setFields(RecapConstants.BIB_ID,RecapConstants.TITLE_DISPLAY,RecapConstants.AUTHOR_SEARCH,RecapConstants.AUTHOR_DISPLAY);
        return solrQuery;
    }
}
