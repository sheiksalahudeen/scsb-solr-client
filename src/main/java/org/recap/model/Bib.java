package org.recap.model;


import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by pvsubrah on 6/11/16.
 */
public class Bib {

    @Field
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
