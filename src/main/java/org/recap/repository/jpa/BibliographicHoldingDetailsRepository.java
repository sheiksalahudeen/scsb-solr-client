package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicHoldingsEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hemalathas on 21/6/16.
 */
public interface BibliographicHoldingDetailsRepository extends PagingAndSortingRepository<BibliographicHoldingsEntity, Integer> {
}
