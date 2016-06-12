package org.recap.model;


import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class Bib {
    @Field
    private Long bibId;

    @Field
    private Date createDate;


    public Long getBibId() {
        return bibId;
    }

    public void setBibId(Long bibId) {
        this.bibId = bibId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
