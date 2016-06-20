package org.recap.model.solr;

/**
 * Created by SheikS on 6/18/2016.
 */
public class SolrIndexRequest {
    private Integer numberOfThread;
    private Integer numberOfDoc;

    public Integer getNumberOfThread() {
        return numberOfThread;
    }

    public void setNumberOfThread(Integer numberOfThread) {
        this.numberOfThread = numberOfThread;
    }

    public Integer getNumberOfDoc() {
        return numberOfDoc;
    }

    public void setNumberOfDoc(Integer numberOfDoc) {
        this.numberOfDoc = numberOfDoc;
    }
}
