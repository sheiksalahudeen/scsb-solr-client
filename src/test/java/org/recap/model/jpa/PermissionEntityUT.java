package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.repository.jpa.PermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 25/1/17.
 */
public class PermissionEntityUT extends BaseTestCase{

    @Autowired
    PermissionsRepository permissionsRepository;

    @Test
    public void savePermission(){
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionName("new admin");
        permissionEntity.setPermissionDesc("new admin");
        PermissionEntity savedPermissionEntity = permissionsRepository.save(permissionEntity);
        assertNotNull(savedPermissionEntity);
        assertNotNull(savedPermissionEntity.getPermissionId());
    }


}