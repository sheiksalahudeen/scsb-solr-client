package org.recap.model.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by angelind on 31/10/16.
 */
@Entity
@Table(name = "MATCHING_BIB_T", schema = "RECAP", catalog = "")
public class MatchingBibEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ROOT")
    private String root;

    @Column(name = "BIB_ID")
    private Integer bibId;

    @Column(name = "OWNING_INSTITUTION")
    private String owningInstitution;

    @Column(name = "OWNING_INST_BIB_ID")
    private String owningInstBibId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "OCLC")
    private String oclc;

    @Column(name = "ISBN")
    private String isbn;

    @Column(name = "ISSN")
    private String issn;

    @Column(name = "LCCN")
    private String lccn;

    @Column(name = "MATERIAL_TYPE")
    private String materialType;

    @Column(name = "MATCHING")
    private String matching;

    @Column(name = "STATUS")
    private String status;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets root.
     *
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * Sets root.
     *
     * @param root the root
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * Gets bib id.
     *
     * @return the bib id
     */
    public Integer getBibId() {
        return bibId;
    }

    /**
     * Sets bib id.
     *
     * @param bibId the bib id
     */
    public void setBibId(Integer bibId) {
        this.bibId = bibId;
    }

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
     * Gets owning inst bib id.
     *
     * @return the owning inst bib id
     */
    public String getOwningInstBibId() {
        return owningInstBibId;
    }

    /**
     * Sets owning inst bib id.
     *
     * @param owningInstBibId the owning inst bib id
     */
    public void setOwningInstBibId(String owningInstBibId) {
        this.owningInstBibId = owningInstBibId;
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
     * Gets oclc.
     *
     * @return the oclc
     */
    public String getOclc() {
        return oclc;
    }

    /**
     * Sets oclc.
     *
     * @param oclc the oclc
     */
    public void setOclc(String oclc) {
        this.oclc = oclc;
    }

    /**
     * Gets isbn.
     *
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets issn.
     *
     * @return the issn
     */
    public String getIssn() {
        return issn;
    }

    /**
     * Sets issn.
     *
     * @param issn the issn
     */
    public void setIssn(String issn) {
        this.issn = issn;
    }

    /**
     * Gets lccn.
     *
     * @return the lccn
     */
    public String getLccn() {
        return lccn;
    }

    /**
     * Sets lccn.
     *
     * @param lccn the lccn
     */
    public void setLccn(String lccn) {
        this.lccn = lccn;
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
     * Gets matching.
     *
     * @return the matching
     */
    public String getMatching() {
        return matching;
    }

    /**
     * Sets matching.
     *
     * @param matching the matching
     */
    public void setMatching(String matching) {
        this.matching = matching;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
