package org.recap.model.reports;

import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/1/17.
 */
public class ReportsResponse {

    private long accessionPrivatePulCount;
    private long accessionPrivateCulCount;
    private long accessionPrivateNyplCount;
    private long accessionSharedPulCount;
    private long accessionSharedCulCount;
    private long accessionSharedNyplCount;
    private long accessionOpenPulCount;
    private long accessionOpenCulCount;
    private long accessionOpenNyplCount;

    private long deaccessionPrivatePulCount;
    private long deaccessionPrivateCulCount;
    private long deaccessionPrivateNyplCount;
    private long deaccessionSharedPulCount;
    private long deaccessionSharedCulCount;
    private long deaccessionSharedNyplCount;
    private long deaccessionOpenPulCount;
    private long deaccessionOpenCulCount;
    private long deaccessionOpenNyplCount;

    private long openPulCgdCount;
    private long openCulCgdCount;
    private long openNyplCgdCount;
    private long sharedPulCgdCount;
    private long sharedCulCgdCount;
    private long sharedNyplCgdCount;
    private long privatePulCgdCount;
    private long privateCulCgdCount;
    private long privateNyplCgdCount;

    private String totalRecordsCount = "0";
    private Integer totalPageCount = 0;
    private String message;
    private List<DeaccessionItemResultsRow> deaccessionItemResultsRows = new ArrayList<>();

    //IncompleteRecordsReport
    private String incompleteTotalRecordsCount = "0";
    private Integer incompleteTotalPageCount = 0;
    private Integer incompletePageNumber = 0;
    private Integer incompletePageSize = 10;
    private List<IncompleteReportResultsRow> incompleteReportResultsRows = new ArrayList<>();

    /**
     * Gets accession private pul count.
     *
     * @return the accession private pul count
     */
    public long getAccessionPrivatePulCount() {
        return accessionPrivatePulCount;
    }

    /**
     * Sets accession private pul count.
     *
     * @param accessionPrivatePulCount the accession private pul count
     */
    public void setAccessionPrivatePulCount(long accessionPrivatePulCount) {
        this.accessionPrivatePulCount = accessionPrivatePulCount;
    }

    /**
     * Gets accession private cul count.
     *
     * @return the accession private cul count
     */
    public long getAccessionPrivateCulCount() {
        return accessionPrivateCulCount;
    }

    /**
     * Sets accession private cul count.
     *
     * @param accessionPrivateCulCount the accession private cul count
     */
    public void setAccessionPrivateCulCount(long accessionPrivateCulCount) {
        this.accessionPrivateCulCount = accessionPrivateCulCount;
    }

    /**
     * Gets accession private nypl count.
     *
     * @return the accession private nypl count
     */
    public long getAccessionPrivateNyplCount() {
        return accessionPrivateNyplCount;
    }

    /**
     * Sets accession private nypl count.
     *
     * @param accessionPrivateNyplCount the accession private nypl count
     */
    public void setAccessionPrivateNyplCount(long accessionPrivateNyplCount) {
        this.accessionPrivateNyplCount = accessionPrivateNyplCount;
    }

    /**
     * Gets accession shared pul count.
     *
     * @return the accession shared pul count
     */
    public long getAccessionSharedPulCount() {
        return accessionSharedPulCount;
    }

    /**
     * Sets accession shared pul count.
     *
     * @param accessionSharedPulCount the accession shared pul count
     */
    public void setAccessionSharedPulCount(long accessionSharedPulCount) {
        this.accessionSharedPulCount = accessionSharedPulCount;
    }

    /**
     * Gets accession shared cul count.
     *
     * @return the accession shared cul count
     */
    public long getAccessionSharedCulCount() {
        return accessionSharedCulCount;
    }

    /**
     * Sets accession shared cul count.
     *
     * @param accessionSharedCulCount the accession shared cul count
     */
    public void setAccessionSharedCulCount(long accessionSharedCulCount) {
        this.accessionSharedCulCount = accessionSharedCulCount;
    }

    /**
     * Gets accession shared nypl count.
     *
     * @return the accession shared nypl count
     */
    public long getAccessionSharedNyplCount() {
        return accessionSharedNyplCount;
    }

    /**
     * Sets accession shared nypl count.
     *
     * @param accessionSharedNyplCount the accession shared nypl count
     */
    public void setAccessionSharedNyplCount(long accessionSharedNyplCount) {
        this.accessionSharedNyplCount = accessionSharedNyplCount;
    }

