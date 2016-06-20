package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends PagingAndSortingRepository<BibliographicEntity, Integer> {
    public List<BibliographicEntity> findByOwningInstitutionId(Integer institutionId);
}
