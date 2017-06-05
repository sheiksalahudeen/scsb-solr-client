package org.recap.model.solr;


import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class Bib {
    @Id
    @Field("id")
    private String id;

    @Field("ContentType")
    private String contentType;

    @Field("BibId")
    private Integer bibId;

    @Field("DocType")
    private String docType;

    @Field("Barcode")
    private String barcode;

    @Field("Title_search")
    private String title;

    @Field("Title_display")
    private String titleDisplay;

    @Field("TitleStartsWith")
    private String titleStartsWith;

    @Field("Title_subfield_a")
    private String titleSubFieldA;

    @Field("Author_display")
    private String authorDisplay;

    @Field("Author_search")
    private List<String> authorSearch;

    @Field("BibOwningInstitution")
    private String owningInstitution;

    @Field("Publisher")
    private String publisher;

    @Field("PublicationPlace")
    private String publicationPlace;

    @Field("PublicationDate")
    private String publicationDate;

    @Field("Subject")
    private String subject;

    @Field("ISBN")
    private List<String> isbn;

    @Field("ISSN")
    private List<String> issn;

    @Field("OCLCNumber")
    private List<String> oclcNumber;

    @Field("MaterialType")
    private String materialType;

    @Field("Notes")
    private String notes;

    @Field("LCCN")
    private String lccn;

    @Field("Imprint")
    private String imprint;

    @Field("BibHoldingsId")
    private List<Integer> holdingsIdList;

    @Field("OwningInstHoldingsId")
    private List<Integer> owningInstHoldingsIdList;

    @Field("BibItemId")
    private List<Integer> bibItemIdList;

    @Field("OwningInstitutionBibId")
    private String owningInstitutionBibId;

    @Field("LeaderMaterialType")
    private String leaderMaterialType;

    @Field("Title_sort")
    private String titleSort;

    @Field("BibCreatedBy")
    private String bibCreatedBy;

    @Field("BibCreatedDate")
    private Date bibCreatedDate;

    @Field("BibLastUpdatedBy")
    private String bibLastUpdatedBy;

    @Field("BibLastUpdatedDate")
    private Date bibLastUpdatedDate;

    @Field("BibHoldingLastUpdatedDate")
    private Date bibHoldingLastUpdatedDate;

    @Field("BibItemLastUpdatedDate")
    private Date bibItemLastUpdatedDate;

    @Field("IsDeletedBib")
    private boolean isDeletedBib = false;

    @Field("BibCatalogingStatus")
    private String bibCatalogingStatus;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
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
     * Gets doc type.
     *
     * @return the doc type
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets doc type.
     *
     * @param docType the doc type
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets barcode.
     *
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Sets barcode.
     *
     * @param barcode the barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
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
     * Gets title display.
     *
     * @return the title display
     */
    public String getTitleDisplay() {
        return titleDisplay;
    }

    /**
     * Sets title display.
     *
     * @param titleDisplay the title display
     */
    public void setTitleDisplay(String titleDisplay) {
        this.titleDisplay = titleDisplay;
    }

    /**
     * Gets title starts with.
     *
     * @return the title starts with
     */
    public String getTitleStartsWith() {
        return titleStartsWith;
    }

    /**
     * Sets title starts with.
     *
     * @param titleStartsWith the title starts with
     */
    public void setTitleStartsWith(String titleStartsWith) {
        this.titleStartsWith = titleStartsWith;
    }

    /**
     * Gets title sub field a.
     *
     * @return the title sub field a
     */
    public String getTitleSubFieldA() {
        return titleSubFieldA;
    }

    /**
     * Sets title sub field a.
     *
     * @param titleSubFieldA the title sub field a
     */
    public void setTitleSubFieldA(String titleSubFieldA) {
        this.titleSubFieldA = titleSubFieldA;
    }

    /**
     * Gets author display.
     *
     * @return the author display
     */
    public String getAuthorDisplay() {
        return authorDisplay;
    }

    /**
     * Sets author display.
     *
     * @param authorDisplay the author display
     */
    public void setAuthorDisplay(String authorDisplay) {
        this.authorDisplay = authorDisplay;
    }

    /**
     * Gets author search.
     *
     * @return the author search
     */
    public List<String> getAuthorSearch() {
        return authorSearch;
    }

    /**
     * Sets author search.
     *
     * @param authorSearch the author search
     */
    public void setAuthorSearch(List<String> authorSearch) {
        this.authorSearch = authorSearch;
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
     * Gets publisher.
     *
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets publisher.
     *
     * @param publisher the publisher
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Gets publication place.
     *
     * @return the publication place
     */
    public String getPublicationPlace() {
        return publicationPlace;
    }

    /**
     * Sets publication place.
     *
     * @param publicationPlace the publication place
     */
    public void setPublicationPlace(String publicationPlace) {
        this.publicationPlace = publicationPlace;
    }

    /**
     * Gets publication date.
     *
     * @return the publication date
     */
    public String getPublicationDate() {
        return publicationDate;
    }

    /**
     * Sets publication date.
     *
     * @param publicationDate the publication date
     */
    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    /**
     * Gets subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets subject.
     *
     * @param subject the subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets isbn.
     *
     * @return the isbn
     */
    public List<String> getIsbn() {
        return isbn;
    }

    /**
     * Sets isbn.
     *
     * @param isbn the isbn
     */
    public void setIsbn(List<String> isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets issn.
     *
     * @return the issn
     */
    public List<String> getIssn() {
        return issn;
    }

    /**
     * Sets issn.
     *
     * @param issn the issn
     */
    public void setIssn(List<String> issn) {
        this.issn = issn;
    }

    /**
     * Gets oclc number.
     *
     * @return the oclc number
     */
    public List<String> getOclcNumber() {
        return oclcNumber;
    }

    /**
     * Sets oclc number.
     *
     * @param oclcNumber the oclc number
     */
    public void setOclcNumber(List<String> oclcNumber) {
        this.oclcNumber = oclcNumber;
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
     * Gets notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets notes.
     *
     * @param notes the notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
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
     * Gets imprint.
     *
     * @return the imprint
     */
    public String getImprint() {
        return imprint;
    }

    /**
     * Sets imprint.
     *
     * @param imprint the imprint
     */
    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    /**
     * Gets holdings id list.
     *
     * @return the holdings id list
     */
    public List<Integer> getHoldingsIdList() {
        return holdingsIdList;
    }

    /**
     * Sets holdings id list.
     *
     * @param holdingsIdList the holdings id list
     */
    public void setHoldingsIdList(List<Integer> holdingsIdList) {
        this.holdingsIdList = holdingsIdList;
    }

    /**
     * Gets owning inst holdings id list.
     *
     * @return the owning inst holdings id list
     */
    public List<Integer> getOwningInstHoldingsIdList() {
        return owningInstHoldingsIdList;
    }

    /**
     * Sets owning inst holdings id list.
     *
     * @param owningInstHoldingsIdList the owning inst holdings id list
     */
    public void setOwningInstHoldingsIdList(List<Integer> owningInstHoldingsIdList) {
        this.owningInstHoldingsIdList = owningInstHoldingsIdList;
    }

    /**
     * Gets bib item id list.
     *
     * @return the bib item id list
     */
    public List<Integer> getBibItemIdList() {
        return bibItemIdList;
    }

    /**
     * Sets bib item id list.
     *
     * @param bibItemIdList the bib item id list
     */
    public void setBibItemIdList(List<Integer> bibItemIdList) {
        this.bibItemIdList = bibItemIdList;
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
     * Gets content type.
     *
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets content type.
     *
     * @param contentType the content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets leader material type.
     *
     * @return the leader material type
     */
    public String getLeaderMaterialType() {
        return leaderMaterialType;
    }

    /**
     * Sets leader material type.
     *
     * @param leaderMaterialType the leader material type
     */
    public void setLeaderMaterialType(String leaderMaterialType) {
        this.leaderMaterialType = leaderMaterialType;
    }

    /**
     * Gets title sort.
     *
     * @return the title sort
     */
    public String getTitleSort() {
        return titleSort;
    }

    /**
     * Sets title sort.
     *
     * @param titleSort the title sort
     */
    public void setTitleSort(String titleSort) {
        this.titleSort = titleSort;
    }

    /**
     * Gets bib created by.
     *
     * @return the bib created by
     */
    public String getBibCreatedBy() {
        return bibCreatedBy;
    }

    /**
     * Sets bib created by.
     *
     * @param bibCreatedBy the bib created by
     */
    public void setBibCreatedBy(String bibCreatedBy) {
        this.bibCreatedBy = bibCreatedBy;
    }

    /**
     * Gets bib created date.
     *
     * @return the bib created date
     */
    public Date getBibCreatedDate() {
        return bibCreatedDate;
    }

    /**
     * Sets bib created date.
     *
     * @param bibCreatedDate the bib created date
     */
    public void setBibCreatedDate(Date bibCreatedDate) {
        this.bibCreatedDate = bibCreatedDate;
    }

    /**
     * Gets bib last updated by.
     *
     * @return the bib last updated by
     */
    public String getBibLastUpdatedBy() {
        return bibLastUpdatedBy;
    }

    /**
     * Sets bib last updated by.
     *
     * @param bibLastUpdatedBy the bib last updated by
     */
    public void setBibLastUpdatedBy(String bibLastUpdatedBy) {
        this.bibLastUpdatedBy = bibLastUpdatedBy;
    }

    /**
     * Gets bib last updated date.
     *
     * @return the bib last updated date
     */
    public Date getBibLastUpdatedDate() {
        return bibLastUpdatedDate;
    }

    /**
     * Sets bib last updated date.
     *
     * @param bibLastUpdatedDate the bib last updated date
     */
    public void setBibLastUpdatedDate(Date bibLastUpdatedDate) {
        this.bibLastUpdatedDate = bibLastUpdatedDate;
    }

    /**
     * Gets bib holding last updated date.
     *
     * @return the bib holding last updated date
     */
    public Date getBibHoldingLastUpdatedDate() {
        return bibHoldingLastUpdatedDate;
    }

    /**
     * Sets bib holding last updated date.
     *
     * @param bibHoldingLastUpdatedDate the bib holding last updated date
     */
    public void setBibHoldingLastUpdatedDate(Date bibHoldingLastUpdatedDate) {
        this.bibHoldingLastUpdatedDate = bibHoldingLastUpdatedDate;
    }

    /**
     * Gets bib item last updated date.
     *
     * @return the bib item last updated date
     */
    public Date getBibItemLastUpdatedDate() {
        return bibItemLastUpdatedDate;
    }

    /**
     * Sets bib item last updated date.
     *
     * @param bibItemLastUpdatedDate the bib item last updated date
     */
    public void setBibItemLastUpdatedDate(Date bibItemLastUpdatedDate) {
        this.bibItemLastUpdatedDate = bibItemLastUpdatedDate;
    }

    /**
     * Is deleted bib boolean.
     *
     * @return the boolean
     */
    public boolean isDeletedBib() {
        return isDeletedBib;
    }

    /**
     * Sets deleted bib.
     *
     * @param deletedBib the deleted bib
     */
    public void setDeletedBib(boolean deletedBib) {
        isDeletedBib = deletedBib;
    }

    /**
     * Gets bib cataloging status.
     *
     * @return the bib cataloging status
     */
    public String getBibCatalogingStatus() {
        return bibCatalogingStatus;
    }

    /**
     * Sets bib cataloging status.
     *
     * @param bibCatalogingStatus the bib cataloging status
     */
    public void setBibCatalogingStatus(String bibCatalogingStatus) {
        this.bibCatalogingStatus = bibCatalogingStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Bib))
            return false;

        Bib bib = (Bib) o;

        if (getId() != null ? !getId().equals(bib.getId()) : bib.getId() != null)
            return false;
        if (getContentType() != null ? !getContentType().equals(bib.getContentType()) : bib.getContentType() != null)
            return false;
        if (getBibId() != null ? !getBibId().equals(bib.getBibId()) : bib.getBibId() != null)
            return false;
        if (getDocType() != null ? !getDocType().equals(bib.getDocType()) : bib.getDocType() != null)
            return false;
        if (getBarcode() != null ? !getBarcode().equals(bib.getBarcode()) : bib.getBarcode() != null)
            return false;
        if (getTitle() != null ? !getTitle().equals(bib.getTitle()) : bib.getTitle() != null)
            return false;
        if (getTitleDisplay() != null ? !getTitleDisplay().equals(bib.getTitleDisplay()) : bib.getTitleDisplay() != null)
            return false;
        if (getTitleStartsWith() != null ? !getTitleStartsWith().equals(bib.getTitleStartsWith()) : bib.getTitleStartsWith() != null)
            return false;
        if (getAuthorDisplay() != null ? !getAuthorDisplay().equals(bib.getAuthorDisplay()) : bib.getAuthorDisplay() != null)
            return false;
        if (getAuthorSearch() != null ? !getAuthorSearch().equals(bib.getAuthorSearch()) : bib.getAuthorSearch() != null)
            return false;
        if (getOwningInstitution() != null ? !getOwningInstitution().equals(bib.getOwningInstitution()) : bib.getOwningInstitution() != null)
            return false;
        if (getPublisher() != null ? !getPublisher().equals(bib.getPublisher()) : bib.getPublisher() != null)
            return false;
        if (getPublicationPlace() != null ? !getPublicationPlace().equals(bib.getPublicationPlace()) : bib.getPublicationPlace() != null)
            return false;
        if (getPublicationDate() != null ? !getPublicationDate().equals(bib.getPublicationDate()) : bib.getPublicationDate() != null)
            return false;
        if (getSubject() != null ? !getSubject().equals(bib.getSubject()) : bib.getSubject() != null)
            return false;
        if (getIsbn() != null ? !getIsbn().equals(bib.getIsbn()) : bib.getIsbn() != null)
            return false;
        if (getIssn() != null ? !getIssn().equals(bib.getIssn()) : bib.getIssn() != null)
            return false;
        if (getOclcNumber() != null ? !getOclcNumber().equals(bib.getOclcNumber()) : bib.getOclcNumber() != null)
            return false;
        if (getMaterialType() != null ? !getMaterialType().equals(bib.getMaterialType()) : bib.getMaterialType() != null)
            return false;
        if (getNotes() != null ? !getNotes().equals(bib.getNotes()) : bib.getNotes() != null)
            return false;
        if (getLccn() != null ? !getLccn().equals(bib.getLccn()) : bib.getLccn() != null)
            return false;
        if (getImprint() != null ? !getImprint().equals(bib.getImprint()) : bib.getImprint() != null)
            return false;
        if (getHoldingsIdList() != null ? !getHoldingsIdList().equals(bib.getHoldingsIdList()) : bib.getHoldingsIdList() != null)
            return false;
        if (getOwningInstHoldingsIdList() != null ? !getOwningInstHoldingsIdList().equals(bib.getOwningInstHoldingsIdList()) : bib.getOwningInstHoldingsIdList() != null)
            return false;
        if (getBibItemIdList() != null ? !getBibItemIdList().equals(bib.getBibItemIdList()) : bib.getBibItemIdList() != null)
            return false;
        if (getOwningInstitutionBibId() != null ? !getOwningInstitutionBibId().equals(bib.getOwningInstitutionBibId()) : bib.getOwningInstitutionBibId() != null)
            return false;
        if (getLeaderMaterialType() != null ? !getLeaderMaterialType().equals(bib.getLeaderMaterialType()) : bib.getLeaderMaterialType() != null)
            return false;
        if (getBibCatalogingStatus() != null ? !getBibCatalogingStatus().equals(bib.getBibCatalogingStatus()) : bib.getBibCatalogingStatus() != null)
            return false;
        return getTitleSort() != null ? getTitleSort().equals(bib.getTitleSort()) : bib.getTitleSort() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getContentType() != null ? getContentType().hashCode() : 0);
        result = 31 * result + (getBibId() != null ? getBibId().hashCode() : 0);
        result = 31 * result + (getDocType() != null ? getDocType().hashCode() : 0);
        result = 31 * result + (getBarcode() != null ? getBarcode().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getTitleDisplay() != null ? getTitleDisplay().hashCode() : 0);
        result = 31 * result + (getTitleStartsWith() != null ? getTitleStartsWith().hashCode() : 0);
        result = 31 * result + (getAuthorDisplay() != null ? getAuthorDisplay().hashCode() : 0);
        result = 31 * result + (getAuthorSearch() != null ? getAuthorSearch().hashCode() : 0);
        result = 31 * result + (getOwningInstitution() != null ? getOwningInstitution().hashCode() : 0);
        result = 31 * result + (getPublisher() != null ? getPublisher().hashCode() : 0);
        result = 31 * result + (getPublicationPlace() != null ? getPublicationPlace().hashCode() : 0);
        result = 31 * result + (getPublicationDate() != null ? getPublicationDate().hashCode() : 0);
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getIsbn() != null ? getIsbn().hashCode() : 0);
        result = 31 * result + (getIssn() != null ? getIssn().hashCode() : 0);
        result = 31 * result + (getOclcNumber() != null ? getOclcNumber().hashCode() : 0);
        result = 31 * result + (getMaterialType() != null ? getMaterialType().hashCode() : 0);
        result = 31 * result + (getNotes() != null ? getNotes().hashCode() : 0);
        result = 31 * result + (getLccn() != null ? getLccn().hashCode() : 0);
        result = 31 * result + (getImprint() != null ? getImprint().hashCode() : 0);
        result = 31 * result + (getHoldingsIdList() != null ? getHoldingsIdList().hashCode() : 0);
        result = 31 * result + (getOwningInstHoldingsIdList() != null ? getOwningInstHoldingsIdList().hashCode() : 0);
        result = 31 * result + (getBibItemIdList() != null ? getBibItemIdList().hashCode() : 0);
        result = 31 * result + (getOwningInstitutionBibId() != null ? getOwningInstitutionBibId().hashCode() : 0);
        result = 31 * result + (getLeaderMaterialType() != null ? getLeaderMaterialType().hashCode() : 0);
        result = 31 * result + (getBibCatalogingStatus() != null ? getBibCatalogingStatus().hashCode() : 0);
        result = 31 * result + (getTitleSort() != null ? getTitleSort().hashCode() : 0);
        return result;
    }
}
