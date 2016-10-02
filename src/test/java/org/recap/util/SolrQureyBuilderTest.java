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
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getSolrQueryForCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    @Test
    public void allFieldsSpecificValueQuery() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("");
        searchRecordsRequest.setFieldValue("Scotland");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getSolrQueryForCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    @Test
    public void SpecificFieldsSpecificValueQuery() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Title_search");
        searchRecordsRequest.setFieldValue("Scotland");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getSolrQueryForCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    /**
     * IF the getSolrQueryForCriteria() is called with Item field/value combinatin, the query would still return
     * only Bib Criteria. You will need to call getItemSolrQueryForCriteria()
     * @throws Exception
     */
    @Test
    public void ItemFieldSpecificValueQuery() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Barcode");
        searchRecordsRequest.setFieldValue("1231");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getSolrQueryForCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    @Test
    public void itemQuery() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("Barcode");
        searchRecordsRequest.setFieldValue("123125123");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("NYPL", "CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQureyBuilder solrQureyBuilder = new SolrQureyBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQureyBuilder.getItemSolrQueryForCriteria("_root_:12",searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }


}