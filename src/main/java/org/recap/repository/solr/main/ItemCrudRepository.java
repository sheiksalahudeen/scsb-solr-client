package org.recap.repository.solr.main;

import org.recap.model.solr.Item;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Created by angelind on 15/6/16.
 */
@RepositoryRestResource(collectionResourceRel = "itemSolr", path = "itemSolr")
public interface ItemCrudRepository extends SolrCrudRepository<Item, String> {

    List<Item> findByBarcode(String barcode);

    Item findByItemId(Integer itemId);

    List<Item> findByCollectionGroupDesignationAndItemIdIn(String collectionGroupDesignation, List<Integer> itemIds);

    Long countByItemId(Integer itemId);

    int deleteByItemId(@Param("itemId") Integer itemId);

    int deleteByItemIdIn(@Param("itemIds") List<Integer> itemIds);
}
