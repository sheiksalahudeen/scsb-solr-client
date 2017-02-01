package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 25/1/17.
 */
public class UsersEntityUT extends BaseTestCase{

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Test
    public void saveUsers(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        UsersEntity usersEntity = new UsersEntity();
        usersEntity.setLoginId("123");
        usersEntity.setInstitutionId(entity.getInstitutionId());
        usersEntity.setUserDescription("test");
        usersEntity.setCreatedBy("test");
        usersEntity.setCreatedDate(new Date());
        usersEntity.setLastUpdatedDate(new Date());
        usersEntity.setLastUpdatedBy("test");
        UsersEntity savedUserEntity = userDetailsRepository.save(usersEntity);
        assertNotNull(savedUserEntity);
        assertNotNull(savedUserEntity.getUserId());

    }

}