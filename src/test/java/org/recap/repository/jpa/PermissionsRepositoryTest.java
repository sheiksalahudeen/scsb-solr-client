package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.PermissionEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by hemalathas on 23/12/16.
 */
public class PermissionsRepositoryTest extends BaseTestCase{

    @Autowired
    PermissionsRepository permissionsRepository;

    @Test
    public void testPermissionByDescription(){
        PermissionEntity permissionEntity = permissionsRepository.findByPermissionName("BarcodeRestriction");
        assertNotNull(permissionEntity);
    }

}