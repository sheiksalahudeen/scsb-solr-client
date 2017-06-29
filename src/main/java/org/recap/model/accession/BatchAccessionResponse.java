package org.recap.model.accession;

/**
 * Created by sheiks on 15/06/17.
 */
public class BatchAccessionResponse {
    private int requestedRecords;
    private int successRecords;
    private int dummyRecords;
    private int duplicateRecords;
    private int emptyBarcodes;
    private int emptyOwningInst;
    private int alreadyAccessioned;
    private int exception;
    private int failure;
    private int invalidLenghBarcode;
    private String timeElapsed;

    public int getRequestedRecords() {
        return requestedRecords;
    }

    public void setRequestedRecords(int requestedRecords) {
        this.requestedRecords = requestedRecords;
    }

    public int getSuccessRecords() {
        return successRecords;
    }

    public void setSuccessRecords(int successRecords) {
        this.successRecords = successRecords;
    }

    public int getDummyRecords() {
        return dummyRecords;
    }

    public void setDummyRecords(int dummyRecords) {
        this.dummyRecords = dummyRecords;
    }

    public int getDuplicateRecords() {
        return duplicateRecords;
    }

    public void setDuplicateRecords(int duplicateRecords) {
        this.duplicateRecords = duplicateRecords;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getEmptyBarcodes() {
        return emptyBarcodes;
    }

    public void setEmptyBarcodes(int emptyBarcodes) {
        this.emptyBarcodes = emptyBarcodes;
    }

    public int getEmptyOwningInst() {
        return emptyOwningInst;
    }

    public void setEmptyOwningInst(int emptyOwningInst) {
        this.emptyOwningInst = emptyOwningInst;
    }

    public int getAlreadyAccessioned() {
        return alreadyAccessioned;
    }

    public void setAlreadyAccessioned(int alreadyAccessioned) {
        this.alreadyAccessioned = alreadyAccessioned;
    }

    public int getException() {
        return exception;
    }

    public void setException(int exception) {
        this.exception = exception;
    }

    public int getInvalidLenghBarcode() {
        return invalidLenghBarcode;
    }

    public void setInvalidLenghBarcode(int invalidLenghBarcode) {
        this.invalidLenghBarcode = invalidLenghBarcode;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void addDummyRecords(int dummyRecords) {
        this.dummyRecords += dummyRecords;
    }

    public void addEmptyBarcodes(int emptyBarcodes) {
        this.emptyBarcodes += emptyBarcodes;
    }

    public void addEmptyOwningInst(int emptyOwningInst) {
        this.emptyOwningInst += emptyOwningInst;
    }

    public void addAlreadyAccessioned(int alreadyAccessioned) {
        this.alreadyAccessioned += alreadyAccessioned;
    }

    public void addException(int exception) {
        this.exception += exception;
    }

    public void addInvalidLenghBarcode(int invalidLenghBarcode) {
        this.invalidLenghBarcode += invalidLenghBarcode;
    }

    public void addSuccessRecord(int successRecords) {
        this.successRecords += successRecords;
    }
    public void addFailure(int failure) {
        this.failure += failure;
    }

    @Override
    public String toString() {
        return "BatchAccessionResponse{" +
                "requestedRecords=" + requestedRecords +
                ", successRecords=" + successRecords +
                ", dummyRecords=" + dummyRecords +
                ", duplicateRecords=" + duplicateRecords +
                ", emptyBarcodes=" + emptyBarcodes +
                ", emptyOwningInst=" + emptyOwningInst +
                ", alreadyAccessioned=" + alreadyAccessioned +
                ", exception=" + exception +
                ", failure=" + failure +
                ", invalidLenghBarcode=" + invalidLenghBarcode +
                ", timeElapsed='" + timeElapsed + '\'' +
                '}';
    }
}
