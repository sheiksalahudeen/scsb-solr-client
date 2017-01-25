package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.PatronDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 17/1/17.
 */
public class PatronEntityUT extends BaseTestCase{

    @Autowired
    PatronDetailsRepository patronDetailsRepository;

    @Test
    public void createPatronTest(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        PatronEntity patronEntity = new PatronEntity();
        patronEntity.setInstitutionIdentifier(entity.getInstitutionCode());
        patronEntity.setInstitutionId(entity.getInstitutionId());
        patronEntity.setEmailId("hamalatha.s@htcindia.com");
        PatronEntity savedPatronEntity = patronDetailsRepository.save(patronEntity);
        assertNotNull(savedPatronEntity);
    }


}