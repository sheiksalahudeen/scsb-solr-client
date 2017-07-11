package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 17/1/17.
 */
public class RequestItemEntityUT extends BaseTestCase{

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Test
    public void createRequestItem() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        assertNotNull(savedRequestTypeEntity);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("REFILE");
        requestStatusEntity.setRequestStatusDescription("REFILE");

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(savedRequestTypeEntity.getRequestTypeId());
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(4);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setPatronId(1);
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        assertNotNull(savedRequestItemEntity);
    }

    @Test
    public void testRequestItemEntity() throws Exception {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        RequestStatusEntity requestStatusEntity = new RequestStatusEntity();
        requestStatusEntity.setRequestStatusId(1);
        requestStatusEntity.setRequestStatusCode("REFILE");
        requestStatusEntity.setRequestStatusDescription("REFILE");

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        assertNotNull(savedRequestTypeEntity);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setRequestId(1);
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getItemId());
        requestItemEntity.setRequestTypeId(savedRequestTypeEntity.getRequestTypeId());
        requestItemEntity.setRequestingInstitutionId(1);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(4);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setItemEntity(bibliographicEntity.getItemEntities().get(0));
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);

        assertNotNull(requestItemEntity.getRequestId());
        assertNotNull(requestItemEntity.getItemId());
        assertNotNull(requestItemEntity.getRequestTypeId());
        assertNotNull(requestItemEntity.getRequestingInstitutionId());
        assertNotNull(requestItemEntity.getRequestExpirationDate());
        assertNotNull(requestItemEntity.getCreatedBy());
        assertNotNull(requestItemEntity.getCreatedDate());
        assertNotNull(requestItemEntity.getLastUpdatedDate());
        assertNotNull(requestItemEntity.getStopCode());
        assertNotNull(requestItemEntity.getRequestStatusId());
        assertNotNull(requestItemEntity.getInstitutionEntity());
        assertNotNull(requestItemEntity.getRequestTypeEntity());
        assertNotNull(requestItemEntity.getItemEntity());
        assertNotNull(requestItemEntity.getRequestStatusEntity());
        assertNotNull(requestItemEntity.getNotes());

        assertNotNull(requestStatusEntity.getRequestStatusId());
        assertNotNull(requestStatusEntity.getRequestStatusCode());
        assertNotNull(requestStatusEntity.getRequestStatusDescription());

        assertNotNull(requestTypeEntity.getRequestTypeId());
        assertNotNull(requestTypeEntity.getRequestTypeCode());
        assertNotNull(requestTypeEntity.getRequestTypeDesc());
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("3123");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());
        return savedBibliographicEntity;

    }

}