package org.recap.model;


import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class Bib {

    @Field
    private Long id;

    @Field
    private Date createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
