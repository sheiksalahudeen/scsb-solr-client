package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends PagingAndSortingRepository<BibliographicEntity, Integer> {}
