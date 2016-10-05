package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by hemalathas on 21/6/16.
 */
public interface HoldingsDetailsRepository extends JpaRepository<HoldingsEntity, String> {

    Long countByIsDeletedFalse();

    HoldingsEntity findByHoldingsId(Integer holdingsId);

    Page<HoldingsEntity> findAllByIsDeletedFalse(Pageable pageable);

    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer owningInstitutionId);

    Page<HoldingsEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    List<ItemEntity> getNonDeletedItemEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionHoldingsId") String owningInstitutionHoldingsId);
}
