package org.recap.repository.jpa;

import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.jpa.UsersEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Repository
public interface UserDetailsRepository extends JpaRepository<UsersEntity,Integer>,JpaSpecificationExecutor {


    /**
     * Finds UsersEntity based on the given login id .
     *
     * @param loginId the login id
     * @return the UsersEntity
     */
    UsersEntity findByLoginId(String loginId);

    /**
     * Finds UsersEntity based on the given login id and institution entity .
     *
     * @param loginId       the login id
     * @param institutionId the institution id
     * @return the UsersEntity
     */
    UsersEntity findByLoginIdAndInstitutionEntity(String loginId,InstitutionEntity institutionId);

    /**
     * Finds UsersEntity based on the given user id.
     *
     * @param userId the user id
     * @return the UsersEntity
     */
    UsersEntity findByUserId(Integer userId);

    /**
     * Gets user's password for validating the user based on the given login id.
     *
     * @param loginId the login id
     * @return the string
     */
    @Query(value = "select userT.passwrd from user_master_t userT where userT.login_id=:loginId",nativeQuery = true)
    String validateUser(@Param("loginId") String loginId);

    /**
     * Gets RoleEntity based on the given loginId.
     *
     * @param loginId the login id
     * @return the RoleEntity
     */
    @Query(value="select roleT.role_name from role_master_t roleT,user_master_t userT where userT.user_role_id=roleT.role_id",nativeQuery = true)
    RoleEntity userRole(@Param("loginId") String loginId);

    @Override
    Page<UsersEntity> findAll(Pageable pageable);

    /**
     * Finds UsersEntity based on the given institution entity.
     *
     * @param institutionId the institution id
     * @param pageable      the pageable
     * @return the pageable UsersEntity
     */
    Page<UsersEntity> findByInstitutionEntity(InstitutionEntity institutionId,Pageable pageable);

    /**
     * Finds UsersEntity based on the given login id.
     *
     * @param loginId  the login id
     * @param pageable the pageable
     * @return the pageable UsersEntity
     */
    Page<UsersEntity> findByLoginId(String loginId,Pageable pageable);

    /**
     * Finds UsersEntity based on the given login id and institution entity.
     *
     * @param loginId       the login id
     * @param institutionId the institution id
     * @param pageable      the pageable
     * @return the pageable UsersEntity
     */
    Page<UsersEntity> findByLoginIdAndInstitutionEntity(String loginId,InstitutionEntity institutionId,Pageable pageable);



}