    /**
     * Gets accession open pul count.
     *
     * @return the accession open pul count
     */
    public long getAccessionOpenPulCount() {
        return accessionOpenPulCount;
    }

    /**
     * Sets accession open pul count.
     *
     * @param accessionOpenPulCount the accession open pul count
     */
    public void setAccessionOpenPulCount(long accessionOpenPulCount) {
        this.accessionOpenPulCount = accessionOpenPulCount;
    }

    /**
     * Gets accession open cul count.
     *
     * @return the accession open cul count
     */
    public long getAccessionOpenCulCount() {
        return accessionOpenCulCount;
    }

    /**
     * Sets accession open cul count.
     *
     * @param accessionOpenCulCount the accession open cul count
     */
    public void setAccessionOpenCulCount(long accessionOpenCulCount) {
        this.accessionOpenCulCount = accessionOpenCulCount;
    }

    /**
     * Gets accession open nypl count.
     *
     * @return the accession open nypl count
     */
    public long getAccessionOpenNyplCount() {
        return accessionOpenNyplCount;
    }

    /**
     * Sets accession open nypl count.
     *
     * @param accessionOpenNyplCount the accession open nypl count
     */
    public void setAccessionOpenNyplCount(long accessionOpenNyplCount) {
        this.accessionOpenNyplCount = accessionOpenNyplCount;
    }

    /**
     * Gets deaccession private pul count.
     *
     * @return the deaccession private pul count
     */
    public long getDeaccessionPrivatePulCount() {
        return deaccessionPrivatePulCount;
    }

    /**
     * Sets deaccession private pul count.
     *
     * @param deaccessionPrivatePulCount the deaccession private pul count
     */
    public void setDeaccessionPrivatePulCount(long deaccessionPrivatePulCount) {
        this.deaccessionPrivatePulCount = deaccessionPrivatePulCount;
    }

    /**
     * Gets deaccession private cul count.
     *
     * @return the deaccession private cul count
     */
    public long getDeaccessionPrivateCulCount() {
        return deaccessionPrivateCulCount;
    }

    /**
     * Sets deaccession private cul count.
     *
     * @param deaccessionPrivateCulCount the deaccession private cul count
     */
    public void setDeaccessionPrivateCulCount(long deaccessionPrivateCulCount) {
        this.deaccessionPrivateCulCount = deaccessionPrivateCulCount;
    }

    /**
     * Gets deaccession private nypl count.
     *
     * @return the deaccession private nypl count
     */
    public long getDeaccessionPrivateNyplCount() {
        return deaccessionPrivateNyplCount;
    }

    /**
     * Sets deaccession private nypl count.
     *
     * @param deaccessionPrivateNyplCount the deaccession private nypl count
     */
    public void setDeaccessionPrivateNyplCount(long deaccessionPrivateNyplCount) {
        this.deaccessionPrivateNyplCount = deaccessionPrivateNyplCount;
    }

    /**
     * Gets deaccession shared pul count.
     *
     * @return the deaccession shared pul count
     */
    public long getDeaccessionSharedPulCount() {
        return deaccessionSharedPulCount;
    }

    /**
     * Sets deaccession shared pul count.
     *
     * @param deaccessionSharedPulCount the deaccession shared pul count
     */
    public void setDeaccessionSharedPulCount(long deaccessionSharedPulCount) {
        this.deaccessionSharedPulCount = deaccessionSharedPulCount;
    }

    /**
     * Gets deaccession shared cul count.
     *
     * @return the deaccession shared cul count
     */
    public long getDeaccessionSharedCulCount() {
        return deaccessionSharedCulCount;
    }

    /**
     * Sets deaccession shared cul count.
     *
     * @param deaccessionSharedCulCount the deaccession shared cul count
     */
    public void setDeaccessionSharedCulCount(long deaccessionSharedCulCount) {
        this.deaccessionSharedCulCount = deaccessionSharedCulCount;
    }

    /**
     * Gets deaccession shared nypl count.
     *
     * @return the deaccession shared nypl count
     */
    public long getDeaccessionSharedNyplCount() {
        return deaccessionSharedNyplCount;
    }

    /**
     * Sets deaccession shared nypl count.
     *
     * @param deaccessionSharedNyplCount the deaccession shared nypl count
     */
    public void setDeaccessionSharedNyplCount(long deaccessionSharedNyplCount) {
        this.deaccessionSharedNyplCount = deaccessionSharedNyplCount;
    }

