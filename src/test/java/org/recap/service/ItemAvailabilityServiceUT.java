package org.recap.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabilityResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/2/17.
 */
public class ItemAvailabilityServiceUT extends BaseTestCase {

    @Autowired
    ItemAvailabilityService itemAvailabilityService;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testItemAvailabilityService() throws Exception {
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        String barcode = savedBibliographicEntity.getItemEntities().get(0).getBarcode();
        String response = itemAvailabilityService.getItemStatusByBarcodeAndIsDeletedFalse(barcode);
        assertNotNull(response);
        assertEquals(response, "Available");
    }

    @Test
    public void testGetItemStatusByBarcodeAndIsDeletedFalseList() throws Exception {
        List<String> barcodeList = Arrays.asList("32101045675921", "32101099791665", "32101086866140", "CU73995576","6668877");
        List<ItemAvailabilityResponse> itemAvailabilityResponses = itemAvailabilityService.getItemStatusByBarcodeAndIsDeletedFalseList(barcodeList);
        assertNotNull(itemAvailabilityResponses);
    }

    @Test
    public void testgetbibItemAvaiablityStatus() throws Exception {
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("93540");
        bibItemAvailabityStatusRequest.setInstitutionId("PUL");
        List<ItemAvailabilityResponse> itemAvailabilityResponses = itemAvailabilityService.getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);

        bibItemAvailabityStatusRequest.setBibliographicId("66056");
        bibItemAvailabityStatusRequest.setInstitutionId("CUL");
        itemAvailabilityResponses = itemAvailabilityService.getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);

        bibItemAvailabityStatusRequest.setBibliographicId("59321");
        bibItemAvailabityStatusRequest.setInstitutionId("SCSB");
        itemAvailabilityResponses = itemAvailabilityService.getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);

        bibItemAvailabityStatusRequest.setBibliographicId("0000");
        bibItemAvailabityStatusRequest.setInstitutionId("PUL");
        itemAvailabilityResponses = itemAvailabilityService.getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);

        bibItemAvailabityStatusRequest.setBibliographicId("0000");
        bibItemAvailabityStatusRequest.setInstitutionId("PU");
        itemAvailabilityResponses = itemAvailabilityService.getbibItemAvaiablityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(itemAvailabilityResponses);
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(entity.getInstitutionId());
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
        itemEntity.setBarcode("123");
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
        return savedBibliographicEntity;
    }

}