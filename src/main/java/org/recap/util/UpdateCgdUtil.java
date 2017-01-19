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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public String updateCGDForItem(String itemBarcode, String newCollectionGroupDesignation, String cgdChangeNotes) {
        String username = RecapConstants.GUEST;
        List<ItemEntity> itemEntities = new ArrayList<>();
        Date lastUpdatedDate = new Date();
        try {
            updateCGDForItemInDB(itemBarcode, newCollectionGroupDesignation, username, lastUpdatedDate);
            updateBibliographicWithLastUpdatedDate(itemBarcode,username,lastUpdatedDate);
            updateCGDForItemInSolr(itemBarcode, itemEntities);
            saveItemChangeLogEntity(itemEntities, username, lastUpdatedDate, RecapConstants.UPDATE_CGD, cgdChangeNotes);
            return RecapConstants.SUCCESS;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return RecapConstants.FAILURE + "-" + e.getMessage();
        }
    }

    public void updateCGDForItemInDB(String itemBarcode, String newCollectionGroupDesignation, String username, Date lastUpdatedDate) {
        CollectionGroupEntity collectionGroupEntity = collectionGroupDetailsRepository.findByCollectionGroupCode(newCollectionGroupDesignation);
        itemDetailsRepository.updateCollectionGroupIdByItemBarcode(collectionGroupEntity.getCollectionGroupId(), itemBarcode, username, lastUpdatedDate);
    }

    public void updateBibliographicWithLastUpdatedDate(String itemBarcode,String userName,Date lastUpdatedDate){
        List<ItemEntity> itemEntityList = itemDetailsRepository.findByBarcode(itemBarcode);
        for(ItemEntity itemEntity:itemEntityList){
            List<BibliographicEntity> bibliographicEntityList = itemEntity.getBibliographicEntities();
            List<Integer> bibliographicIdList = new ArrayList<>();
            for(BibliographicEntity bibliographicEntity : bibliographicEntityList){
                bibliographicIdList.add(bibliographicEntity.getBibliographicId());
            }
            bibliographicDetailsRepository.updateBibItemLastUpdatedDate(bibliographicIdList,userName,lastUpdatedDate);
        }
    }

    public void updateCGDForItemInSolr(String itemBarcode, List<ItemEntity> itemEntities) {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        itemEntities.addAll(itemDetailsRepository.findByBarcode(itemBarcode));
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                if (itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                    for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
                        solrTemplate.saveDocument(bibSolrInputDocument);
                        solrTemplate.commit();
                    }
                }
            }
        }
    }

    private void saveItemChangeLogEntity(List<ItemEntity> itemEntities, String username, Date lastUpdatedDate, String operationType, String notes) {
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
                itemChangeLogEntity.setUpdatedBy(username);
                itemChangeLogEntity.setUpdatedDate(lastUpdatedDate);
                itemChangeLogEntity.setOperationType(operationType);
                itemChangeLogEntity.setRecordId(itemEntity.getItemId());
                itemChangeLogEntity.setNotes(notes);
                itemChangeLogDetailsRepository.save(itemChangeLogEntity);
            }
        }
    }
}
