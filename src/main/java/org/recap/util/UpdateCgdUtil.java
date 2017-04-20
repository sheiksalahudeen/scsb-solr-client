package org.recap.util;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.model.camel.EmailPayLoad;
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
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 5/1/17.
 */
@Service
public class UpdateCgdUtil {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCgdUtil.class);

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

    @Autowired
    ProducerTemplate producerTemplate;

    public String updateCGDForItem(String itemBarcode, String owningInstitution, String oldCollectionGroupDesignation, String newCollectionGroupDesignation, String cgdChangeNotes) {
        String username = RecapConstants.GUEST;
        List<ItemEntity> itemEntities = new ArrayList<>();
        Date lastUpdatedDate = new Date();
        String cgdChangeLog = oldCollectionGroupDesignation + RecapConstants.TO + newCollectionGroupDesignation;
        try {
            updateCGDForItemInDB(itemBarcode, newCollectionGroupDesignation, username, lastUpdatedDate);
            itemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
            setCGDChangeLogToItemEntity(cgdChangeLog,itemEntities);
            updateCGDForItemInSolr(itemEntities);
            saveItemChangeLogEntity(itemEntities, username, lastUpdatedDate, RecapConstants.UPDATE_CGD, cgdChangeNotes);
            sendEmail(itemBarcode, owningInstitution, oldCollectionGroupDesignation, newCollectionGroupDesignation, cgdChangeNotes);
            return RecapConstants.SUCCESS;
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
            return RecapConstants.FAILURE + "-" + e.getMessage();
        }
    }

    public void updateCGDForItemInDB(String itemBarcode, String newCollectionGroupDesignation, String username, Date lastUpdatedDate) {
        CollectionGroupEntity collectionGroupEntity = collectionGroupDetailsRepository.findByCollectionGroupCode(newCollectionGroupDesignation);
        itemDetailsRepository.updateCollectionGroupIdByItemBarcode(collectionGroupEntity.getCollectionGroupId(), itemBarcode, username, lastUpdatedDate);
    }

    public void updateCGDForItemInSolr(List<ItemEntity> itemEntities) {
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            for (ItemEntity itemEntity : itemEntities) {
                if (itemEntity != null && CollectionUtils.isNotEmpty(itemEntity.getBibliographicEntities())) {
                    for (BibliographicEntity bibliographicEntity : itemEntity.getBibliographicEntities()) {
                        StopWatch stopWatchIndexDocument = new StopWatch();
                        stopWatchIndexDocument.start();
                        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
                        solrTemplate.saveDocument(bibSolrInputDocument,1);
                        stopWatchIndexDocument.stop();
                        logger.info("Time taken to index the doc for updateCGDForItemInSolr--->{}sec",stopWatchIndexDocument.getTotalTimeSeconds());
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

    private void sendEmail(String itemBarcode, String owningInstitution, String oldCollectionGroupDesignation, String newCollectionGroupDesignation, String cgdChangeNotes) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setItemBarcode(itemBarcode);
        emailPayLoad.setItemInstitution(owningInstitution);
        emailPayLoad.setOldCgd(oldCollectionGroupDesignation);
        emailPayLoad.setNewCgd(newCollectionGroupDesignation);
        emailPayLoad.setNotes(cgdChangeNotes);
        producerTemplate.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_FOR, RecapConstants.UPDATECGD);
    }

    private void setCGDChangeLogToItemEntity(String cgdChangeLog,List<ItemEntity> itemEntityList){
        for(ItemEntity itemEntity:itemEntityList){
            itemEntity.setCgdChangeLog(cgdChangeLog);
        }
    }
}
