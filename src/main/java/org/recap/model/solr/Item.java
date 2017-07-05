package org.recap.model.solr;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * Created by angelind on 15/6/16.
 */
public class Item {

    @Id
    @Field
    private String id;

    @Field("ItemId")
    private Integer itemId;

    @Field("OwningInstitutionItemId")
    private String owningInstitutionItemId;

    @Field("Barcode")
    private String barcode;

    @Field("Availability_search")
    private String availability;

    @Field("CollectionGroupDesignation")
    private String collectionGroupDesignation;

    @Field("DocType")
    private String docType;

    @Field("CustomerCode")
    private String customerCode;

    @Field("UseRestriction_search")
    private String useRestriction;

    @Field("VolumePartYear")
    private String volumePartYear;

    @Field("CallNumber_search")
    private String callNumberSearch;

    @Field("CallNumber_display")
    private String callNumberDisplay;

    @Field("ItemOwningInstitution")
    private String owningInstitution;

    @Field("ItemBibId")
    private List<Integer> itemBibIdList;

    @Field("HoldingsId")
    private List<Integer> holdingsIdList;

    @Field("Availability_display")
    private String availabilityDisplay;

    @Field("UseRestriction_display")
    private String useRestrictionDisplay;

    @Field("CopyNumber")
    private String copyNumber;

    @Field("ItemCreatedBy")
    private String itemCreatedBy;

    @Field("ItemCreatedDate")
    private Date itemCreatedDate;

    @Field("ItemLastUpdatedBy")
    private String itemLastUpdatedBy;

    @Field("ItemLastUpdatedDate")
    private Date itemLastUpdatedDate;

    @Field("IsDeletedItem")
    private boolean isDeletedItem = false;

    @Field("Title_sort")
    private String titleSort;

    @Field("ItemCatalogingStatus")
    private String itemCatalogingStatus;

    @Field("CGDChangeLog")
    private String cgdChangeLog;

    @Ignore
    private String root;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets item id.
     *
     * @return the item id
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * Sets item id.
     *
     * @param itemId the item id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets owning institution item id.
     *
     * @return the owning institution item id
     */
    public String getOwningInstitutionItemId() {
        return owningInstitutionItemId;
    }

    /**
     * Sets owning institution item id.
     *
     * @param owningInstitutionItemId the owning institution item id
     */
    public void setOwningInstitutionItemId(String owningInstitutionItemId) {
        this.owningInstitutionItemId = owningInstitutionItemId;
    }

    /**
     * Gets barcode.
     *
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets barcode.
     *
     * @param barcode the barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * Gets availability.
     *
     * @return the availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Sets availability.
     *
     * @param availability the availability
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Gets collection group designation.
     *
     * @return the collection group designation
     */
    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    /**
     * Sets collection group designation.
     *
     * @param collectionGroupDesignation the collection group designation
     */
    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    /**
     * Gets doc type.
     *
     * @return the doc type
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets doc type.
     *
     * @param docType the doc type
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets customer code.
     *
     * @return the customer code
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets customer code.
     *
     * @param customerCode the customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * Gets use restriction.
     *
     * @return the use restriction
     */
    public String getUseRestriction() {
        return useRestriction;
    }

    /**
     * Sets use restriction.
     *
     * @param useRestriction the use restriction
     */
    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    /**
     * Gets volume part year.
     *
     * @return the volume part year
     */
    public String getVolumePartYear() {
        return volumePartYear;
    }

    /**
     * Sets volume part year.
     *
     * @param volumePartYear the volume part year
     */
    public void setVolumePartYear(String volumePartYear) {
        this.volumePartYear = volumePartYear;
    }

    /**
     * Gets call number search.
     *
     * @return the call number search
     */
    public String getCallNumberSearch() {
        return callNumberSearch;
    }

    /**
     * Sets call number search.
     *
     * @param callNumberSearch the call number search
     */
    public void setCallNumberSearch(String callNumberSearch) {
        this.callNumberSearch = callNumberSearch;
    }

    /**
     * Gets call number display.
     *
     * @return the call number display
     */
    public String getCallNumberDisplay() {
        return callNumberDisplay;
    }

    /**
     * Sets call number display.
     *
     * @param callNumberDisplay the call number display
     */
    public void setCallNumberDisplay(String callNumberDisplay) {
        this.callNumberDisplay = callNumberDisplay;
    }

    /**
     * Gets owning institution.
     *
     * @return the owning institution
     */
    public String getOwningInstitution() {
        return owningInstitution;
    }

    /**
     * Sets owning institution.
     *
     * @param owningInstitution the owning institution
     */
    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    /**
     * Gets holdings id list.
     *
     * @return the holdings id list
     */
    public List<Integer> getHoldingsIdList() {
        return holdingsIdList;
    }

