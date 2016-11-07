package org.recap.model.solr;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 8/7/16.
 */
public class BibItem {

    @Id
    @Field("id")
    private String id;

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

    @Field("OwningInstHoldingsId")
    private List<Integer> owningInstHoldingsIdList;

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

    @Ignore
    private String root;

    @Ignore
    private List<Item> items = new ArrayList<>();

    @Ignore
    private List<Holdings> holdingsList = new ArrayList<>();

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

    public List<Integer> getOwningInstHoldingsIdList() {
        return owningInstHoldingsIdList;
    }

    public void setOwningInstHoldingsIdList(List<Integer> owningInstHoldingsIdList) {
        this.owningInstHoldingsIdList = owningInstHoldingsIdList;
    }

    public String getOwningInstitutionBibId() {
        return owningInstitutionBibId;
    }

    public void setOwningInstitutionBibId(String owningInstitutionBibId) {
        this.owningInstitutionBibId = owningInstitutionBibId;
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

    public List<Item> getItems() {
        if(items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public List<Holdings> getHoldingsList() {
        if(holdingsList == null) {
            holdingsList = new ArrayList<>();
        }
        return holdingsList;
    }

    public void setHoldingsList(List<Holdings> holdingsList) {
        this.holdingsList = holdingsList;
    }

    public void addItem(Item item) {
        getItems().add(item);
    }

    public void addHoldings(Holdings holdings) {
        getHoldingsList().add(holdings);
    }
}