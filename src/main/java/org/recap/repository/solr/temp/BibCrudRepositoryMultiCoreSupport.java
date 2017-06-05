package org.recap.repository.solr.temp;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.recap.model.solr.Bib;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.convert.MappingSolrConverter;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

import java.io.File;

/**
 * Created by pvsubrah on 6/14/16.
 */
public class BibCrudRepositoryMultiCoreSupport extends SimpleSolrRepository<Bib, String> {

    /**
     * This method instantiates a new bib crud repository to perform crud operations on temp cores.
     *
     * @param coreName the core name
     * @param solrUrl  the solr url
     */
    public BibCrudRepositoryMultiCoreSupport(String coreName, String solrUrl) {

        SolrTemplate solrTemplate = new SolrTemplate( new HttpSolrClient(solrUrl+ File.separator+coreName));
        solrTemplate.setSolrConverter(new MappingSolrConverter(new SimpleSolrMappingContext()) {
        });
        solrTemplate.setSolrCore(coreName);
        setSolrOperations(solrTemplate);
    }
}
