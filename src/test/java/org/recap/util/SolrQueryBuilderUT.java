package org.recap.util;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.recap.model.search.SearchRecordsRequest;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by peris on 9/30/16.
 */
public class SolrQueryBuilderUT {
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

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForParentAndChildCriteria(searchRecordsRequest);
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

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForParentAndChildCriteria(searchRecordsRequest);
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

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForParentAndChildCriteria(searchRecordsRequest);
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

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForChildAndParentCriteria(searchRecordsRequest);
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

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForChildAndParentCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    @Test
    public void dataDumpQueryForIncremental() throws Exception {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("BibLastUpdatedDate");
        searchRecordsRequest.setFieldValue("2016-10-21T14:30Z TO NOW");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("CUL", "PUL"));
        searchRecordsRequest.getCollectionGroupDesignations().addAll(Arrays.asList("Shared", "Private", "Open"));
        searchRecordsRequest.getAvailability().addAll(Arrays.asList("Available", "Not Available"));
        searchRecordsRequest.getUseRestrictions().addAll(Arrays.asList("No Restrictions", "In Library Use", "Supervised Use"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery quryForAllFieldsNoValue = solrQueryBuilder.getQueryForChildAndParentCriteria(searchRecordsRequest);
        System.out.println(quryForAllFieldsNoValue);
    }

    @Test
    public void getDeletedQueryForDataDump(){
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldName("BibLastUpdatedDate");
        searchRecordsRequest.setFieldValue("2016-10-21T14:30Z TO NOW");
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("CUL", "PUL"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));

        SolrQueryBuilder solrQueryBuilder = new SolrQueryBuilder();
        SolrQuery queryForAllFieldsNoValue = solrQueryBuilder.getDeletedQueryForDataDump(searchRecordsRequest,true);
        System.out.println(queryForAllFieldsNoValue);
        assertNotNull(queryForAllFieldsNoValue);
    }


}