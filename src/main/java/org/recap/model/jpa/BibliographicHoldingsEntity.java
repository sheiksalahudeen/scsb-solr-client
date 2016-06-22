package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by pvsubrah on 6/17/16.
 */

@Entity
@Table(name = "bibliographic_holdings_t", schema = "recap", catalog = "")
public class BibliographicHoldingsEntity implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BIBLIOGRAPHIC_HOLDINGS_ID")
    private Integer bibliographicHoldingsId;

    @Column(name = "BIBLIOGRAPHIC_ID")
    private Integer bibliographicId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BIBLIOGRAPHIC_ID", insertable = false, updatable = false)
    private BibliographicEntity bibliographicEntity;

    @Column(name = "HOLDINGS_ID")
    private Integer holdingsId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HOLDINGS_ID", insertable = false, updatable = false)
    private HoldingsEntity holdingsEntity;

    public Integer getBibliographicHoldingsId() {
        return bibliographicHoldingsId;
    }

    public void setBibliographicHoldingsId(Integer bibliographicHoldingsId) {
        this.bibliographicHoldingsId = bibliographicHoldingsId;
    }

    public Integer getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(Integer bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public Integer getHoldingsId() {
        return holdingsId;
    }

    public void setHoldingsId(Integer holdingsId) {
        this.holdingsId = holdingsId;
    }

    public BibliographicEntity getBibliographicEntity() {
        return bibliographicEntity;
    }

    public void setBibliographicEntity(BibliographicEntity bibliographicEntity) {
        this.bibliographicEntity = bibliographicEntity;
    }

    public HoldingsEntity getHoldingsEntity() {
        return holdingsEntity;
    }

    public void setHoldingsEntity(HoldingsEntity holdingsEntity) {
        this.holdingsEntity = holdingsEntity;
    }
}