    /**
     * Gets deaccession open pul count.
     *
     * @return the deaccession open pul count
     */
    public long getDeaccessionOpenPulCount() {
        return deaccessionOpenPulCount;
    }

    /**
     * Sets deaccession open pul count.
     *
     * @param deaccessionOpenPulCount the deaccession open pul count
     */
    public void setDeaccessionOpenPulCount(long deaccessionOpenPulCount) {
        this.deaccessionOpenPulCount = deaccessionOpenPulCount;
    }

    /**
     * Gets deaccession open cul count.
     *
     * @return the deaccession open cul count
     */
    public long getDeaccessionOpenCulCount() {
        return deaccessionOpenCulCount;
    }

    /**
     * Sets deaccession open cul count.
     *
     * @param deaccessionOpenCulCount the deaccession open cul count
     */
    public void setDeaccessionOpenCulCount(long deaccessionOpenCulCount) {
        this.deaccessionOpenCulCount = deaccessionOpenCulCount;
    }

    /**
     * Gets deaccession open nypl count.
     *
     * @return the deaccession open nypl count
     */
    public long getDeaccessionOpenNyplCount() {
        return deaccessionOpenNyplCount;
    }

    /**
     * Sets deaccession open nypl count.
     *
     * @param deaccessionOpenNyplCount the deaccession open nypl count
     */
    public void setDeaccessionOpenNyplCount(long deaccessionOpenNyplCount) {
        this.deaccessionOpenNyplCount = deaccessionOpenNyplCount;
    }

    /**
     * Gets open pul cgd count.
     *
     * @return the open pul cgd count
     */
    public long getOpenPulCgdCount() {
        return openPulCgdCount;
    }

    /**
     * Sets open pul cgd count.
     *
     * @param openPulCgdCount the open pul cgd count
     */
    public void setOpenPulCgdCount(long openPulCgdCount) {
        this.openPulCgdCount = openPulCgdCount;
    }

    /**
     * Gets open cul cgd count.
     *
     * @return the open cul cgd count
     */
    public long getOpenCulCgdCount() {
        return openCulCgdCount;
    }

    /**
     * Sets open cul cgd count.
     *
     * @param openCulCgdCount the open cul cgd count
     */
    public void setOpenCulCgdCount(long openCulCgdCount) {
        this.openCulCgdCount = openCulCgdCount;
    }

    /**
     * Gets open nypl cgd count.
     *
     * @return the open nypl cgd count
     */
    public long getOpenNyplCgdCount() {
        return openNyplCgdCount;
    }

    /**
     * Sets open nypl cgd count.
     *
     * @param openNyplCgdCount the open nypl cgd count
     */
    public void setOpenNyplCgdCount(long openNyplCgdCount) {
        this.openNyplCgdCount = openNyplCgdCount;
    }

    /**
     * Gets shared pul cgd count.
     *
     * @return the shared pul cgd count
     */
    public long getSharedPulCgdCount() {
        return sharedPulCgdCount;
    }

    /**
     * Sets shared pul cgd count.
     *
     * @param sharedPulCgdCount the shared pul cgd count
     */
    public void setSharedPulCgdCount(long sharedPulCgdCount) {
        this.sharedPulCgdCount = sharedPulCgdCount;
    }

    /**
     * Gets shared cul cgd count.
     *
     * @return the shared cul cgd count
     */
    public long getSharedCulCgdCount() {
        return sharedCulCgdCount;
    }

    /**
     * Sets shared cul cgd count.
     *
     * @param sharedCulCgdCount the shared cul cgd count
     */
    public void setSharedCulCgdCount(long sharedCulCgdCount) {
        this.sharedCulCgdCount = sharedCulCgdCount;
    }

    /**
     * Gets shared nypl cgd count.
     *
     * @return the shared nypl cgd count
     */
    public long getSharedNyplCgdCount() {
        return sharedNyplCgdCount;
    }

    /**
     * Sets shared nypl cgd count.
     *
     * @param sharedNyplCgdCount the shared nypl cgd count
     */
    public void setSharedNyplCgdCount(long sharedNyplCgdCount) {
        this.sharedNyplCgdCount = sharedNyplCgdCount;
    }

    /**
     * Gets private pul cgd count.
     *
     * @return the private pul cgd count
     */
    public long getPrivatePulCgdCount() {
        return privatePulCgdCount;
    }

