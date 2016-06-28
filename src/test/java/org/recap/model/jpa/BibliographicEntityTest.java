package org.recap.model.jpa;

import org.junit.Ignore;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Range;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/20/16.
 */
public class BibliographicEntityTest extends BaseTestCase {

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
        assertEquals(entity.getContent(),"Mock Bib Content");
        assertEquals(entity.getOwningInstitutionId().toString(),"3");
        System.out.println("owning institution bibId-->"+entity.getOwningInstitutionBibId());
        Long afterSave = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertTrue((beforeSaveCount+1) == afterSave);
        bibliographicDetailsRepository.delete(entity);

    }

    @Test
    public void findByInstitutionIdPagable() throws Exception {
        Long beforeSaveCount = bibliographicDetailsRepository.countByOwningInstitutionId(3);
        assertNotNull(beforeSaveCount);
        if(beforeSaveCount == 0 || beforeSaveCount<=3){
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
            BibliographicEntity entity2= bibliographicDetailsRepository.save(bibliographicEntity1);
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
            assertTrue( byInstitutionId.getContent().size()== 3);
            bibliographicDetailsRepository.delete(bibliographicEntity);
            bibliographicDetailsRepository.delete(bibliographicEntity1);
            bibliographicDetailsRepository.delete(bibliographicEntity2);
        }else{
            Page<BibliographicEntity> byInstitutionId = bibliographicDetailsRepository.findByOwningInstitutionId(new PageRequest(0, 3), 3);
            assertNotNull(byInstitutionId);
            assertTrue( byInstitutionId.getContent().size()== 3);
        }
    }



}
