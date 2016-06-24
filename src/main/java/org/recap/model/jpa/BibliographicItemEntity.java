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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "BIB_INST_ID", referencedColumnName = "OWNING_INST_ID"),
            @JoinColumn(name = "OWNING_INST_BIB_ID", referencedColumnName = "OWNING_INST_BIB_ID")})
    private BibliographicEntity bibliographicEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumns({@JoinColumn(name = "ITEM_INST_ID", referencedColumnName = "OWNING_INST_ID"),
            @JoinColumn(name = "OWNING_INST_ITEM_ID", referencedColumnName= "OWNING_INST_ITEM_ID")})
    private ItemEntity itemEntity;

    public Integer getBibliographicItemId() {
        return bibliographicItemId;
    }

    public void setBibliographicItemId(Integer bibliographicItemId) {
        this.bibliographicItemId = bibliographicItemId;
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
