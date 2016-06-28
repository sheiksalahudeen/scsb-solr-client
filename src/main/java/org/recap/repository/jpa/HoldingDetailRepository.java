package org.recap.repository.jpa;

import org.recap.model.jpa.HoldingsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hemalathas on 21/6/16.
 */
public interface HoldingDetailRepository extends PagingAndSortingRepository<HoldingsEntity,Integer> {

}
