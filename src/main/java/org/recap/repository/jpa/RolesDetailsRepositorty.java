package org.recap.repository.jpa;

import org.recap.model.jpa.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Created by dharmendrag on 13/12/16.
 */
public interface RolesDetailsRepositorty extends JpaRepository<RoleEntity, Integer> {

    /**
     * Finds pageable list of RoleEntity based on the given role name.
     *
     * @param roleName the role name
     * @param pageable the pageable
     * @return the page
     */
    Page<RoleEntity> findByRoleName(String roleName, Pageable pageable);


    /**
     * Finds RoleEntity based on the given role name.
     *
     * @param roleName the role name
     * @return the role entity
     */
    RoleEntity findByRoleName(String roleName);

    /**
     * Finds pagable list of RoleEntity based on the given role name.
     *
     * @param pageable the pageable
     * @param roleName the role name
     * @return the page
     */
    @Query(value = "select roles from RoleEntity roles where roles.roleName = :roleName")
    Page<RoleEntity> findByRoleName(Pageable pageable, @Param("roleName") String roleName);



}
