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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHoldingsId() {
        return holdingsId;
    }

    public void setHoldingsId(Integer holdingsId) {
        this.holdingsId = holdingsId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getSummaryHoldings() {
        return summaryHoldings;
    }

    public void setSummaryHoldings(String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getHoldingsCreatedBy() {
        return holdingsCreatedBy;
    }

    public void setHoldingsCreatedBy(String holdingsCreatedBy) {
        this.holdingsCreatedBy = holdingsCreatedBy;
    }

    public Date getHoldingsCreatedDate() {
        return holdingsCreatedDate;
    }

    public void setHoldingsCreatedDate(Date holdingsCreatedDate) {
        this.holdingsCreatedDate = holdingsCreatedDate;
    }

    public String getHoldingsLastUpdatedBy() {
        return holdingsLastUpdatedBy;
    }

    public void setHoldingsLastUpdatedBy(String holdingsLastUpdatedBy) {
        this.holdingsLastUpdatedBy = holdingsLastUpdatedBy;
    }

    public Date getHoldingsLastUpdatedDate() {
        return holdingsLastUpdatedDate;
    }

    public void setHoldingsLastUpdatedDate(Date holdingsLastUpdatedDate) {
        this.holdingsLastUpdatedDate = holdingsLastUpdatedDate;
    }

    public boolean isDeletedHoldings() {
        return isDeletedHoldings;
    }

    public void setDeletedHoldings(boolean deletedHoldings) {
        isDeletedHoldings = deletedHoldings;
    }
}
