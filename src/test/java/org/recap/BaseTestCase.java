package org.recap;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.recap.admin.SolrAdmin;
import org.recap.repository.jpa.*;
import org.recap.repository.solr.main.BibSolrCrudRepository;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@WebAppConfiguration
@Transactional
@Rollback()
public class BaseTestCase {

    @Autowired
    public SolrAdmin solrAdmin;

    @Autowired
    public SolrClient solrAdminClient;

    @Autowired
    public BibSolrCrudRepository bibCrudRepository;

    @Autowired
    public BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    public ItemDetailsRepository itemDetailsRepository;

    @Autowired
    public HoldingDetailRepository holdingDetailRepository;

    @Autowired
    public BibliographicHoldingDetailsRepository bibliographicHoldingDetailsRepository;

    @Autowired
    public ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    public InstitutionDetailRepository institutionDetailRepository;

    @Autowired
    public CollectionGroupDetailRepository collectionGroupDetailRepository;

    @Autowired
    public ItemCrudRepository itemCrudRepository;

    @Value("${bib.rest.url}")
    public String bibResourceURL;

    @Value(("${item.rest.url}"))
    public String itemResourceURL;

    @Test
    public void loadContexts() {
        System.out.println();
    }
}
