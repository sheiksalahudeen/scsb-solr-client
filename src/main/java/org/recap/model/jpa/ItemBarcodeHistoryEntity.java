package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sheiks on 07/07/17.
 */
@Entity
@Table(name = "item_barcode_history_t", schema = "recap", catalog = "")
public class ItemBarcodeHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HISTORY_ID")
    private Integer historyId;

    @Column(name = "OWNING_INST")
    private String owningingInstitution;

    @Column(name = "OWNING_INST_ITEM_ID")
    private String owningingInstitutionItemId;

    @Column(name = "OLD_BARCODE")
    private String oldBarcode;

    @Column(name = "NEW_BARCODE")
    private String newBarcode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public String getOwningingInstitution() {
        return owningingInstitution;
    }

    public void setOwningingInstitution(String owningingInstitution) {
        this.owningingInstitution = owningingInstitution;
    }

    public String getOwningingInstitutionItemId() {
        return owningingInstitutionItemId;
    }

    public void setOwningingInstitutionItemId(String owningingInstitutionItemId) {
        this.owningingInstitutionItemId = owningingInstitutionItemId;
    }

    public String getOldBarcode() {
        return oldBarcode;
    }

    public void setOldBarcode(String oldBarcode) {
        this.oldBarcode = oldBarcode;
    }

    public String getNewBarcode() {
        return newBarcode;
    }

    public void setNewBarcode(String newBarcode) {
        this.newBarcode = newBarcode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
