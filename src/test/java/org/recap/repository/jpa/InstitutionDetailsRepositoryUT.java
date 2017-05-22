package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pvsubrah on 6/22/16.
 */
public class InstitutionDetailsRepositoryUT extends BaseTestCase {

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void saveAndFind() throws Exception {
        assertNotNull(institutionDetailsRepository);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("test");
        institutionEntity.setInstitutionName("test");

        InstitutionEntity savedInstitutionEntity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(savedInstitutionEntity);
        assertNotNull(savedInstitutionEntity.getInstitutionId());
        assertEquals(savedInstitutionEntity.getInstitutionCode(), "test");
        assertEquals(savedInstitutionEntity.getInstitutionName(), "test");

        InstitutionEntity byInstitutionCode = institutionDetailsRepository.findByInstitutionCode("test");
        assertNotNull(byInstitutionCode);

        InstitutionEntity byInstitutionName = institutionDetailsRepository.findByInstitutionName("test");
        assertNotNull(byInstitutionName);
    }

    @Test
    public void findByInstitutionCodeNotIn() throws Exception {
        List<InstitutionEntity> institutionEntities = institutionDetailsRepository.findByInstitutionCodeNotIn(Arrays.asList("PUL"));
        assertNotNull(institutionEntities);
        for(InstitutionEntity institutionEntity : institutionEntities) {
            assertNotEquals(institutionEntity.getInstitutionCode(), "PUL");
        }
    }

}