package org.recap.model.matchingReports;

import java.io.Serializable;
import java.util.List;

/**
 * Created by angelind on 16/6/17.
 */
public class TitleExceptionReport implements Serializable{

    private String owningInstitution;
    private String bibId;
    private String owningInstitutionBibId;
    private String materialType;
    private String oclc;
    private String isbn;
    private String issn;
    private String lccn;
    private List<String> titleList;

    /**
     * Gets owning institution.
     *
     * @return the owning institution
     */
    public String getOwningInstitution() {
        return owningInstitution;
    }

    /**
     * Sets owning institution.
     *
     * @param owningInstitution the owning institution
     */
    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public String getBibId() {
        return bibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    /**
     * Gets owning institution bib id.
     *
     * @return the owning institution bib id
     */
    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    /**
     * Sets owning institution bib id.
     *
     * @param owningInstitutionBibId the owning institution bib id
     */
    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    /**
     * Gets material type.
     *
     * @return the material type
     */
    public String getMaterialType() {
        return materialType;
    }

    /**
     * Sets material type.
     *
     * @param materialType the material type
     */
    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    /**
     * Gets oclc.
     *
     * @return the oclc
     */
    public String getOCLC() {
        return oclc;
    }

    /**
     * Sets oclc.
     *
     * @param oclc the oclc
     */
    public void setOCLC(String oclc) {
        this.oclc = oclc;
    }

    /**
     * Gets isbn.
     *
     * @return the isbn
     */
    public String getISBN() {
        return isbn;
    }

    /**
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets issn.
     *
     * @return the issn
     */
    public String getISSN() {
        return issn;
    }

    /**
     * Sets issn.
     *
     * @param issn the issn
     */
    public void setISSN(String issn) {
        this.issn = issn;
    }

    /**
     * Gets lccn.
     *
     * @return the lccn
     */
    public String getLCCN() {
        return lccn;
    }

    /**
     * Sets lccn.
     *
     * @param lccn the lccn
     */
    public void setLCCN(String lccn) {
        this.lccn = lccn;
    }

    /**
     * Gets title list.
     *
     * @return the title list
     */
    public List<String> getTitleList() {
        return titleList;
    }

    /**
     * Sets title list.
     *
     * @param titleList the title list
     */
    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }
}
