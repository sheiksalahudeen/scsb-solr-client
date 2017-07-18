package org.recap.repository.solr.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.solr.BibItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by premkb on 27/1/17.
 */
@Ignore
public class DataDumpSolrDocumentRepositoryImplAT extends BaseTestCase{

    @Autowired
    private DataDumpSolrDocumentRepositoryImpl dataDumpSolrDocumentRepository;

    @Test
    public void searchByItem() throws Exception{
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.getOwningInstitutions().addAll(Arrays.asList("CUL", "PUL"));
        searchRecordsRequest.getMaterialTypes().addAll(Arrays.asList("Monograph", "Serial", "Other"));
        searchRecordsRequest.setDeleted(true);
        Map<Integer, BibItem> bibItemMap = new HashMap<>();
        dataDumpSolrDocumentRepository.searchByItem(searchRecordsRequest,true,bibItemMap);
        assertTrue(!bibItemMap.isEmpty());
    }
}
