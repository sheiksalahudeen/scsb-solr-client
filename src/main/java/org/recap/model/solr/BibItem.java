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
     * Gets items.
     *
     * @return the items
     */
    public List<Item> getItems() {
        if(items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    /**
     * Sets items.
     *
     * @param items the items
     */
    public void setItems(List<Item> items) {
        this.items = items;
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
     * Gets holdings list.
     *
     * @return the holdings list
     */
    public List<Holdings> getHoldingsList() {
        if(holdingsList == null) {
            holdingsList = new ArrayList<>();
        }
        return holdingsList;
    }

    /**
     * Sets holdings list.
     *
     * @param holdingsList the holdings list
     */
    public void setHoldingsList(List<Holdings> holdingsList) {
        this.holdingsList = holdingsList;
    }

    /**
     * Add items to the item list.
     *
     * @param item the item
     */
    public void addItem(Item item) {
        getItems().add(item);
    }

    /**
     * Add holdings.
     *
     * @param holdings the holdings
     */
    public void addHoldings(Holdings holdings) {
        getHoldingsList().add(holdings);
    }
}