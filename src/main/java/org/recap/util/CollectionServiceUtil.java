package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.recap.RecapConstants;
import org.recap.model.deAccession.DeAccessionRequest;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.repository.jpa.*;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.recap.service.deAccession.DeAccessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by rajeshbabuk on 19/10/16.
 */
@Service
public class CollectionServiceUtil {

    Logger logger = LoggerFactory.getLogger(CollectionServiceUtil.class);

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
    DeAccessionService deAccessionService;

    public void updateCGDForItem(BibliographicMarcForm bibliographicMarcForm) {
        String userName = RecapConstants.GUEST;
        Date lastUpdatedDate = new Date();
        try {
            updateCGDForItemInDB(bibliographicMarcForm, userName, lastUpdatedDate);
            updateCGDForItemInSolr(bibliographicMarcForm);
            saveItemChangeLogEntity(bibliographicMarcForm.getItemId(), userName, lastUpdatedDate, RecapConstants.UPDATE_CGD, bibliographicMarcForm.getCgdChangeNotes());
            bibliographicMarcForm.setSubmitted(true);
            bibliographicMarcForm.setMessage(RecapConstants.CGD_UPDATE_SUCCESSFUL);
        } catch (Exception e) {
            logger.error(e.getMessage());
            bibliographicMarcForm.setErrorMessage(RecapConstants.CGD_UPDATE_FAILED + "-" + e.getMessage());
        }
    }

    public void updateCGDForItemInDB(BibliographicMarcForm bibliographicMarcForm, String userName, Date lastUpdatedDate) {
        CollectionGroupEntity collectionGroupEntity = collectionGroupDetailsRepository.findByCollectionGroupCode(bibliographicMarcForm.getNewCollectionGroupDesignation());
        itemDetailsRepository.updateCollectionGroupIdByItemId(collectionGroupEntity.getCollectionGroupId(), bibliographicMarcForm.getItemId(), userName, lastUpdatedDate);
    }

    public void updateCGDForItemInSolr(BibliographicMarcForm bibliographicMarcForm) {
        BibliographicEntity bibliographicEntity = bibliographicDetailsRepository.findByBibliographicId(bibliographicMarcForm.getBibId());
        BibJSONUtil bibJSONUtil = new BibJSONUtil();
        SolrInputDocument bibSolrInputDocument = bibJSONUtil.generateBibAndItemsForIndex(bibliographicEntity, solrTemplate, bibliographicDetailsRepository, holdingsDetailsRepository);
        if (CollectionUtils.isNotEmpty(bibSolrInputDocument.getChildDocuments())) {
            for (SolrInputDocument holdingsSolrInputDocument : bibSolrInputDocument.getChildDocuments()) {
                if (CollectionUtils.isNotEmpty(holdingsSolrInputDocument.getChildDocuments())) {
                    for (SolrInputDocument itemSolrInputDocument : holdingsSolrInputDocument.getChildDocuments()) {
                        if (bibliographicMarcForm.getItemId().equals(itemSolrInputDocument.get(RecapConstants.ITEM_ID).getValue())) {
                            itemSolrInputDocument.setField(RecapConstants.COLLECTION_GROUP_DESIGNATION, bibliographicMarcForm.getNewCollectionGroupDesignation());
                        }
                    }
                }
            }
        }
        solrTemplate.saveDocument(bibSolrInputDocument);
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

    public void deAccessionItem(BibliographicMarcForm bibliographicMarcForm) {
        try {
            DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
            String itemBarcode = bibliographicMarcForm.getBarcode();
            deAccessionRequest.setItemBarcodes(Arrays.asList(itemBarcode));
            Map<String, String> resultMap = deAccessionService.deAccession(deAccessionRequest);
            String resultMessage = resultMap.get(itemBarcode);
            if (StringUtils.isNotBlank(resultMessage)) {
                if (RecapConstants.SUCCESS.equals(resultMessage)) {
                    String userName = RecapConstants.GUEST;
                    Date lastUpdatedDate = new Date();
                    saveItemChangeLogEntity(bibliographicMarcForm.getItemId(), userName, lastUpdatedDate, RecapConstants.DEACCESSION, bibliographicMarcForm.getDeaccessionNotes());
                    bibliographicMarcForm.setSubmitted(true);
                    bibliographicMarcForm.setMessage(RecapConstants.DEACCESSION_SUCCESSFUL);
                } else {
                    String failureMessage = resultMessage.replace(RecapConstants.FAILURE + " -", "");
                    bibliographicMarcForm.setErrorMessage(RecapConstants.DEACCESSION_FAILED + " - " + failureMessage);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            bibliographicMarcForm.setErrorMessage(RecapConstants.DEACCESSION_FAILED + " - " + e.getMessage());
        }
    }
}