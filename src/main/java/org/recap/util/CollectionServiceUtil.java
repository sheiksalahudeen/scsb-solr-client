package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.ItemChangeLogEntity;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.repository.jpa.*;
import org.recap.repository.solr.main.ItemCrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * Created by rajeshbabuk on 19/10/16.
 */
@Service
public class CollectionServiceUtil {

    Logger logger = LoggerFactory.getLogger(CollectionServiceUtil.class);

    @Value("${solr.server.protocol}")
    String serverProtocol;

    @Value("${scsb.url}")
    String scsbUrl;

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

    public void deaccessionItem(BibliographicMarcForm bibliographicMarcForm) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = serverProtocol + scsbUrl + RecapConstants.DEACCESSION_URL;
            String itemBarcode = bibliographicMarcForm.getBarcode();

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(itemBarcode);
            jsonObject.put(RecapConstants.ITEM_BARCODES, jsonArray);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(RecapConstants.API_KEY, RecapConstants.RECAP);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            String deaccessionRestResponse = restTemplate.postForObject(url, httpEntity, String.class);
            JSONObject deaccessionResponseJson = new JSONObject(deaccessionRestResponse);
            String resultMessage = (String) deaccessionResponseJson.get(itemBarcode);
            if (StringUtils.isNotBlank(resultMessage)) {
                if (RecapConstants.SUCCESS.equals(resultMessage)) {
                    String userName = RecapConstants.GUEST;
                    Date lastUpdatedDate = new Date();
                    saveItemChangeLogEntity(bibliographicMarcForm.getItemId(), userName, lastUpdatedDate, RecapConstants.DEACCESSION, bibliographicMarcForm.getDeaccessionNotes());
                    bibliographicMarcForm.setSubmitted(true);
                    bibliographicMarcForm.setMessage(RecapConstants.DEACCESSION_SUCCESSFUL);
                } else {
                    String failureMessage = resultMessage.replace(RecapConstants.FAILURE + " -", "");
                    bibliographicMarcForm.setErrorMessage(RecapConstants.DEACCESSION_FAILED + "-" + failureMessage);
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage());
            bibliographicMarcForm.setErrorMessage(RecapConstants.DEACCESSION_FAILED + "-" + e.getMessage());
        }
    }
}
