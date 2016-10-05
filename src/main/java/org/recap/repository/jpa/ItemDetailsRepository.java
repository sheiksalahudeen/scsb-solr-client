package org.recap.repository.jpa;

import org.recap.model.jpa.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
public interface ItemDetailsRepository extends PagingAndSortingRepository<ItemEntity, Integer> {

    Long countByIsDeletedFalse();

    Page<ItemEntity> findAllByIsDeletedFalse(Pageable pageable);

    ItemEntity findByItemId(Integer itemId);

    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer institutionId);

    Page<ItemEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    List<ItemEntity> findByOwningInstitutionId(Integer owningInstitutionId);

    ItemEntity findByOwningInstitutionItemId(String owningInstitutionItemId);
}
