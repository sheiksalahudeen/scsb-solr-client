package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends JpaRepository<BibliographicEntity, BibliographicPK> {
    Long countByOwningInstitutionId(Integer institutionId);
    Page<BibliographicEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);
    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibId(Integer owningInstitutionId, String owningInstitutionBibId);
    BibliographicEntity findByBibliographicId(Integer bibId);
    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    @Query(value = "select count(*) from bibliographic_t bib, institution_t inst where bib.OWNING_INST_ID = inst.INSTITUTION_ID AND inst.INSTITUTION_CODE=?1 limit 1",  nativeQuery = true)
    Long countByOwningInstitutionCode(String institutionCode);
}
