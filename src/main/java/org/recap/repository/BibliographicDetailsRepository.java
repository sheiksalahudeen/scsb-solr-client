package org.recap.repository;

import org.recap.model.BibliographicEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends PagingAndSortingRepository<BibliographicEntity, Integer> {}
