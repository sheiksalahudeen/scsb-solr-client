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

    Page<RoleEntity> findByRoleName(String roleName, Pageable pageable);


    RoleEntity findByRoleName(String roleName);

    @Query(value = "select roles from RoleEntity roles where roles.roleName = :roleName")
    Page<RoleEntity> findByRoleName(Pageable pageable, @Param("roleName") String roleName);



}
