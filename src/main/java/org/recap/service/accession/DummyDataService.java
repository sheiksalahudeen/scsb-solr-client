package org.recap.service.accession;

import org.recap.RecapConstants;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by premkb on 27/4/17.
 */
@Service
public class DummyDataService {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataService.class);

    private Map<String,Integer> collectionGroupMap;

    private Map<String,Integer> itemStatusMap;

    @Autowired
    private BibliographicDetailsRepository bibliographicDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private AccessionDAO accessionDAO;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is used to create dummy record when item barcode is not found in ILS.
     * @param owningInstitutionId the owning institution id
     * @param itemBarcode         the item barcode
     * @param customerCode        the customer code
     * @return the bibliographic entity
     */
    public BibliographicEntity createDummyDataAsIncomplete(Integer owningInstitutionId, String itemBarcode, String customerCode) {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        Date currentDate = new Date();
        try {
            updateBibWithDummyDetails(owningInstitutionId, bibliographicEntity, currentDate,RecapConstants.ACCESSION, String.valueOf(random.nextInt()));

            HoldingsEntity holdingsEntity = getHoldingsWithDummyDetails(owningInstitutionId, currentDate,RecapConstants.ACCESSION, String.valueOf(random.nextInt()));

            ItemEntity itemEntity = new ItemEntity();
            itemEntity.setCallNumberType(RecapConstants.DUMMY_CALL_NUMBER_TYPE);
            itemEntity.setCallNumber(RecapConstants.DUMMYCALLNUMBER);
            itemEntity.setCreatedDate(currentDate);
            itemEntity.setCreatedBy(RecapConstants.ACCESSION);
            itemEntity.setLastUpdatedDate(currentDate);
            itemEntity.setLastUpdatedBy(RecapConstants.ACCESSION);
            itemEntity.setBarcode(itemBarcode);
            itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
            itemEntity.setOwningInstitutionId(owningInstitutionId);
            itemEntity.setCollectionGroupId((Integer) getCollectionGroupMap().get(RecapConstants.NOT_AVAILABLE_CGD));
            itemEntity.setCustomerCode(customerCode);
            itemEntity.setItemAvailabilityStatusId((Integer) getItemStatusMap().get(RecapConstants.NOT_AVAILABLE));
            itemEntity.setDeleted(false);
            itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
            itemEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
            List<ItemEntity> itemEntityList = new ArrayList<>();
            itemEntityList.add(itemEntity);
            holdingsEntity.setItemEntities(itemEntityList);

            bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
            bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        } catch (Exception e) {
            logger.error(RecapConstants.LOG_ERROR,e);
        }
        BibliographicEntity savedBibliographicEntity = accessionDAO.saveBibRecord(bibliographicEntity);
        return savedBibliographicEntity;
    }

    public HoldingsEntity getHoldingsWithDummyDetails(Integer owningInstitutionId, Date currentDate, String createdBy, String owningInstitutionHoldingsId) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(getXmlContent(RecapConstants.DUMMY_HOLDING_CONTENT_XML).getBytes());
        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(createdBy);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        holdingsEntity.setOwningInstitutionHoldingsId(owningInstitutionHoldingsId);
        holdingsEntity.setLastUpdatedBy(createdBy);
        return holdingsEntity;
    }

    public void updateBibWithDummyDetails(Integer owningInstitutionId, BibliographicEntity bibliographicEntity, Date currentDate,
                                          String createdBy, String owningInstitutionBibId
    ) {
        bibliographicEntity.setContent(getXmlContent(RecapConstants.DUMMY_BIB_CONTENT_XML).getBytes());
        bibliographicEntity.setCreatedDate(currentDate);
        bibliographicEntity.setCreatedBy(createdBy);
        bibliographicEntity.setLastUpdatedBy(createdBy);
        bibliographicEntity.setLastUpdatedDate(currentDate);
        bibliographicEntity.setOwningInstitutionId(owningInstitutionId);
        bibliographicEntity.setOwningInstitutionBibId(owningInstitutionBibId);
        bibliographicEntity.setCatalogingStatus(RecapConstants.INCOMPLETE_STATUS);
    }

    private Map getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = (CollectionGroupEntity) iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getCollectionGroupId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return collectionGroupMap;
    }

    private Map getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = (ItemStatusEntity) iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getItemStatusId());
                }
            } catch (Exception e) {
                logger.error(RecapConstants.EXCEPTION,e);
            }
        }
        return itemStatusMap;
    }

    private String getXmlContent(String filename) {
        InputStream inputStream = getClass().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    out.append("\n");
                } else {
                    out.append(line);
                    out.append("\n");
                }
            }
        } catch (IOException e) {
            logger.error(RecapConstants.EXCEPTION,e);
        }
        return out.toString();
    }
}
