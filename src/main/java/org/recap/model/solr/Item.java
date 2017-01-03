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

    @Ignore
    private String root;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getUseRestriction() {
        return useRestriction;
    }

    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    public String getVolumePartYear() {
        return volumePartYear;
    }

    public void setVolumePartYear(String volumePartYear) {
        this.volumePartYear = volumePartYear;
    }

    public String getCallNumberSearch() {
        return callNumberSearch;
    }

    public void setCallNumberSearch(String callNumberSearch) {
        this.callNumberSearch = callNumberSearch;
    }

    public String getCallNumberDisplay() {
        return callNumberDisplay;
    }

    public void setCallNumberDisplay(String callNumberDisplay) {
        this.callNumberDisplay = callNumberDisplay;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public List<Integer> getHoldingsIdList() {
        return holdingsIdList;
    }

    public void setHoldingsIdList(List<Integer> holdingsIdList) {
        this.holdingsIdList = holdingsIdList;
    }

    public List<Integer> getItemBibIdList() {
        return itemBibIdList;
    }

    public void setItemBibIdList(List<Integer> itemBibIdList) {
        this.itemBibIdList = itemBibIdList;
    }

    public String getAvailabilityDisplay() {
        return availabilityDisplay;
    }

    public void setAvailabilityDisplay(String availabilityDisplay) {
        this.availabilityDisplay = availabilityDisplay;
    }

    public String getUseRestrictionDisplay() {
        return useRestrictionDisplay;
    }

    public void setUseRestrictionDisplay(String useRestrictionDisplay) {
        this.useRestrictionDisplay = useRestrictionDisplay;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(String copyNumber) {
        this.copyNumber = copyNumber;
    }

    public String getItemCreatedBy() {
        return itemCreatedBy;
    }

    public void setItemCreatedBy(String itemCreatedBy) {
        this.itemCreatedBy = itemCreatedBy;
    }

    public Date getItemCreatedDate() {
        return itemCreatedDate;
    }

    public void setItemCreatedDate(Date itemCreatedDate) {
        this.itemCreatedDate = itemCreatedDate;
    }

    public String getItemLastUpdatedBy() {
        return itemLastUpdatedBy;
    }

    public void setItemLastUpdatedBy(String itemLastUpdatedBy) {
        this.itemLastUpdatedBy = itemLastUpdatedBy;
    }

    public Date getItemLastUpdatedDate() {
        return itemLastUpdatedDate;
    }

    public void setItemLastUpdatedDate(Date itemLastUpdatedDate) {
        this.itemLastUpdatedDate = itemLastUpdatedDate;
    }

    public boolean isDeletedItem() {
        return isDeletedItem;
    }

    public void setDeletedItem(boolean deletedItem) {
        isDeletedItem = deletedItem;
    }

    public String getTitleSort() {
        return titleSort;
    }

    public void setTitleSort(String titleSort) {
        this.titleSort = titleSort;
    }

    public String getItemCatalogingStatus() {
        return itemCatalogingStatus;
    }

    public void setItemCatalogingStatus(String itemCatalogingStatus) {
        this.itemCatalogingStatus = itemCatalogingStatus;
    }
}