    /**
     * Sets private pul cgd count.
     *
     * @param privatePulCgdCount the private pul cgd count
     */
    public void setPrivatePulCgdCount(long privatePulCgdCount) {
        this.privatePulCgdCount = privatePulCgdCount;
    }

    /**
     * Gets private cul cgd count.
     *
     * @return the private cul cgd count
     */
    public long getPrivateCulCgdCount() {
        return privateCulCgdCount;
    }

    /**
     * Sets private cul cgd count.
     *
     * @param privateCulCgdCount the private cul cgd count
     */
    public void setPrivateCulCgdCount(long privateCulCgdCount) {
        this.privateCulCgdCount = privateCulCgdCount;
    }

    /**
     * Gets private nypl cgd count.
     *
     * @return the private nypl cgd count
     */
    public long getPrivateNyplCgdCount() {
        return privateNyplCgdCount;
    }

    /**
     * Sets private nypl cgd count.
     *
     * @param privateNyplCgdCount the private nypl cgd count
     */
    public void setPrivateNyplCgdCount(long privateNyplCgdCount) {
        this.privateNyplCgdCount = privateNyplCgdCount;
    }

    /**
     * Gets total records count.
     *
     * @return the total records count
     */
    public String getTotalRecordsCount() {
        return totalRecordsCount;
    }

    /**
     * Sets total records count.
     *
     * @param totalRecordsCount the total records count
     */
    public void setTotalRecordsCount(String totalRecordsCount) {
        this.totalRecordsCount = totalRecordsCount;
    }

    /**
     * Gets total page count.
     *
     * @return the total page count
     */
    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    /**
     * Sets total page count.
     *
     * @param totalPageCount the total page count
     */
    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets deaccession item results rows.
     *
     * @return the deaccession item results rows
     */
    public List<DeaccessionItemResultsRow> getDeaccessionItemResultsRows() {
        return deaccessionItemResultsRows;
    }

    /**
     * Sets deaccession item results rows.
     *
     * @param deaccessionItemResultsRows the deaccession item results rows
     */
    public void setDeaccessionItemResultsRows(List<DeaccessionItemResultsRow> deaccessionItemResultsRows) {
        this.deaccessionItemResultsRows = deaccessionItemResultsRows;
    }

    /**
     * Gets incomplete total records count.
     *
     * @return the incomplete total records count
     */
    public String getIncompleteTotalRecordsCount() {
        return incompleteTotalRecordsCount;
    }

    /**
     * Sets incomplete total records count.
     *
     * @param incompleteTotalRecordsCount the incomplete total records count
     */
    public void setIncompleteTotalRecordsCount(String incompleteTotalRecordsCount) {
        this.incompleteTotalRecordsCount = incompleteTotalRecordsCount;
    }

    /**
     * Gets incomplete total page count.
     *
     * @return the incomplete total page count
     */
    public Integer getIncompleteTotalPageCount() {
        return incompleteTotalPageCount;
    }

    /**
     * Sets incomplete total page count.
     *
     * @param incompleteTotalPageCount the incomplete total page count
     */
    public void setIncompleteTotalPageCount(Integer incompleteTotalPageCount) {
        this.incompleteTotalPageCount = incompleteTotalPageCount;
    }

    /**
     * Gets incomplete report results rows.
     *
     * @return the incomplete report results rows
     */
    public List<IncompleteReportResultsRow> getIncompleteReportResultsRows() {
        return incompleteReportResultsRows;
    }

    /**
     * Sets incomplete report results rows.
     *
     * @param incompleteReportResultsRows the incomplete report results rows
     */
    public void setIncompleteReportResultsRows(List<IncompleteReportResultsRow> incompleteReportResultsRows) {
        this.incompleteReportResultsRows = incompleteReportResultsRows;
    }

    /**
     * Gets incomplete page number.
     *
     * @return the incomplete page number
     */
    public Integer getIncompletePageNumber() {
        return incompletePageNumber;
    }

    /**
     * Sets incomplete page number.
     *
     * @param incompletePageNumber the incomplete page number
     */
    public void setIncompletePageNumber(Integer incompletePageNumber) {
        this.incompletePageNumber = incompletePageNumber;
    }

    /**
     * Gets incomplete page size.
     *
     * @return the incomplete page size
     */
    public Integer getIncompletePageSize() {
        return incompletePageSize;
    }

    /**
     * Sets incomplete page size.
     *
     * @param incompletePageSize the incomplete page size
     */
    public void setIncompletePageSize(Integer incompletePageSize) {
        this.incompletePageSize = incompletePageSize;
    }
}
