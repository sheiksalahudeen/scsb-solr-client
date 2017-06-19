package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.HoldingsPK;
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
 * Created by hemalathas on 21/6/16.
 */
@RepositoryRestResource(collectionResourceRel = "holdings", path = "holdings")
public interface HoldingsDetailsRepository extends JpaRepository<HoldingsEntity, HoldingsPK> {

    /**
     * Count the number of holdings by using isDeleted field which is false.
     *
     * @return the long
     */
    Long countByIsDeletedFalse();

    /**
     * Finds holding entity by using holdings id.
     *
     * @param holdingsId the holdings id
     * @return the holdings entity
     */
    HoldingsEntity findByHoldingsId(Integer holdingsId);

    /**
     * Find all holdings entities by using isDeleted field which is false.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<HoldingsEntity> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * Count the number of holdings by using owning institution id and is deleted field which is false.
     *
     * @param owningInstitutionId the owning institution id
     * @return the long
     */
    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer owningInstitutionId);

    /**
     * Finds hodldings entities by using owning institution id and isDeleted field which is false.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @return the page
     */
    Page<HoldingsEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    HoldingsEntity findByOwningInstitutionHoldingsIdAndOwningInstitutionId(String owningInstitutionHoldingsId, Integer owningInstitutionId);

    /**
     * Gets non deleted item entities based on the owning institution id and owning institution holdings id.
     *
     * @param owningInstitutionId         the owning institution id
     * @param owningInstitutionHoldingsId the owning institution holdings id
     * @return the non deleted item entities
     */
    List<ItemEntity> getNonDeletedItemEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionHoldingsId") String owningInstitutionHoldingsId);

    /**
     * Gets the count of non deleted items based on the owning institution id and owning institution holdings id.
     *
     * @param owningInstitutionId         the owning institution id
     * @param owningInstitutionHoldingsId the owning institution holdings id
     * @return the non deleted items count
     */
    @Query(value = "SELECT COUNT(*) FROM ITEM_T, ITEM_HOLDINGS_T WHERE ITEM_HOLDINGS_T.ITEM_INST_ID = ITEM_T.OWNING_INST_ID AND " +
            "ITEM_HOLDINGS_T.OWNING_INST_ITEM_ID = ITEM_T.OWNING_INST_ITEM_ID AND ITEM_T.IS_DELETED = 0 AND " +
            "ITEM_HOLDINGS_T.OWNING_INST_HOLDINGS_ID = :owningInstitutionHoldingsId AND ITEM_HOLDINGS_T.HOLDINGS_INST_ID = :owningInstitutionId", nativeQuery = true)
    Long getNonDeletedItemsCount(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionHoldingsId") String owningInstitutionHoldingsId);

    /**
     * This query marks holdings as deleted for the given list of holding ids.
     *
     * @param holdingIds      the holding ids
     * @param lastUpdatedBy   the last updated by
     * @param lastUpdatedDate the last updated date
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE HoldingsEntity holdings SET holdings.isDeleted = true, holdings.lastUpdatedBy = :lastUpdatedBy, holdings.lastUpdatedDate = :lastUpdatedDate WHERE holdings.holdingsId IN :holdingIds")
    int markHoldingsAsDeleted(@Param("holdingIds") List<Integer> holdingIds, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);
}
