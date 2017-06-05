package org.recap.model.search;

/**
 * Created by rajeshbabuk on 25/7/16.
 */
public class BibDataField {

    private String dataFieldTag;
    private char indicator1;
    private char indicator2;
    private String dataFieldValue;

    /**
     * Gets data field tag.
     *
     * @return the data field tag
     */
    public String getDataFieldTag() {
        return dataFieldTag;
    }

    /**
     * Sets data field tag.
     *
     * @param dataFieldTag the data field tag
     */
    public void setDataFieldTag(String dataFieldTag) {
        this.dataFieldTag = dataFieldTag;
    }

    /**
     * Gets indicator 1.
     *
     * @return the indicator 1
     */
    public char getIndicator1() {
        return indicator1;
    }

    /**
     * Sets indicator 1.
     *
     * @param indicator1 the indicator 1
     */
    public void setIndicator1(char indicator1) {
        this.indicator1 = indicator1;
    }

    /**
     * Gets indicator 2.
     *
     * @return the indicator 2
     */
    public char getIndicator2() {
        return indicator2;
    }

    /**
     * Sets indicator 2.
     *
     * @param indicator2 the indicator 2
     */
    public void setIndicator2(char indicator2) {
        this.indicator2 = indicator2;
    }

    /**
     * Gets data field value.
     *
     * @return the data field value
     */
    public String getDataFieldValue() {
        return dataFieldValue;
    }

    /**
     * Sets data field value.
     *
     * @param dataFieldValue the data field value
     */
    public void setDataFieldValue(String dataFieldValue) {
        this.dataFieldValue = dataFieldValue;
    }
}
