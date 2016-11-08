package org.recap.repository.jpa;

import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.BibliographicPK;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */
public interface BibliographicDetailsRepository extends JpaRepository<BibliographicEntity, BibliographicPK> {

    Long countByIsDeletedFalse();

    Long countByOwningInstitutionId(Integer owningInstitutionId);

    Long countByLastUpdatedDateAfter(Date createdDate);

    Long countByOwningInstitutionIdAndLastUpdatedDateAfter(Integer owningInstitutionId, Date createdDate);

    Page<BibliographicEntity> findByLastUpdatedDateAfter(Pageable pageable, Date createdDate);

    Page<BibliographicEntity> findByOwningInstitutionIdAndLastUpdatedDateAfter(Pageable pageable, Integer institutionId, Date createdDate);

    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer owningInstitutionId);

    Page<BibliographicEntity> findAllByIsDeletedFalse(Pageable pageable);

    Page<BibliographicEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    Page<BibliographicEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);

    BibliographicEntity findByOwningInstitutionIdAndOwningInstitutionBibIdAndIsDeletedFalse(Integer owningInstitutionId, String owningInstitutionBibId);

    BibliographicEntity findByBibliographicId(Integer bibId);

    List<BibliographicEntity> findByOwningInstitutionBibId(String owningInstitutionBibId);

    @Query(value = "select count(*) from bibliographic_t bib, institution_t inst where bib.OWNING_INST_ID = inst.INSTITUTION_ID AND inst.INSTITUTION_CODE=?1 limit 1", nativeQuery = true)
    Long countByOwningInstitutionCodeAndIsDeletedFalse(String institutionCode);

    List<HoldingsEntity> getNonDeletedHoldingsEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

    List<ItemEntity> getNonDeletedItemEntities(@Param("owningInstitutionId") Integer owningInstitutionId, @Param("owningInstitutionBibId") String owningInstitutionBibId);

}
