package org.recap.model.search;

/**
 * Created by akulak on 23/12/16.
 */
public class DeaccessionItemResultsRow {

    private Integer itemId;
    private String deaccessionDate;
    private String title;
    private String deaccessionOwnInst;
    private String itemBarcode;
    private String cgd;
    private String deaccessionNotes;
    private String deaccessionCreatedBy;

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
     * Gets deaccession date.
     *
     * @return the deaccession date
     */
    public String getDeaccessionDate() {
        return deaccessionDate;
    }

    /**
     * Sets deaccession date.
     *
     * @param deaccessionDate the deaccession date
     */
    public void setDeaccessionDate(String deaccessionDate) {
        this.deaccessionDate = deaccessionDate;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets deaccession own inst.
     *
     * @return the deaccession own inst
     */
    public String getDeaccessionOwnInst() {
        return deaccessionOwnInst;
    }

    /**
     * Sets deaccession own inst.
     *
     * @param deaccessionOwnInst the deaccession own inst
     */
    public void setDeaccessionOwnInst(String deaccessionOwnInst) {
        this.deaccessionOwnInst = deaccessionOwnInst;
    }

    /**
     * Gets item barcode.
     *
     * @return the item barcode
     */
    public String getItemBarcode() {
        return itemBarcode;
    }

    /**
     * Sets item barcode.
     *
     * @param itemBarcode the item barcode
     */
    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    /**
     * Gets cgd.
     *
     * @return the cgd
     */
    public String getCgd() {
        return cgd;
    }

    /**
     * Sets cgd.
     *
     * @param cgd the cgd
     */
    public void setCgd(String cgd) {
        this.cgd = cgd;
    }

    /**
     * Gets deaccession notes.
     *
     * @return the deaccession notes
     */
    public String getDeaccessionNotes() {
        return deaccessionNotes;
    }

    /**
     * Sets deaccession notes.
     *
     * @param deaccessionNotes the deaccession notes
     */
    public void setDeaccessionNotes(String deaccessionNotes) {
        this.deaccessionNotes = deaccessionNotes;
    }

    /**
     * Gets deaccession created by.
     *
     * @return the deaccession created by
     */
    public String getDeaccessionCreatedBy() {
        return deaccessionCreatedBy;
    }

    /**
     * Sets deaccession created by.
     *
     * @param deaccessionCreatedBy the deaccession created by
     */
    public void setDeaccessionCreatedBy(String deaccessionCreatedBy) {
        this.deaccessionCreatedBy = deaccessionCreatedBy;
    }
}
