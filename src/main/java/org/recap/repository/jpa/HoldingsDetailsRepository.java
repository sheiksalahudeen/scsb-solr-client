package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hemalathas on 21/6/16.
 */
public interface HoldingsDetailsRepository extends JpaRepository<HoldingsEntity, String> {

    HoldingsEntity findByHoldingsId(Integer holdingsId);
    Long countByOwningInstitutionId(Integer institutionId);
    Page<HoldingsEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);
}
