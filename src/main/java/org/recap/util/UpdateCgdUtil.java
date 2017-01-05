package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.*;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by rajeshbabuk on 5/1/17.
 */
@Service
public class UpdateCgdUtil {

    Logger logger = LoggerFactory.getLogger(UpdateCgdUtil.class);

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    HoldingsDetailsRepository holdingsDetailsRepository;

    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    @Autowired
    CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    ItemCrudRepository itemSolrCrudRepository;

    @Autowired
    SolrTemplate solrTemplate;

    public String updateCGDForItem(Integer itemId, String newCollectionGroupDesignation, String cgdChangeNotes) {
        String userName = RecapConstants.GUEST;
        Date lastUpdatedDate = new Date();
        try {
            updateCGDForItemInDB(itemId, newCollectionGroupDesignation, userName, lastUpdatedDate);
            updateCGDForItemInSolr(itemId, newCollectionGroupDesignation);
            saveItemChangeLogEntity(itemId, userName, lastUpdatedDate, RecapConstants.UPDATE_CGD, cgdChangeNotes);
            return RecapConstants.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return RecapConstants.FAILURE + "-" + e.getMessage();
        }
    }

    public void updateCGDForItemInDB(Integer itemId, String newCollectionGroupDesignation, String userName, Date lastUpdatedDate) {
        CollectionGroupEntity collectionGroupEntity = collectionGroupDetailsRepository.findByCollectionGroupCode(newCollectionGroupDesignation);
        itemDetailsRepository.updateCollectionGroupIdByItemId(collectionGroupEntity.getCollectionGroupId(), itemId, userName, lastUpdatedDate);
    }

    public void updateCGDForItemInSolr(Integer itemId, String newCollectionGroupDesignation) {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        ItemEntity itemEntity = itemDetailsRepository.findByItemId(itemId);
        if (itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
            for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
                if (CollectionUtils.isNotEmpty(bibSolrInputDocument.getChildDocuments())) {
                    for (SolrInputDocument holdingsSolrInputDocument : bibSolrInputDocument.getChildDocuments()) {
                        if (CollectionUtils.isNotEmpty(holdingsSolrInputDocument.getChildDocuments())) {
                            for (SolrInputDocument itemSolrInputDocument : holdingsSolrInputDocument.getChildDocuments()) {
                                if (itemId.equals(itemSolrInputDocument.get(RecapConstants.ITEM_ID).getValue())) {
                                    itemSolrInputDocument.setField(RecapConstants.COLLECTION_GROUP_DESIGNATION, newCollectionGroupDesignation);
                                }
                            }
                        }
                    }
                }
                solrTemplate.saveDocument(bibSolrInputDocument);
            }
        }
        solrTemplate.commit();
    }

    private void saveItemChangeLogEntity(Integer recordId, String userName, Date lastUpdatedDate, String operationType, String notes) {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setUpdatedBy(userName);
        itemChangeLogEntity.setUpdatedDate(lastUpdatedDate);
        itemChangeLogEntity.setOperationType(operationType);
        itemChangeLogEntity.setRecordId(recordId);
        itemChangeLogEntity.setNotes(notes);
        itemChangeLogDetailsRepository.save(itemChangeLogEntity);
    }
}
