package org.recap.model.jpa;


import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/20/16.
 */
public class BibliographicEntityTest extends BaseTestCase {

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;


    @Test
    public void findByInstitutionId() throws Exception {
        Long beforeSaveCount = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertNotNull(beforeSaveCount);
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random));
        bibliographicEntity.setOwningInstitutionId(3);
        BibliographicEntity entity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(entity);
        assertEquals(entity.getContent(), "Mock Bib Content");
        assertEquals(entity.getOwningInstitutionId().toString(), "3");
        System.out.println("owning institution bibId-->" + entity.getOwningInstitutionBibId());
        Long afterSave = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertTrue((beforeSaveCount + 1) == afterSave);
        bibliographicDetailsRepository.delete(entity);

    }

    @Test
    public void findByInstitutionIdPagable() throws Exception {
        Long beforeSaveCount = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertNotNull(beforeSaveCount);
        if (beforeSaveCount == 0 || beforeSaveCount <= 3) {
            Random random = new Random();
            BibliographicEntity bibliographicEntity = new BibliographicEntity();
            bibliographicEntity.setContent("Mock Bib Content1");
            bibliographicEntity.setCreatedDate(new Date());
            bibliographicEntity.setLastUpdatedDate(new Date());
            bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random));
            bibliographicEntity.setOwningInstitutionId(3);
            BibliographicEntity entity1 = bibliographicDetailsRepository.save(bibliographicEntity);
            assertNotNull(entity1);

            Random random1 = new Random();
            BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
            bibliographicEntity1.setContent("Mock Bib Content2");
            bibliographicEntity1.setCreatedDate(new Date());
            bibliographicEntity1.setLastUpdatedDate(new Date());
            bibliographicEntity1.setOwningInstitutionBibId(String.valueOf(random1));
            bibliographicEntity1.setOwningInstitutionId(3);
            BibliographicEntity entity2 = bibliographicDetailsRepository.save(bibliographicEntity1);
            assertNotNull(entity2);

            Random random2 = new Random();
            BibliographicEntity bibliographicEntity2 = new BibliographicEntity();
            bibliographicEntity2.setContent("Mock Bib Content3");
            bibliographicEntity2.setCreatedDate(new Date());
            bibliographicEntity2.setLastUpdatedDate(new Date());
            bibliographicEntity2.setOwningInstitutionBibId(String.valueOf(random2));
            bibliographicEntity2.setOwningInstitutionId(3);
            BibliographicEntity entity3 = bibliographicDetailsRepository.save(bibliographicEntity2);
            assertNotNull(entity3);

            Page<BibliographicEntity> byInstitutionId = bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(0, 3), 3);
            assertNotNull(byInstitutionId);
            assertTrue(byInstitutionId.getContent().size() == 3);
            bibliographicDetailsRepository.delete(bibliographicEntity);
            bibliographicDetailsRepository.delete(bibliographicEntity1);
            bibliographicDetailsRepository.delete(bibliographicEntity2);
        } else {
            Page<BibliographicEntity> byInstitutionId = bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(0, 3), 3);
            assertNotNull(byInstitutionId);
            assertTrue(byInstitutionId.getContent().size() == 3);
        }
    }

    @Test
    public void saveBibSingleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
    }

    @Test
    public void saveBibMultipleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));


        HoldingsEntity holdingsEntity1 = new HoldingsEntity();
        holdingsEntity1.setContent("mock holdings");
        holdingsEntity1.setCreatedDate(new Date());
        holdingsEntity1.setLastUpdatedDate(new Date());
        holdingsEntity1.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));


        HoldingsEntity holdingsEntity2 = new HoldingsEntity();
        holdingsEntity2.setContent("mock holdings");
        holdingsEntity2.setCreatedDate(new Date());
        holdingsEntity2.setLastUpdatedDate(new Date());
        holdingsEntity2.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity1, holdingsEntity2));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(0).getHoldingsId());
        assertNotNull(savedBibliographicEntity.getHoldingsEntities().get(1).getHoldingsId());
    }

    @Test
    public void saveMultipleBibSingleHoldings() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity1 = new BibliographicEntity();
        bibliographicEntity1.setContent("mock Content");
        bibliographicEntity1.setCreatedDate(new Date());
        bibliographicEntity1.setLastUpdatedDate(new Date());
        bibliographicEntity1.setOwningInstitutionId(1);
        bibliographicEntity1.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        BibliographicEntity bibliographicEntity2 = new BibliographicEntity();
        bibliographicEntity2.setContent("mock Content");
        bibliographicEntity2.setCreatedDate(new Date());
        bibliographicEntity2.setLastUpdatedDate(new Date());
        bibliographicEntity2.setOwningInstitutionId(1);
        bibliographicEntity2.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        bibliographicEntity1.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity2.setHoldingsEntities(Arrays.asList(holdingsEntity));

        Iterable<BibliographicEntity> savedBibliographicEntities = bibliographicDetailsRepository.save(Arrays.asList(bibliographicEntity1, bibliographicEntity2));
        assertNotNull(savedBibliographicEntities);
    }

    @Test
    public void saveBibSingleHoldingsSingleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
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
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntity(holdingsEntity);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
        assertNotNull(savedBibliographicEntity.getHoldingsEntities());
        assertNotNull(savedBibliographicEntity.getItemEntities());

    }

    @Test
    public void saveBibSingleHoldingsMultipleItem() throws Exception {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content");
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));


        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity1 = new ItemEntity();
        itemEntity1.setLastUpdatedDate(new Date());
        itemEntity1.setCreatedDate(new Date());
        itemEntity1.setCustomerCode("1");
        itemEntity1.setItemAvailabilityStatusId(1);
        itemEntity1.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity1.setOwningInstitutionId(1);
        itemEntity1.setBarcode("123");
        itemEntity1.setCallNumber("x.12321");
        itemEntity1.setCollectionGroupId(1);
        itemEntity1.setCallNumberType("1");
        itemEntity1.setHoldingsEntity(holdingsEntity);


        ItemEntity itemEntity2 = new ItemEntity();
        itemEntity2.setLastUpdatedDate(new Date());
        itemEntity2.setCreatedDate(new Date());
        itemEntity2.setItemAvailabilityStatusId(1);
        itemEntity2.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity2.setOwningInstitutionId(1);
        itemEntity2.setBarcode("123");
        itemEntity2.setCallNumber("x.12321");
        itemEntity2.setCollectionGroupId(1);
        itemEntity2.setCallNumberType("1");
        itemEntity2.setCustomerCode("123");
        itemEntity2.setHoldingsEntity(holdingsEntity);

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));


        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity1, itemEntity2));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.save(bibliographicEntity);
        assertNotNull(savedBibliographicEntity);
    }


}
