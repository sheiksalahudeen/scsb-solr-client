package org.recap.repository.solr.temp;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.recap.model.solr.Item;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.convert.MappingSolrConverter;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

import java.io.File;

/**
 * Created by angelind on 15/6/16.
 */
public class ItemCrudRepositoryMultiCoreSupport extends SimpleSolrRepository<Item, String> {

    /**
     * This method Instantiates a new item crud repository multi core support.
     *
     * @param coreName the core name
     * @param solrUrl  the solr url
     */
    public ItemCrudRepositoryMultiCoreSupport(String coreName, String solrUrl) {

        SolrTemplate solrTemplate = new SolrTemplate( new HttpSolrClient(solrUrl+ File.separator+coreName));
        solrTemplate.setSolrConverter(new MappingSolrConverter(new SimpleSolrMappingContext()) {
        });
        solrTemplate.setSolrCore(coreName);
        setSolrOperations(solrTemplate);
    }
}