    /**
     * Sets holdings id list.
     *
     * @param holdingsIdList the holdings id list
     */
    public void setHoldingsIdList(List<Integer> holdingsIdList) {
        this.holdingsIdList = holdingsIdList;
    }

    /**
     * Gets item bib id list.
     *
     * @return the item bib id list
     */
    public List<Integer> getItemBibIdList() {
        return itemBibIdList;
    }

    /**
     * Sets item bib id list.
     *
     * @param itemBibIdList the item bib id list
     */
    public void setItemBibIdList(List<Integer> itemBibIdList) {
        this.itemBibIdList = itemBibIdList;
    }

    /**
     * Gets availability display.
     *
     * @return the availability display
     */
    public String getAvailabilityDisplay() {
        return availabilityDisplay;
    }

    /**
     * Sets availability display.
     *
     * @param availabilityDisplay the availability display
     */
    public void setAvailabilityDisplay(String availabilityDisplay) {
        this.availabilityDisplay = availabilityDisplay;
    }

    /**
     * Gets use restriction display.
     *
     * @return the use restriction display
     */
    public String getUseRestrictionDisplay() {
        return useRestrictionDisplay;
    }

    /**
     * Sets use restriction display.
     *
     * @param useRestrictionDisplay the use restriction display
     */
    public void setUseRestrictionDisplay(String useRestrictionDisplay) {
        this.useRestrictionDisplay = useRestrictionDisplay;
    }

    /**
     * Gets root.
     *
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * Sets root.
     *
     * @param root the root
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * Gets copy number.
     *
     * @return the copy number
     */
    public String getCopyNumber() {
        return copyNumber;
    }

    /**
     * Sets copy number.
     *
     * @param copyNumber the copy number
     */
    public void setCopyNumber(String copyNumber) {
        this.copyNumber = copyNumber;
    }

    /**
     * Gets item created by.
     *
     * @return the item created by
     */
    public String getItemCreatedBy() {
        return itemCreatedBy;
    }

    /**
     * Sets item created by.
     *
     * @param itemCreatedBy the item created by
     */
    public void setItemCreatedBy(String itemCreatedBy) {
        this.itemCreatedBy = itemCreatedBy;
    }

    /**
     * Gets item created date.
     *
     * @return the item created date
     */
    public Date getItemCreatedDate() {
        return itemCreatedDate;
    }

    /**
     * Sets item created date.
     *
     * @param itemCreatedDate the item created date
     */
    public void setItemCreatedDate(Date itemCreatedDate) {
        this.itemCreatedDate = itemCreatedDate;
    }

    /**
     * Gets item last updated by.
     *
     * @return the item last updated by
     */
    public String getItemLastUpdatedBy() {
        return itemLastUpdatedBy;
    }

    /**
     * Sets item last updated by.
     *
     * @param itemLastUpdatedBy the item last updated by
     */
    public void setItemLastUpdatedBy(String itemLastUpdatedBy) {
        this.itemLastUpdatedBy = itemLastUpdatedBy;
    }

    /**
     * Gets item last updated date.
     *
     * @return the item last updated date
     */
    public Date getItemLastUpdatedDate() {
        return itemLastUpdatedDate;
    }

    /**
     * Sets item last updated date.
     *
     * @param itemLastUpdatedDate the item last updated date
     */
    public void setItemLastUpdatedDate(Date itemLastUpdatedDate) {
        this.itemLastUpdatedDate = itemLastUpdatedDate;
    }

    /**
     * Is deleted item boolean.
     *
     * @return the boolean
     */
    public boolean isDeletedItem() {
        return isDeletedItem;
    }

    /**
     * Sets deleted item.
     *
     * @param deletedItem the deleted item
     */
    public void setDeletedItem(boolean deletedItem) {
        isDeletedItem = deletedItem;
    }

    /**
     * Gets title sort.
     *
     * @return the title sort
     */
    public String getTitleSort() {
        return titleSort;
    }

    /**
     * Sets title sort.
     *
     * @param titleSort the title sort
     */
    public void setTitleSort(String titleSort) {
        this.titleSort = titleSort;
    }

    /**
     * Gets item cataloging status.
     *
     * @return the item cataloging status
     */
    public String getItemCatalogingStatus() {
        return itemCatalogingStatus;
    }

    /**
     * Sets item cataloging status.
     *
     * @param itemCatalogingStatus the item cataloging status
     */
    public void setItemCatalogingStatus(String itemCatalogingStatus) {
        this.itemCatalogingStatus = itemCatalogingStatus;
    }

    /**
     * Gets cgd change log.
     *
     * @return the cgd change log
     */
    public String getCgdChangeLog() {
        return cgdChangeLog;
    }

    /**
     * Sets cgd change log.
     *
     * @param cgdChangeLog the cgd change log
     */
    public void setCgdChangeLog(String cgdChangeLog) {
        this.cgdChangeLog = cgdChangeLog;
    }
}
