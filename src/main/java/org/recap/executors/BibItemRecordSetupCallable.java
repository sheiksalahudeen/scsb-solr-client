package org.recap.executors;

import org.apache.solr.common.SolrInputDocument;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.util.BibJSONUtil;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.concurrent.Callable;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public class BibItemRecordSetupCallable implements Callable {

    BibliographicEntity bibliographicEntity;
    private final SolrTemplate solrTemplate;

    public BibItemRecordSetupCallable(BibliographicEntity bibliographicEntity, SolrTemplate solrTemplate) {
        this.bibliographicEntity = bibliographicEntity;
        this.solrTemplate = solrTemplate;
    }

    @Override
    public Object call() throws Exception {
        SolrInputDocument solrInputDocument = new BibJSONUtil().generateBibAndItemsForIndex(bibliographicEntity, solrTemplate);
        return solrInputDocument ;
    }
}
