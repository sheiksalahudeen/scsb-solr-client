package org.recap.repository.jpa;

import org.recap.model.jpa.InstitutionEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface InstitutionDetailsRepository extends PagingAndSortingRepository<InstitutionEntity,Integer> {
    /**
     * Finds institution entity  by using institution id.
     *
     * @param institutionId the institution id
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionId(Integer institutionId);

    /**
     * Finds institution entity by using institution code.
     *
     * @param institutionCode the institution code
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionCode(String institutionCode);

    /**
     * Finds institution entity by using institution name.
     *
     * @param institutionName the institution name
     * @return the institution entity
     */
    InstitutionEntity findByInstitutionName(String institutionName);

    /**
     * Finds institution entity which is not in the given list of institution code.
     *
     * @param institutionCodes list of institution codes
     * @return the list of institution entities
     */
    List<InstitutionEntity> findByInstitutionCodeNotIn(List<String> institutionCodes);
}
