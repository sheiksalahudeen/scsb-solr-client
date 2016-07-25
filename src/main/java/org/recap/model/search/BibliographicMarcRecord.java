package org.recap.model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
public class BibliographicMarcRecord {

    private Integer bibId;
    private String title;
    private String author;
    private String publisher;
    private String publishedDate;
    private String owningInstitution;
    private String tag000;
    private String controlNumber001;
    private String controlNumber005;
    private String controlNumber008;
    private String content;
    private List<BibDataField> bibDataFields = new ArrayList<>();

    public Integer getBibId() {
        return bibId;
    }

    public void setBibId(Integer bibId) {
        this.bibId = bibId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getOwningInstitution() {
        return owningInstitution;
    }

    public void setOwningInstitution(String owningInstitution) {
        this.owningInstitution = owningInstitution;
    }

    public String getTag000() {
        return tag000;
    }

    public void setTag000(String tag000) {
        this.tag000 = tag000;
    }

    public String getControlNumber001() {
        return controlNumber001;
    }

    public void setControlNumber001(String controlNumber001) {
        this.controlNumber001 = controlNumber001;
    }

    public String getControlNumber005() {
        return controlNumber005;
    }

    public void setControlNumber005(String controlNumber005) {
        this.controlNumber005 = controlNumber005;
    }

    public String getControlNumber008() {
        return controlNumber008;
    }

    public void setControlNumber008(String controlNumber008) {
        this.controlNumber008 = controlNumber008;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<BibDataField> getBibDataFields() {
        return bibDataFields;
    }

    public void setBibDataFields(List<BibDataField> bibDataFields) {
        this.bibDataFields = bibDataFields;
    }
}
