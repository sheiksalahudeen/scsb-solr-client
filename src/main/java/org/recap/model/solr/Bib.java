package org.recap.model.solr;


import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Created by pvsubrah on 6/11/16.
 */

public class Bib {
    @Id
    @Field("id")
    private String id;

    @Field("BibId")
    private String bibId;

    @Field("DocType")
    private String docType;

    @Field("Barcode")
    private String barcode;

    @Field("Title")
    private String title;

    @Field("Author")
    private String author;

    @Field("OwningInstitution")
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

    @Field("HoldingsId")
    private List<String> holdingsIdList;

    @Field("BibItemId")
    private List<String> bibItemIdList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public List<String> getHoldingsIdList() {
        return holdingsIdList;
    }

    public void setHoldingsIdList(List<String> holdingsIdList) {
        this.holdingsIdList = holdingsIdList;
    }

    public List<String> getBibItemIdList() {
        return bibItemIdList;
    }

    public void setBibItemIdList(List<String> bibItemIdList) {
        this.bibItemIdList = bibItemIdList;
    }
}
