package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hemalathas on 21/6/16.
 */
public interface HoldingsDetailsRepository extends JpaRepository<HoldingsEntity, String> {

    HoldingsEntity findByHoldingsId(Integer holdingsId);

}
