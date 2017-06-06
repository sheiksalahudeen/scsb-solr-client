package org.recap.repository.jpa;

import org.recap.model.jpa.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Created by dharmendrag on 13/12/16.
 */
public interface PermissionsRepository extends JpaRepository<PermissionEntity,Integer> {

    /**
     * Finds PermissionEntity based on the given permission name.
     *
     * @param permissionDesc the permission desc
     * @return the permission entity
     */
    PermissionEntity findByPermissionName(String permissionDesc);

    /**
     * Finds a list of permission entities based on the given permission name.
     *
     * @param pageable       the pageable
     * @param permissionName the permission name
     * @return the page
     */
    @Query(value = "select permission from PermissionEntity permission where permission.permissionName = :permissionName")
    Page<PermissionEntity> findByPermissionName(Pageable pageable, @Param("permissionName") String permissionName);
}
