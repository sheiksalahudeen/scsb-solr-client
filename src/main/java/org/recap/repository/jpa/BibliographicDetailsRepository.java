package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicPK;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
@RepositoryRestResource(collectionResourceRel = "bibliographic", path = "bibliographic")
public interface BibliographicDetailsRepository extends JpaRepository<BibliographicEntity, BibliographicPK> {

    /**
     * Gets the count the number of bibs by using IsDeleted field which has false.
     *
     * @return the long
     */
    Long countByIsDeletedFalse();

    /**
     * Gets the count the number of bibs by using owning institution id.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionId(Integer owningInstitutionId);

    /**
     * Gets the count the number of bibs by using the date in the parameter.
     *
     * @param createdDate the created date
     * @return the long
     */
    Long countByLastUpdatedDateAfter(Date createdDate);

    /**
     * Gets the count the number of bibs by using owning institution id and last updated date.
     *
     * @param owningInstitutionId the owning institution id
     * @param createdDate         the created date
     * @return the long
     */
    Long countByOwningInstitutionIdAndLastUpdatedDateAfter(Integer owningInstitutionId, Date createdDate);

    /**
     * Gets pageable Bibliographic Entities by using last updated date from the given parameter.
     *
     * @param pageable    the pageable
     * @param createdDate the created date
     * @return the page
     */
    Page<BibliographicEntity> findByLastUpdatedDateAfter(Pageable pageable, Date createdDate);

    /**
     * Gets pageable Bibliographic Entities by using owning institution id and last updated date.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @param createdDate   the created date
     * @return the page
     */
    Page<BibliographicEntity> findByOwningInstitutionIdAndLastUpdatedDateAfter(Pageable pageable, Integer institutionId, Date createdDate);

    /**
     * Gets the count of bibs by using owning institution id and is deleted false.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer owningInstitutionId);

    /**
     * Find all bibliographic entites by using IsDeleted field which is false.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<BibliographicEntity> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * Find bibliographic entities by using owning institution id and IsDeleted field which has false.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @return the page
     */
    Page<BibliographicEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    /**
     * Find bibliographic entites by using owning institution id.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @return the page
     */
    Page<BibliographicEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);

    /**
     * Find the bibliographic entity by using owning institution id,owning institution bib id and is deleted field which has false value.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Integer owningInstitutionId, String owningInstitutionBibId);

    /**
     * Find the bibliographic entity by using bibliographic id .
     *
     * @param bibliographicId the bibliographic id
     * @return the bibliographic entity
     */
    BibliographicEntity findByBibliographicId(@Param("bibliographicId") Integer bibliographicId);

    /**
     * Find the list of bibliographic entities by using a list of bibliographic ids.
     *
     * @param bibliographicIds the bibliographic ids
     * @return the list
     */
    List<BibliographicEntity> findByBibliographicIdIn(List<Integer> bibliographicIds);

    /**
     * Find the bibliographic entity by using bibliographic id and is deleted field which has false value.
     *
     * @param bibliographicId the bibliographic id
     * @return the bibliographic entity
     */
    BibliographicEntity findByBibliographicIdAndIsDeletedFalse(@Param("bibliographicId") Integer bibliographicId);

    /**
     * Find the bibliographic entity by using owning institution id and owning institution bib id.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the bibliographic entity
     */
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibId(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * Find a list of bibliographic entities by using owning institution bib id.
     *
     * @param owningInstitutionBibId the owning institution bib id
     * @return the list
     */
    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    /**
     * Count the number of bibs by using owning institution code and is deleted field which has false value.
     *
     * @param institutionCode the institution code
     * @return the long
     */
    @Query(value = "select count(*) from bibliographic_t bib, institution_t inst where bib.OWNING_INST_ID = inst.INSTITUTION_ID AND inst.INSTITUTION_CODE=?1 limit 1", nativeQuery = true)
    Long countByOwningInstitutionCodeAndIsDeletedFalse(String institutionCode);

    /**
     * Gets a list of holding entities by using owning institution id and owning institution bib id.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the non deleted holdings entities
     */
    List<HoldingsEntity> getNonDeletedHoldingsEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * Gets a list of item entities by using owning institution id and owning institution bib id.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the non deleted item entities
     */
    List<ItemEntity> getNonDeletedItemEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * Gets the count of all non deleted items based on owning institution id and owning institution bib id.
     *
     * @param owningInstitutionId    the owning institution id
     * @param owningInstitutionBibId the owning institution bib id
     * @return the non deleted items count
     */
    @Query(value = "SELECT COUNT(*) FROM ITEM_T, BIBLIOGRAPHIC_ITEM_T WHERE BIBLIOGRAPHIC_ITEM_T.ITEM_INST_ID = ITEM_T.OWNING_INST_ID " +
            "AND BIBLIOGRAPHIC_ITEM_T.OWNING_INST_ITEM_ID = ITEM_T.OWNING_INST_ITEM_ID AND ITEM_T.IS_DELETED = 0 AND " +
            "BIBLIOGRAPHIC_ITEM_T.OWNING_INST_BIB_ID = :owningInstitutionBibId AND BIBLIOGRAPHIC_ITEM_T.BIB_INST_ID = :owningInstitutionId", nativeQuery = true)
    Long getNonDeletedItemsCount(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    /**
     * This query marks the bibs as deleted for the given list of bib ids.
     *
     * @param bibliographicIds the bibliographic ids
     * @param lastUpdatedBy    the last updated by
     * @param lastUpdatedDate  the last updated date
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE BibliographicEntity bib SET bib.isDeleted = true, bib.lastUpdatedBy = :lastUpdatedBy, bib.lastUpdatedDate = :lastUpdatedDate WHERE bib.bibliographicId IN :bibliographicIds")
    int markBibsAsDeleted(@Param("bibliographicIds") List<Integer> bibliographicIds, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);

    /**
     * Gets bibliographic entities for changed items based on the operation type and date.
     *
     * @param pageable      the pageable
     * @param operationType the operation type
     * @param from the from date
     * @param to the to date
     * @return the bibliographic entities for changed items
     */
    @Query(value = "SELECT distinct BIB FROM BibliographicEntity as BIB WHERE BIB.bibliographicId IN " +
            "(SELECT DISTINCT BIB1.bibliographicId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.itemEntities AS ITEMS " +
            "WHERE ITEMS.itemId IN (SELECT recordId FROM ItemChangeLogEntity where operationType=?1 and updated_date between ?2 and ?3))")
    Page<BibliographicEntity> getBibliographicEntitiesForChangedItems(Pageable pageable, String operationType, Date from, Date to);

    /**
     * Gets count of bibliographic entities for changed items based on the operation type and date.
     *
     * @param from the from date
     * @param to the to date
     * @param operationType the operation type
     * @return the count of bibliographic entities for changed items
     */
    @Query(value = "SELECT count(distinct BIB) FROM BibliographicEntity as BIB WHERE BIB.bibliographicId IN " +
            "(SELECT DISTINCT BIB1.bibliographicId FROM BibliographicEntity as BIB1 INNER JOIN BIB1.itemEntities AS ITEMS " +
            "WHERE ITEMS.itemId IN (SELECT recordId FROM ItemChangeLogEntity where operationType=?1 and updated_date between ?2 and ?3))")
    Long getCountOfBibliographicEntitiesForChangedItems(String operationType, Date from, Date to);

}