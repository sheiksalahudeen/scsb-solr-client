package org.recap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by pvsubrah on 6/11/16.
 */

@Entity
@Table(name = "item_t", schema = "recap", catalog = "")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEM_ID")
    private Integer itemId;

    @Column(name = "BAR_CODE")
    private String barcode;

    @Column(name = "CUSTOMER_CODE")
    private String customerCode;

    @Column(name = "HOLDINGS_ID", insertable=false, updatable=false)
    private Integer holdingsId;

    @Column(name = "CALL_NUMBER")
    private String callNumber;

    @Column(name = "CALL_NUMBER_TYPE")
    private String callNumberType;

    @Column(name = "ITEM_AVAIL_STATUS_ID", insertable=false, updatable=false)
    private Integer itemAvailabilityStatusId;

    @Column(name = "COPY_NUMBER")
    private Integer copyNumber;

    @Column(name = "OWNING_INST_ID", insertable=false, updatable=false)
    private Integer owningInstitutionId;

    @Column(name = "COLLECTION_GROUP_ID", insertable=false, updatable=false)
    private Integer collectionGroupId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;

    @Column(name = "BIBLIOGRAPHIC_ID")
    private Integer bibliographicId;

    @Column(name = "USE_RESTRICTIONS")
    private String useRestrictions;

    @Column(name = "VOLUME_PART_YEAR")
    private String volumePartYear;

    @Column(name = "OWNING_INST_ITEM_ID")
    private String owningInstitutionItemId;

    @Column(name = "NOTES_ID")
    private Integer notesId;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="HOLDINGS_ID")
    @JsonIgnore
    private HoldingsEntity holdingsEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_AVAIL_STATUS_ID")
    private ItemStatusEntity itemStatusEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "COLLECTION_GROUP_ID")
    private CollectionGroupEntity collectionGroupEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID")
    private InstitutionEntity institutionEntity;

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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Integer getHoldingsId() {
        return holdingsId;
    }

    public void setHoldingsId(Integer holdingsId) {
        this.holdingsId = holdingsId;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallNumberType() {
        return callNumberType;
    }

    public void setCallNumberType(String callNumberType) {
        this.callNumberType = callNumberType;
    }

    public Integer getItemAvailabilityStatusId() {
        return itemAvailabilityStatusId;
    }

    public void setItemAvailabilityStatusId(Integer itemAvailabilityStatusId) {
        this.itemAvailabilityStatusId = itemAvailabilityStatusId;
    }

    public Integer getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    public Integer getOwningInstitutionId() {
        return owningInstitutionId;
    }

    public void setOwningInstitutionId(Integer owningInstitutionId) {
        this.owningInstitutionId = owningInstitutionId;
    }

    public Integer getCollectionGroupId() {
        return collectionGroupId;
    }

    public void setCollectionGroupId(Integer collectionGroupId) {
        this.collectionGroupId = collectionGroupId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(Integer bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public String getUseRestrictions() {
        return useRestrictions;
    }

    public void setUseRestrictions(String useRestrictions) {
        this.useRestrictions = useRestrictions;
    }

    public String getVolumePartYear() {
        return volumePartYear;
    }

    public void setVolumePartYear(String volumePartYear) {
        this.volumePartYear = volumePartYear;
    }

    public String getOwningInstitutionItemId() {
        return owningInstitutionItemId;
    }

    public void setOwningInstitutionItemId(String owningInstitutionItemId) {
        this.owningInstitutionItemId = owningInstitutionItemId;
    }

    public Integer getNotesId() {
        return notesId;
    }

    public void setNotesId(Integer notesId) {
        this.notesId = notesId;
    }

    public HoldingsEntity getHoldingsEntity() {
        return holdingsEntity;
    }

    public void setHoldingsEntity(HoldingsEntity holdingsEntity) {
        this.holdingsEntity = holdingsEntity;
    }

    public ItemStatusEntity getItemStatusEntity() {
        return itemStatusEntity;
    }

    public void setItemStatusEntity(ItemStatusEntity itemStatusEntity) {
        this.itemStatusEntity = itemStatusEntity;
    }

    public CollectionGroupEntity getCollectionGroupEntity() {
        return collectionGroupEntity;
    }

    public void setCollectionGroupEntity(CollectionGroupEntity collectionGroupEntity) {
        this.collectionGroupEntity = collectionGroupEntity;
    }

    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }
}
