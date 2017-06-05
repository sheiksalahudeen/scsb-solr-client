package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
public class BibliographicMarcForm {

    private Integer bibId;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String owningInstitution;
    private String callNumber;
    private String leaderMaterialType;
    private String tag000;
    private String controlNumber001;
    private String controlNumber005;
    private String controlNumber008;
    private String content;
    private List<BibDataField> bibDataFields = new ArrayList<>();
    private String errorMessage;

    private Integer itemId;
    private String availability;
    private String barcode;
    private String locationCode;
    private String useRestriction;
    private String monographCollectionGroupDesignation;
    private String collectionGroupDesignation;
    private String newCollectionGroupDesignation;
    private String cgdChangeNotes;
    private String customerCode;
    private String deaccessionType;
    private String deaccessionNotes;
    private List<String> deliveryLocations = new ArrayList<>();
    private String deliveryLocation;
    private boolean shared = false;
    private boolean submitted = false;
    private String message;
    private String collectionAction;
    private boolean allowEdit;

    /**
     * Is allow edit boolean.
     *
     * @return the boolean
     */
    public boolean isAllowEdit() {
        return allowEdit;
    }

    /**
     * Sets allow edit.
     *
     * @param allowEdit the allow edit
     */
    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
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
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     */
    public void setAuthor(String author) {
        this.author = author;
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
     * Gets published date.
     *
     * @return the published date
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * Sets published date.
     *
     * @param publishedDate the published date
     */
    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
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
     * Gets call number.
     *
     * @return the call number
     */
    public String getCallNumber() {
        return callNumber;
    }

    /**
     * Sets call number.
     *
     * @param callNumber the call number
     */
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    /**
     * Gets tag 000.
     *
     * @return the tag 000
     */
    public String getTag000() {
        return tag000;
    }

    /**
     * Sets tag 000.
     *
     * @param tag000 the tag 000
     */
    public void setTag000(String tag000) {
        this.tag000 = tag000;
    }

    /**
     * Gets control number 001.
     *
     * @return the control number 001
     */
    public String getControlNumber001() {
        return controlNumber001;
    }

    /**
     * Sets control number 001.
     *
     * @param controlNumber001 the control number 001
     */
    public void setControlNumber001(String controlNumber001) {
        this.controlNumber001 = controlNumber001;
    }

    /**
     * Gets control number 005.
     *
     * @return the control number 005
     */
    public String getControlNumber005() {
        return controlNumber005;
    }

    /**
     * Sets control number 005.
     *
     * @param controlNumber005 the control number 005
     */
    public void setControlNumber005(String controlNumber005) {
        this.controlNumber005 = controlNumber005;
    }

    /**
     * Gets control number 008.
     *
     * @return the control number 008
     */
    public String getControlNumber008() {
        return controlNumber008;
    }

    /**
     * Sets control number 008.
     *
     * @param controlNumber008 the control number 008
     */
    public void setControlNumber008(String controlNumber008) {
        this.controlNumber008 = controlNumber008;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets bib data fields.
     *
     * @return the bib data fields
     */
    public List<BibDataField> getBibDataFields() {
        return bibDataFields;
    }

    /**
     * Sets bib data fields.
     *
     * @param bibDataFields the bib data fields
     */
    public void setBibDataFields(List<BibDataField> bibDataFields) {
        this.bibDataFields = bibDataFields;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

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
     * Gets availability.
     *
     * @return the availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Sets availability.
     *
     * @param availability the availability
     */
    public void setAvailability(String availability) {
        this.availability = availability;
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
     * Gets location code.
     *
     * @return the location code
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * Sets location code.
     *
     * @param locationCode the location code
     */
    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    /**
     * Gets use restriction.
     *
     * @return the use restriction
     */
    public String getUseRestriction() {
        return useRestriction;
    }

    /**
     * Sets use restriction.
     *
     * @param useRestriction the use restriction
     */
    public void setUseRestriction(String useRestriction) {
        this.useRestriction = useRestriction;
    }

    /**
     * Gets collection group designation.
     *
     * @return the collection group designation
     */
    public String getCollectionGroupDesignation() {
        return collectionGroupDesignation;
    }

    /**
     * Sets collection group designation.
     *
     * @param collectionGroupDesignation the collection group designation
     */
    public void setCollectionGroupDesignation(String collectionGroupDesignation) {
        this.collectionGroupDesignation = collectionGroupDesignation;
    }

    /**
     * Gets new collection group designation.
     *
     * @return the new collection group designation
     */
    public String getNewCollectionGroupDesignation() {
        return newCollectionGroupDesignation;
    }

    /**
     * Sets new collection group designation.
     *
     * @param newCollectionGroupDesignation the new collection group designation
     */
    public void setNewCollectionGroupDesignation(String newCollectionGroupDesignation) {
        this.newCollectionGroupDesignation = newCollectionGroupDesignation;
    }

    /**
     * Gets cgd change notes.
     *
     * @return the cgd change notes
     */
    public String getCgdChangeNotes() {
        return cgdChangeNotes;
    }

    /**
     * Sets cgd change notes.
     *
     * @param cgdChangeNotes the cgd change notes
     */
    public void setCgdChangeNotes(String cgdChangeNotes) {
        this.cgdChangeNotes = cgdChangeNotes;
    }

    /**
     * Gets customer code.
     *
     * @return the customer code
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets customer code.
     *
     * @param customerCode the customer code
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * Gets deaccession type.
     *
     * @return the deaccession type
     */
    public String getDeaccessionType() {
        return deaccessionType;
    }

    /**
     * Sets deaccession type.
     *
     * @param deaccessionType the deaccession type
     */
    public void setDeaccessionType(String deaccessionType) {
        this.deaccessionType = deaccessionType;
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
     * Gets delivery locations.
     *
     * @return the delivery locations
     */
    public List<String> getDeliveryLocations() {
        return deliveryLocations;
    }

    /**
     * Sets delivery locations.
     *
     * @param deliveryLocations the delivery locations
     */
    public void setDeliveryLocations(List<String> deliveryLocations) {
        this.deliveryLocations = deliveryLocations;
    }

    /**
     * Gets delivery location.
     *
     * @return the delivery location
     */
    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    /**
     * Sets delivery location.
     *
     * @param deliveryLocation the delivery location
     */
    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    /**
     * Is shared boolean.
     *
     * @return the boolean
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * Sets shared.
     *
     * @param shared the shared
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * Is submitted boolean.
     *
     * @return the boolean
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Sets submitted.
     *
     * @param submitted the submitted
     */
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets collection action.
     *
     * @return the collection action
     */
    public String getCollectionAction() {
        return collectionAction;
    }

    /**
     * Sets collection action.
     *
     * @param collectionAction the collection action
     */
    public void setCollectionAction(String collectionAction) {
        this.collectionAction = collectionAction;
    }

    /**
     * Gets monograph collection group designation.
     *
     * @return the monograph collection group designation
     */
    public String getMonographCollectionGroupDesignation() {
        return monographCollectionGroupDesignation;
    }

    /**
     * Sets monograph collection group designation.
     *
     * @param monographCollectionGroupDesignation the monograph collection group designation
     */
    public void setMonographCollectionGroupDesignation(String monographCollectionGroupDesignation) {
        this.monographCollectionGroupDesignation = monographCollectionGroupDesignation;
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
}
