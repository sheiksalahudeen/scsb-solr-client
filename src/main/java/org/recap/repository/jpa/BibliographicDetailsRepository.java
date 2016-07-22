package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends JpaRepository<BibliographicEntity, Integer> {
    Long countByOwningInstitutionId(Integer institutionId);
    Page<BibliographicEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibId(Integer owningInstitutionId, String owningInstitutionBibId);
    BibliographicEntity findByBibliographicId(Integer bibId);
}
