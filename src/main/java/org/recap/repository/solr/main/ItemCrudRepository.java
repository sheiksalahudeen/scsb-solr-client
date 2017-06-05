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

    /**
     * Finds a list of item based on the given barcode.
     *
     * @param barcode the barcode
     * @return the list
     */
    List<Item> findByBarcode(String barcode);

    /**
     * Finds item based on the given item id.
     *
     * @param itemId the item id
     * @return the item
     */
    Item findByItemId(Integer itemId);

    /**
     * Finds a list of items based on the given collection group designation and list of item ids.
     *
     * @param collectionGroupDesignation the collection group designation
     * @param itemIds                    the item ids
     * @return the list
     */
    List<Item> findByCollectionGroupDesignationAndItemIdIn(String collectionGroupDesignation, List<Integer> itemIds);

    /**
     * Counts item based on the given item id.
     *
     * @param itemId the item id
     * @return the long
     */
    Long countByItemId(Integer itemId);

    /**
     * Deletes item based on given item id.
     *
     * @param itemId the item id
     * @return the int
     */
    int deleteByItemId(@Param("itemId") Integer itemId);

    /**
     * Deletes item based on the given list of item id.
     *
     * @param itemIds the item ids
     * @return the int
     */
    int deleteByItemIdIn(@Param("itemIds") List<Integer> itemIds);
}
