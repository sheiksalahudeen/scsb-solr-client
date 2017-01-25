package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.RoleEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 22/12/16.
 */
public class RolesDetailsRepositortyUT extends BaseTestCase{

    @Autowired
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Test
    public void testByRoleName(){
        RoleEntity roleEntity = rolesDetailsRepositorty.findByRoleName("Search");
        assertNotNull(roleEntity);
    }

    @Test
    public void testPagination(){

    }

}