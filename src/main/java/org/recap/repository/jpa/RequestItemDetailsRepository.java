package org.recap.repository.jpa;

import org.recap.model.jpa.RequestItemEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chenchulakshmig on 6/13/16.
 */
@RepositoryRestResource(collectionResourceRel = "requestItem", path = "requestItem")
public interface RequestItemDetailsRepository extends PagingAndSortingRepository<RequestItemEntity, Integer> {

    List<RequestItemEntity> findByItemIdIn(@Param("itemIds") List<Integer> itemIds);

    @Transactional
    int deleteByItemIdIn(@Param("itemIds") List<Integer> itemIds);
}
