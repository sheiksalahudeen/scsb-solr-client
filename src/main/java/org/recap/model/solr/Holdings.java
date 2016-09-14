package org.recap.model.solr;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

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

    @Field("OwningInstitution")
    private String owningInstitution;

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
}
