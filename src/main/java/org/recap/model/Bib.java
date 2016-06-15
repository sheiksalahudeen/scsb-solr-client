package org.recap.model;


import org.apache.solr.client.solrj.beans.Field;

/**
 * Created by pvsubrah on 6/11/16.
 */

public class Bib {
    @Field
    private String id;

    @Field("Barcode")
    private String barcode;


//    @Field
//    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    //    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
}
