package org.recap.repository.jpa;

import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 21/6/16.
 */
@RepositoryRestResource(collectionResourceRel = "item", path = "item")
public interface ItemDetailsRepository extends JpaRepository<ItemEntity, ItemPK> {

    /**
     * Find all item entities by using isDeleted field which is false.
     *
     * @param pageable the pageable
     * @return the page
     */
    Page<ItemEntity> findAllByIsDeletedFalse(Pageable pageable);

    /**
     * Finds item entity by using item id.
     *
     * @param itemId the item id
     * @return the item entity
     */
    ItemEntity findByItemId(Integer itemId);

    /**
     * Counts the number of items by using owning institution id and isDeleted field which is false.
     *
     * @param institutionId the institution id
     * @return the long
     */
    Long countByOwningInstitutionIdAndIsDeletedFalse(Integer institutionId);

    /**
     * Finds item entities by using owning institution id and isDeleted field which is false.
     *
     * @param pageable      the pageable
     * @param institutionId the institution id
     * @return the page
     */
    Page<ItemEntity> findByOwningInstitutionIdAndIsDeletedFalse(Pageable pageable, Integer institutionId);

    /**
     * Finds a list of item entities based on the given owning institution id.
     *
     * @param owningInstitutionId the owning institution id
     * @return the list
     */
    List<ItemEntity> findByOwningInstitutionId(Integer owningInstitutionId);

    /**
     * Finds the item entity by using owning institution item id .
     *
     * @param owningInstitutionItemId the owning institution item id
     * @return the item entity
     */
    ItemEntity findByOwningInstitutionItemId(@Param("owningInstitutionItemId") String owningInstitutionItemId);

    /**
     * Finds the item entity by using owning institution item id and owning institution id.
     *
     * @param owningInstitutionItemId the owning institution item id
     * @param owningInstitutionId     the owning institution id
     * @return the item entity
     */
    ItemEntity findByOwningInstitutionItemIdAndOwningInstitutionId(String owningInstitutionItemId, Integer owningInstitutionId);

    /**
     * Finds a list of item entities based on the given barcode.
     *
     * @param barcode the barcode
     * @return the list
     */
    List<ItemEntity> findByBarcode(String barcode);

    /**
     * Finds a list of item entities based on the given barcode and list of customer codes.
     *
     * @param barcode      the barcode
     * @param customerCode the customer code
     * @return the list
     */
    List<ItemEntity> findByBarcodeAndCustomerCode(String barcode, String customerCode);

    /**
     * Update the item's collection group based on the given parameters.
     *
     * @param collectionGroupId the collection group id
     * @param itemBarcode       the item barcode
     * @param lastUpdatedBy     the last updated by
     * @param lastUpdatedDate   the last updated date
     * @return the int
     */
    @Modifying
    @Transactional
    @Query("update ItemEntity item set item.collectionGroupId = :collectionGroupId, item.lastUpdatedBy = :lastUpdatedBy, item.lastUpdatedDate = :lastUpdatedDate where item.barcode = :itemBarcode")
    int updateCollectionGroupIdByItemBarcode(@Param("collectionGroupId") Integer collectionGroupId, @Param("itemBarcode") String itemBarcode, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);

    /**
     * Finds a list of item entities by using a list of barcodes.
     *
     * @param barcodes the barcodes
     * @return the list
     */
    List<ItemEntity> findByBarcodeIn(@Param("barcodes") List<String> barcodes);

    /**
     * This query marks the item as deleted based on the item id,last updated by and last updated date values.
     *
     * @param itemId          the item id
     * @param lastUpdatedBy   the last updated by
     * @param lastUpdatedDate the last updated date
     * @return the int
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ItemEntity item SET item.isDeleted = true, item.lastUpdatedBy = :lastUpdatedBy, item.lastUpdatedDate = :lastUpdatedDate WHERE item.itemId = :itemId")
    int markItemAsDeleted(@Param("itemId") Integer itemId, @Param("lastUpdatedBy") String lastUpdatedBy, @Param("lastUpdatedDate") Date lastUpdatedDate);

    /**
     * Gets item status by using barcode and isDeleted field value false.
     *
     * @param barcode the barcode
     * @return the item status by barcode and is deleted false
     */
    @Query(value = "select itemStatus.statusCode from ItemEntity item, ItemStatusEntity itemStatus where item.itemAvailabilityStatusId = itemStatus.itemStatusId and item.barcode = :barcode and item.isDeleted = 0")
    String getItemStatusByBarcodeAndIsDeletedFalse(@Param("barcode") String barcode);

    /**
     * Gets a list of item entities with item availability status which contains available and not available and isDeleted field which is false by using a list of barcode.
     *
     * @param barcodeList the barcode list
     * @return the item status by barcode and is deleted false list
     */
    @Query(value = "select item from ItemEntity item where item.itemAvailabilityStatusId in (1,2) and item.barcode in (:barcode) and item.isDeleted = 0")
    List<ItemEntity> getItemStatusByBarcodeAndIsDeletedFalseList(@Param("barcode") List<String> barcodeList);

    /**
     * Finds item entity  based on the last updated date and last update date time.
     *
     * @param pageable           the pageable
     * @param lastUpdatedDate    the last updated date
     * @param lastUpdateDateTime the last update date time
     * @return the page
     */
    @Query(value = "select item from ItemEntity item where item.lastUpdatedDate between ?1 and ?2")
    Page<ItemEntity> findByLastUpdatedDate(Pageable pageable, Date lastUpdatedDate, Date lastUpdateDateTime);

    /**
     * Finds a list of item entities by using a list of item ids.
     *
     * @param itemIds the item ids
     * @return the list
     */
    List<ItemEntity> findByItemIdIn(List<Integer> itemIds);
}