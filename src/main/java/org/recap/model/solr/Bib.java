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

    @Field("IsDeletedBib")
    private boolean isDeletedBib = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBibId() {
        return bibId;
    }

    public void setBibId(Integer bibId) {
        this.bibId = bibId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleDisplay() {
        return titleDisplay;
    }

    public void setTitleDisplay(String titleDisplay) {
        this.titleDisplay = titleDisplay;
    }

    public String getTitleStartsWith() {
        return titleStartsWith;
    }

    public void setTitleStartsWith(String titleStartsWith) {
        this.titleStartsWith = titleStartsWith;
    }

    public String getAuthorDisplay() {
        return authorDisplay;
    }

    public void setAuthorDisplay(String authorDisplay) {
        this.authorDisplay = authorDisplay;
    }

    public List<String> getAuthorSearch() {
        return authorSearch;
    }

    public void setAuthorSearch(List<String> authorSearch) {
        this.authorSearch = authorSearch;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublicationPlace() {
        return publicationPlace;
    }

    public void setPublicationPlace(String publicationPlace) {
        this.publicationPlace = publicationPlace;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getIsbn() {
        return isbn;
    }

    public void setIsbn(List<String> isbn) {
        this.isbn = isbn;
    }

    public List<String> getIssn() {
        return issn;
    }

    public void setIssn(List<String> issn) {
        this.issn = issn;
    }

    public List<String> getOclcNumber() {
        return oclcNumber;
    }

    public void setOclcNumber(List<String> oclcNumber) {
        this.oclcNumber = oclcNumber;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLccn() {
        return lccn;
    }

    public void setLccn(String lccn) {
        this.lccn = lccn;
    }

    public String getImprint() {
        return imprint;
    }

    public void setImprint(String imprint) {
        this.imprint = imprint;
    }

    public List<Integer> getHoldingsIdList() {
        return holdingsIdList;
    }

    public void setHoldingsIdList(List<Integer> holdingsIdList) {
        this.holdingsIdList = holdingsIdList;
    }

    public List<Integer> getOwningInstHoldingsIdList() {
        return owningInstHoldingsIdList;
    }

    public void setOwningInstHoldingsIdList(List<Integer> owningInstHoldingsIdList) {
        this.owningInstHoldingsIdList = owningInstHoldingsIdList;
    }

    public List<Integer> getBibItemIdList() {
        return bibItemIdList;
    }

    public void setBibItemIdList(List<Integer> bibItemIdList) {
        this.bibItemIdList = bibItemIdList;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getLeaderMaterialType() {
        return leaderMaterialType;
    }

    public void setLeaderMaterialType(String leaderMaterialType) {
        this.leaderMaterialType = leaderMaterialType;
    }

    public String getTitleSort() {
        return titleSort;
    }

    public void setTitleSort(String titleSort) {
        this.titleSort = titleSort;
    }

    public String getBibCreatedBy() {
        return bibCreatedBy;
    }

    public void setBibCreatedBy(String bibCreatedBy) {
        this.bibCreatedBy = bibCreatedBy;
    }

    public Date getBibCreatedDate() {
        return bibCreatedDate;
    }

    public void setBibCreatedDate(Date bibCreatedDate) {
        this.bibCreatedDate = bibCreatedDate;
    }

    public String getBibLastUpdatedBy() {
        return bibLastUpdatedBy;
    }

    public void setBibLastUpdatedBy(String bibLastUpdatedBy) {
        this.bibLastUpdatedBy = bibLastUpdatedBy;
    }

    public Date getBibLastUpdatedDate() {
        return bibLastUpdatedDate;
    }

    public void setBibLastUpdatedDate(Date bibLastUpdatedDate) {
        this.bibLastUpdatedDate = bibLastUpdatedDate;
    }

    public boolean isDeletedBib() {
        return isDeletedBib;
    }

    public void setDeletedBib(boolean deletedBib) {
        isDeletedBib = deletedBib;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bib)) return false;

        Bib bib = (Bib) o;

        if (getId() != null ? !getId().equals(bib.getId()) : bib.getId() != null) return false;
        if (getContentType() != null ? !getContentType().equals(bib.getContentType()) : bib.getContentType() != null)
            return false;
        if (getBibId() != null ? !getBibId().equals(bib.getBibId()) : bib.getBibId() != null) return false;
        if (getDocType() != null ? !getDocType().equals(bib.getDocType()) : bib.getDocType() != null) return false;
        if (getBarcode() != null ? !getBarcode().equals(bib.getBarcode()) : bib.getBarcode() != null) return false;
        if (getTitle() != null ? !getTitle().equals(bib.getTitle()) : bib.getTitle() != null) return false;
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
        if (getSubject() != null ? !getSubject().equals(bib.getSubject()) : bib.getSubject() != null) return false;
        if (getIsbn() != null ? !getIsbn().equals(bib.getIsbn()) : bib.getIsbn() != null) return false;
        if (getIssn() != null ? !getIssn().equals(bib.getIssn()) : bib.getIssn() != null) return false;
        if (getOclcNumber() != null ? !getOclcNumber().equals(bib.getOclcNumber()) : bib.getOclcNumber() != null)
            return false;
        if (getMaterialType() != null ? !getMaterialType().equals(bib.getMaterialType()) : bib.getMaterialType() != null)
            return false;
        if (getNotes() != null ? !getNotes().equals(bib.getNotes()) : bib.getNotes() != null) return false;
        if (getLccn() != null ? !getLccn().equals(bib.getLccn()) : bib.getLccn() != null) return false;
        if (getImprint() != null ? !getImprint().equals(bib.getImprint()) : bib.getImprint() != null) return false;
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
        result = 31 * result + (getTitleSort() != null ? getTitleSort().hashCode() : 0);
        return result;
    }
}
