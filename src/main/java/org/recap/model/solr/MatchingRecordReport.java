package org.recap.model.solr;

/**
 * Created by angelind on 1/7/16.
 */
public class MatchingRecordReport {

    private String matchPointTag;
    private String matchPointContent;
    private String bibId;
    private String title;
    private String barcode;
    private String institutionId;
    private String useRestrictions;

    public String getMatchPointTag() {
        return matchPointTag;
    }

    public void setMatchPointTag(String matchPointTag) {
        this.matchPointTag = matchPointTag;
    }

    public String getMatchPointContent() {
        return matchPointContent;
    }

    public void setMatchPointContent(String matchPointContent) {
        this.matchPointContent = matchPointContent;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getUseRestrictions() {
        return useRestrictions;
    }

    public void setUseRestrictions(String useRestrictions) {
        this.useRestrictions = useRestrictions;
    }
}
