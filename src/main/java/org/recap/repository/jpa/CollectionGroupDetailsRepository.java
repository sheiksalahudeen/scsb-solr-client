package org.recap.repository.jpa;

import org.recap.model.jpa.CollectionGroupEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hemalathas on 22/6/16.
 */
public interface CollectionGroupDetailsRepository extends PagingAndSortingRepository<CollectionGroupEntity, Integer> {

    /**
     * Find collection group entity by using the given collection group code.
     *
     * @param collectionGroupCode the collection group code
     * @return the collection group entity
     */
    CollectionGroupEntity findByCollectionGroupCode(String collectionGroupCode);
}
