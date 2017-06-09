package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RoleEntityUT extends BaseTestCase{

    @Autowired
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Test
    public void saveRole(){
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName("test role");
        roleEntity.setRoleDescription("test role");
        RoleEntity savedRoleEntity = rolesDetailsRepositorty.save(roleEntity);
        assertNotNull(savedRoleEntity);
        assertNotNull(savedRoleEntity.getRoleId());
    }

}