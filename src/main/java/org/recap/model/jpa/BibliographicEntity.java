package org.recap.model.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * Created by pvsubrah on 6/10/16.
 */

@Entity
@Table(name = "bibliographic_t", schema = "recap", catalog = "")
public class BibliographicEntity implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BIBLIOGRAPHIC_ID")
    private Integer bibliographicId;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "OWNING_INST_ID")
    private Integer owningInstitutionId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED_DATE")
    private Date lastUpdatedDate;

    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstitutionBibId;

    @OneToMany(mappedBy="bibliographicEntity")
    private List<BibliographicHoldingsEntity> bibliographicHoldingsEntities;

    @OneToMany(mappedBy="bibliographicEntity")
    private List<BibliographicItemEntity> bibliographicItemEntities;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNING_INST_ID", insertable=false, updatable=false)
    private InstitutionEntity institutionEntity;

    public Integer getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(Integer bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
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

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public List<BibliographicHoldingsEntity> getBibliographicHoldingsEntities() {
        return bibliographicHoldingsEntities;
    }

    public void setBibliographicHoldingsEntities(List<BibliographicHoldingsEntity> bibliographicHoldingsEntities) {
        this.bibliographicHoldingsEntities = bibliographicHoldingsEntities;
    }

    public List<BibliographicItemEntity> getBibliographicItemEntities() {
        return bibliographicItemEntities;
    }

    public void setBibliographicItemEntities(List<BibliographicItemEntity> bibliographicItemEntities) {
        this.bibliographicItemEntities = bibliographicItemEntities;
    }

    public InstitutionEntity getInstitutionEntity() {
        return institutionEntity;
    }

    public void setInstitutionEntity(InstitutionEntity institutionEntity) {
        this.institutionEntity = institutionEntity;
    }
}
