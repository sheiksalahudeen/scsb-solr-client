package org.recap.util;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.recap.model.search.SearchRecordsRequest;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by peris on 9/30/16.
 */
public class SolrQureyBuilderTest {
    @Test
    public void allFieldsNoValueQuery() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("");
        searchRecordsRequest.setFieldValue("");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getQuryForAllFieldsNoValue(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue.getQuery());
    }

    @Test
    public void allFieldsSpecificValueForItemCriteria() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("");
        searchRecordsRequest.setFieldValue("Scotland");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getQuryForAllFieldsSpecificValue(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue.getQuery());
    }


    @Test
    public void SpecificItemFieldSpecificValueForItemCriteria() throws Exception {

    }

    @Test
    public void SpecificBibFieldSpecificValueForItemCriteria() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Title_search");
        searchRecordsRequest.setFieldValue("Scotland");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getQuryForSpecificFieldSpecificValue(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue.getQuery());
    }

}