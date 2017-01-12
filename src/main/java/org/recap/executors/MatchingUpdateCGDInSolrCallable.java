package org.recap.executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.springframework.data.solr.core.SolrTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by angelind on 12/1/17.
 */
public class MatchingUpdateCGDInSolrCallable implements Callable {

    private List<ItemEntity> itemEntities;
    private BibJSONUtil bibJSONUtil;
    private SolrTemplate solrTemplate;
    private BibliographicDetailsRepository bibliographicDetailsRepository;
    private HoldingsDetailsRepository holdingsDetailsRepository;

    public MatchingUpdateCGDInSolrCallable(List<ItemEntity> itemEntities, BibJSONUtil bibJSONUtil, SolrTemplate solrTemplate,
                                           BibliographicDetailsRepository bibliographicDetailsRepository, HoldingsDetailsRepository holdingsDetailsRepository) {
        this.itemEntities = itemEntities;
        this.bibJSONUtil = bibJSONUtil;
        this.solrTemplate = solrTemplate;
        this.bibliographicDetailsRepository = bibliographicDetailsRepository;
        this.holdingsDetailsRepository = holdingsDetailsRepository;
    }

    @Override
    public Object call() throws Exception {
        List<SolrInputDocument> bibSolrInputDocuments = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(itemEntities)) {
            for(ItemEntity itemEntity : itemEntities) {
                if (itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                    for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate,
                                bibliographicDetailsRepository, holdingsDetailsRepository);
                        bibSolrInputDocuments.add(bibSolrInputDocument);
                    }
                }
            }
            solrTemplate.saveDocuments(bibSolrInputDocuments);
        }
        return null;
    }
}
