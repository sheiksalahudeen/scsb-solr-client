package org.recap.service.deAccession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.HoldingsDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.BibJSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by angelind on 10/11/16.
 */

@Component
public class DeAccessSolrDocumentService {
    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingDetailRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    SolrTemplate solrTemplate;

    public String updateIsDeletedBibByBibId(@RequestBody List<Integer> bibIds){
        try{
            for(Integer bibId : bibIds){
                BibJSONUtil bibJSONUtil = new BibJSONUtil();
                BibliographicEntity bibEntity = bibliographicDetailsRepository.findByBibliographicId(bibId);
                SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
                bibSolrInputDocument.setField(RecapConstants.IS_DELETED_BIB,true);
                solrTemplate.saveDocument(bibSolrInputDocument);
                solrTemplate.commit();
            }
            return "Bib documents updated successfully.";
        }catch(Exception ex){
            return "Bib documents failed to update.";
        }
    }

    public String updateIsDeletedHoldingsByHoldingsId(@RequestBody  List<Integer> holdingsIds){
        try{
            for(Integer holdingsId : holdingsIds){
                BibJSONUtil bibJSONUtil = new BibJSONUtil();
                HoldingsEntity holdingsEntity = holdingDetailRepository.findByHoldingsId(holdingsId);
                if(holdingsEntity != null && CollectionUtils.isNotEmpty(holdingsEntity.getBibliographicEntities())) {
                    for(BibliographicEntity bibliographicEntity : holdingsEntity.getBibliographicEntities()) {
                        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
                        for (SolrInputDocument holdingsSolrInputDocument : bibSolrInputDocument.getChildDocuments()) {
                            if (holdingsId.equals(holdingsSolrInputDocument.get(RecapConstants.HOLDING_ID).getValue())) {
                                holdingsSolrInputDocument.setField(RecapConstants.IS_DELETED_HOLDINGS, true);
                            }
                        }
                        solrTemplate.saveDocument(bibSolrInputDocument);
                    }
                }
                solrTemplate.commit();
            }
            return "Holdings documents updated successfully.";
        }catch(Exception ex){
            return "Holdings documents failed to update.";
        }
    }

    public String updateIsDeletedItemByItemIds(@RequestBody  List<Integer> itemIds){
        try{
            for(Integer itemId : itemIds){
                BibJSONUtil bibJSONUtil = new BibJSONUtil();
                ItemEntity itemEntity = itemDetailsRepository.findByItemId(itemId);
                if(itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                    for(BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingDetailRepository);
                        for (SolrInputDocument holdingsSolrInputDocument : bibSolrInputDocument.getChildDocuments()) {
                            for (SolrInputDocument itemSolrInputDocument : holdingsSolrInputDocument.getChildDocuments()) {
                                if (itemId.equals(itemSolrInputDocument.get(RecapConstants.ITEM_ID).getValue())) {
                                    itemSolrInputDocument.setField(RecapConstants.IS_DELETED_ITEM,true);;
                                }
                            }
                        }
                        solrTemplate.saveDocument(bibSolrInputDocument);
                    }
                }
                solrTemplate.commit();
            }
            return "Item documents updated successfully.";
        }catch(Exception ex){
            return "Item documents failed to update.";
        }
    }
}
