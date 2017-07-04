package org.recap.model.solr;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by rajeshbabuk on 13/9/16.
 */
public class Holdings {

    @Id
    @Field
    private String id;

    @Field("HoldingId")
    private Integer holdingsId;

    @Field("DocType")
    private String docType;

    @Field("SummaryHoldings")
    private String summaryHoldings;

    @Field("HoldingsOwningInstitution")
    private String owningInstitution;

    @Field("OwningInstitutionHoldingsId")
    private String owningInstitutionHoldingsId;

    @Field("HoldingsCreatedBy")
    private String holdingsCreatedBy;

    @Field("HoldingsCreatedDate")
    private Date holdingsCreatedDate;

    @Field("HoldingsLastUpdatedBy")
    private String holdingsLastUpdatedBy;

    @Field("HoldingsLastUpdatedDate")
    private Date holdingsLastUpdatedDate;

    @Field("IsDeletedHoldings")
    private boolean isDeletedHoldings = false;

    @Ignore
    private String root;

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
     * Gets holdings id.
     *
     * @return the holdings id
     */
    public Integer getHoldingsId() {
        return holdingsId;
    }

    /**
     * Sets holdings id.
     *
     * @param holdingsId the holdings id
     */
    public void setHoldingsId(Integer holdingsId) {
        this.holdingsId = holdingsId;
    }

    /**
     * Gets owning institution holdings id.
     *
     * @return the owning institution holdings id
     */
    public String getOwningInstitutionHoldingsId() {
        return owningInstitutionHoldingsId;
    }

    /**
     * Sets owning institution holdings id.
     *
     * @param owningInstitutionHoldingsId the owning institution holdings id
     */
    public void setOwningInstitutionHoldingsId(String owningInstitutionHoldingsId) {
        this.owningInstitutionHoldingsId = owningInstitutionHoldingsId;
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
     * Gets summary holdings.
     *
     * @return the summary holdings
     */
    public String getSummaryHoldings() {
        return summaryHoldings;
    }

    /**
     * Sets summary holdings.
     *
     * @param summaryHoldings the summary holdings
     */
    public void setSummaryHoldings(String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
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
     * Gets holdings created by.
     *
     * @return the holdings created by
     */
    public String getHoldingsCreatedBy() {
        return holdingsCreatedBy;
    }

    /**
     * Sets holdings created by.
     *
     * @param holdingsCreatedBy the holdings created by
     */
    public void setHoldingsCreatedBy(String holdingsCreatedBy) {
        this.holdingsCreatedBy = holdingsCreatedBy;
    }

    /**
     * Gets holdings created date.
     *
     * @return the holdings created date
     */
    public Date getHoldingsCreatedDate() {
        return holdingsCreatedDate;
    }

    /**
     * Sets holdings created date.
     *
     * @param holdingsCreatedDate the holdings created date
     */
    public void setHoldingsCreatedDate(Date holdingsCreatedDate) {
        this.holdingsCreatedDate = holdingsCreatedDate;
    }

    /**
     * Gets holdings last updated by.
     *
     * @return the holdings last updated by
     */
    public String getHoldingsLastUpdatedBy() {
        return holdingsLastUpdatedBy;
    }

    /**
     * Sets holdings last updated by.
     *
     * @param holdingsLastUpdatedBy the holdings last updated by
     */
    public void setHoldingsLastUpdatedBy(String holdingsLastUpdatedBy) {
        this.holdingsLastUpdatedBy = holdingsLastUpdatedBy;
    }

    /**
     * Gets holdings last updated date.
     *
     * @return the holdings last updated date
     */
    public Date getHoldingsLastUpdatedDate() {
        return holdingsLastUpdatedDate;
    }

    /**
     * Sets holdings last updated date.
     *
     * @param holdingsLastUpdatedDate the holdings last updated date
     */
    public void setHoldingsLastUpdatedDate(Date holdingsLastUpdatedDate) {
        this.holdingsLastUpdatedDate = holdingsLastUpdatedDate;
    }

    /**
     * Is deleted holdings boolean.
     *
     * @return the boolean
     */
    public boolean isDeletedHoldings() {
        return isDeletedHoldings;
    }

    /**
     * Sets deleted holdings.
     *
     * @param deletedHoldings the deleted holdings
     */
    public void setDeletedHoldings(boolean deletedHoldings) {
        isDeletedHoldings = deletedHoldings;
    }
}
