package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.recap.RecapConstants;
import org.recap.model.jpa.MatchingMatchPointsEntity;
import org.recap.model.search.SearchRecordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by peris on 9/30/16.
 */

@Component
public class SolrQueryBuilder {

    private Logger logger = LoggerFactory.getLogger(SolrQueryBuilder.class);

    String and = " AND ";

    String coreParentFilterQuery = "{!parent which=\"ContentType:parent\"}";

    String coreChildFilterQuery = "{!child of=\"ContentType:parent\"}";

    private Pattern regexPattern = Pattern.compile("([&\\|\\!\\(\\}\\[\\]\\<\\>\\~\\*\\+\\?\\:])");

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
        stringBuilder.append(and).append(coreParentFilterQuery).append(RecapConstants.IS_DELETED_ITEM).append(":").append(searchRecordsRequest.isDeleted());

        return stringBuilder.toString();
    }

    public String getQueryStringForParentCriteriaForChild(SearchRecordsRequest searchRecordsRequest) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(buildQueryForBibFacetCriteria(searchRecordsRequest));
        stringBuilder.append(and).append(coreChildFilterQuery).append(RecapConstants.IS_DELETED_BIB).append(":").append(searchRecordsRequest.isDeleted());
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
            stringBuilder.append(buildQueryForMatchChildReturnParent(RecapConstants.USE_RESTRICTION, useRestrictions));
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
                modifiedValues.add("\"" + value.trim() + "\"");
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
        StringBuilder stringBuilder = new StringBuilder();
        String fieldValue = parseSearchRequest(searchRecordsRequest.getFieldValue().trim());
        String fieldName = searchRecordsRequest.getFieldName();

        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(fieldValue)) {
            //The following "if" condition is for exact match (i.e string data type fields in Solr)
            //Author, Title, Publisher, Publication Place, Publication date, Subjet & Notes.
            if(!(fieldName.equalsIgnoreCase(RecapConstants.BARCODE) || fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISBN_CRITERIA)
                    || fieldName.equalsIgnoreCase(RecapConstants.OCLC_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISSN_CRITERIA))) {

                if(fieldName.contains("Date") && !fieldName.equalsIgnoreCase(RecapConstants.PUBLICATION_DATE)){
                    stringBuilder.append(fieldName).append(":").append("[");
                    stringBuilder.append(fieldValue).append("]").append(and);
                    return stringBuilder.toString();
                }

                String[] fieldValues = fieldValue.split("\\s+");

                if(fieldName.equalsIgnoreCase(RecapConstants.TITLE_STARTS_WITH)) {
                    stringBuilder.append(fieldName).append(":").append("(");
                    stringBuilder.append("\"").append(fieldValues[0]).append("\"").append(")").append(and);
                } else {
                    if(fieldValues.length > 1) {
                        for(String value : fieldValues) {
                            stringBuilder.append(fieldName).append(":").append("(").append("\"");
                            stringBuilder.append(value).append("\"").append(")").append(and);
                        }
                    } else {
                        stringBuilder.append(fieldName).append(":").append("(");
                        stringBuilder.append("\"").append(fieldValue).append("\"").append(")").append(and);
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
                        stringBuilder.append(buildQueryForMatchChildReturnParent(fieldName, Arrays.asList(fieldValues))).append(and);
                    }
                }
                //Check for Bib fields.
                else {
                    stringBuilder.append(fieldName).append(":").append("(");
                    stringBuilder.append("\"").append(fieldValue).append("\"").append(")").append(and);
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public String getCountQueryForFieldCriteria(SearchRecordsRequest searchRecordsRequest, String parentQuery) {
        StringBuilder stringBuilder = new StringBuilder();
        String fieldValue = parseSearchRequest(searchRecordsRequest.getFieldValue().trim());
        String fieldName = searchRecordsRequest.getFieldName();
        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(fieldValue)) {
            if(!(fieldName.equalsIgnoreCase(RecapConstants.BARCODE) || fieldName.equalsIgnoreCase(RecapConstants.CALL_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISBN_CRITERIA)
                    || fieldName.equalsIgnoreCase(RecapConstants.OCLC_NUMBER) || fieldName.equalsIgnoreCase(RecapConstants.ISSN_CRITERIA))) {
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

    public SolrQuery getSolrQueryForBibItem(String parentQueryString) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parentQueryString);
        return new SolrQuery(stringBuilder.toString());
    }

    public SolrQuery getQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);
        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria + and + RecapConstants.IS_DELETED_BIB + ":" + searchRecordsRequest.isDeleted() + and + queryForFieldCriteria + queryStringForItemCriteriaForParent);
        return solrQuery;
    }

    public SolrQuery getQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted() + and + queryForFieldCriteria + queryStringForParentCriteriaForChild);
        return solrQuery;
    }

    public SolrQuery getDeletedQueryForDataDump(SearchRecordsRequest searchRecordsRequest) {
        String queryForFieldCriteria = getQueryForFieldCriteria(searchRecordsRequest);
        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String queryForBibCriteria = buildQueryForBibFacetCriteria(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted() + and + queryForFieldCriteria + queryForBibCriteria);
        return solrQuery;
    }

    public SolrQuery getCountQueryForParentAndChildCriteria(SearchRecordsRequest searchRecordsRequest) {
        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreParentFilterQuery);
        String queryStringForBibCriteria = getQueryStringForMatchChildReturnParent(searchRecordsRequest);
        String queryStringForItemCriteriaForParent = getQueryStringForItemCriteriaForParent(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForBibCriteria + and + RecapConstants.IS_DELETED_BIB + ":" + searchRecordsRequest.isDeleted() + and + queryStringForItemCriteriaForParent);
        solrQuery.setFilterQueries(countQueryForFieldCriteria);
        return solrQuery;
    }

    public SolrQuery getCountQueryForChildAndParentCriteria(SearchRecordsRequest searchRecordsRequest) {
        String countQueryForFieldCriteria = getCountQueryForFieldCriteria(searchRecordsRequest, coreChildFilterQuery);
        String queryStringForItemCriteria = getQueryStringForMatchParentReturnChild(searchRecordsRequest);
        String queryStringForParentCriteriaForChild = getQueryStringForParentCriteriaForChild(searchRecordsRequest);
        SolrQuery solrQuery = new SolrQuery(queryStringForItemCriteria + and + RecapConstants.IS_DELETED_ITEM + ":" + searchRecordsRequest.isDeleted() + and + queryStringForParentCriteriaForChild);
        solrQuery.setFilterQueries(countQueryForFieldCriteria);
        return solrQuery;
    }

    /**
     * This method escapes the special characters.
     * @param searchText
     * @return
     */
    public String parseSearchRequest(String searchText) {
        StringBuffer modifiedText = new StringBuffer();
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
        SolrQuery solrQuery = new SolrQuery(query.toString());
        solrQuery.setRows(rows);
        return solrQuery;
    }
}
