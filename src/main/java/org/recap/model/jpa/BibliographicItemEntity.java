package org.recap.model.jpa;

import javax.persistence.*;

/**
 * Created by pvsubrah on 6/10/16.
 */
@Entity
@Table(name = "bibliographic_item_t", schema = "recap", catalog = "")
public class BibliographicItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BIBLIOGRAPHIC_ITEM_ID")
    private Integer bibliographicItemId;

    @Column(name = "BIBLIOGRAPHIC_ID")
    private Integer bibliographicId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BIBLIOGRAPHIC_ID", insertable = false, updatable = false)
    private BibliographicEntity bibliographicEntity;

    @Column(name = "ITEM_ID")
    private Integer itemId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ITEM_ID", insertable = false, updatable = false)
    private ItemEntity itemEntity;

    public Integer getBibliographicItemId() {
        return bibliographicItemId;
    }

    public void setBibliographicItemId(Integer bibliographicItemId) {
        this.bibliographicItemId = bibliographicItemId;
    }

    public Integer getBibliographicId() {
        return bibliographicId;
    }

    public void setBibliographicId(Integer bibliographicId) {
        this.bibliographicId = bibliographicId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BibliographicEntity getBibliographicEntity() {
        return bibliographicEntity;
    }

    public void setBibliographicEntity(BibliographicEntity bibliographicEntity) {
        this.bibliographicEntity = bibliographicEntity;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }
}
