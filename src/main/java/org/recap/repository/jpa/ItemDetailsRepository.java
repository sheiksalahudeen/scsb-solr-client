package org.recap.repository.jpa;

import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public interface ItemDetailsRepository extends PagingAndSortingRepository<ItemEntity, Integer> {
    public Long countByOwningInstitutionId(Integer institutionId);
    public List<ItemEntity> findByOwningInstitutionId(Pageable pageable, Integer institutionId);
}
