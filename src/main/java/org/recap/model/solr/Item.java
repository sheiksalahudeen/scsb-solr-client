package org.recap.model.solr;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

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

    @Field("Availability")
    private String availability;

    @Field("CollectionGroupDesignation")
    private String collectionGroupDesignation;

    @Field("DocType")
    private String docType;

    @Field("CustomerCode")
    private String customerCode;

    @Field("UseRestriction")
    private String useRestriction;

    @Field("VolumePartYear")
    private String volumePartYear;

    @Field("CallNumber")
    private String callNumber;

    @Field("ItemOwningInstitution")
    private String owningInstitution;

    @Field("ItemBibId")
    private List<Integer> itemBibIdList;

    @Field("HoldingsId")
    private List<Integer> holdingsIdList;

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

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
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

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}
