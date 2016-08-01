package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/10/16.
 */

@Entity
@Table(name = "bibliographic_t", schema = "recap", catalog = "")
@IdClass(BibliographicPK.class)
public class BibliographicEntity implements Serializable {
    @Column(name = "BIBLIOGRAPHIC_ID", insertable = false, updatable = false)
    private Integer bibliographicId;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;

    @Id
    @Column(name = "OWNING_INST_ID")
    private Integer owningInstitutionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;

    @Column(name = "LAST_UPDATED_BY")
    private String lastUpdatedBy;

    @Id
    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstitutionBibId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable = false, updatable = false)
    private InstitutionEntity institutionEntity;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_holdings_t", joinColumns = {
            @JoinColumn(name = "OWNING_INST_BIB_ID", referencedColumnName = "OWNING_INST_BIB_ID"),
            @JoinColumn(name = "OWNING_INST_ID", referencedColumnName = "OWNING_INST_ID")},
            inverseJoinColumns = @JoinColumn(name = "OWNING_INST_HOLDINGS_ID", referencedColumnName = "OWNING_INST_HOLDINGS_ID"))
    private List<HoldingsEntity> holdingsEntities;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "bibliographic_item_t", joinColumns = {
            @JoinColumn(name = "OWNING_INST_BIB_ID", referencedColumnName = "OWNING_INST_BIB_ID"),
            @JoinColumn(name = "BIB_INST_ID", referencedColumnName = "OWNING_INST_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "OWNING_INST_ITEM_ID", referencedColumnName = "OWNING_INST_ITEM_ID"),
                    @JoinColumn(name = "ITEM_INST_ID", referencedColumnName = "OWNING_INST_ID")})
    private List<ItemEntity> itemEntities;

    public BibliographicEntity() {
    }

    public Integer getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(Integer bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Integer getOwningInstitutionId() {
        return owningInstitutionId;
    }

    public void setOwningInstitutionId(Integer owningInstitutionId) {
        this.owningInstitutionId = owningInstitutionId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }

    public List<HoldingsEntity> getHoldingsEntities() {
        return holdingsEntities;
    }

    public void setHoldingsEntities(List<HoldingsEntity> holdingsEntities) {
        this.holdingsEntities = holdingsEntities;
    }

    public List<ItemEntity> getItemEntities() {
        return itemEntities;
    }

    public void setItemEntities(List<ItemEntity> itemEntities) {
        this.itemEntities = itemEntities;
    }


}

