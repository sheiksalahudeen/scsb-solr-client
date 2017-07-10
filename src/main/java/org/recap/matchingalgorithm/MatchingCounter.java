package org.recap.matchingalgorithm;

/**
 * Created by angelind on 6/1/17.
 */
public class MatchingCounter {

    private static Integer pulSharedCount;
    private static Integer culSharedCount;
    private static Integer nyplSharedCount;

    private static Integer pulOpenCount;
    private static Integer culOpenCount;
    private static Integer nyplOpenCount;

    private static Integer pulCGDUpdatedSharedCount;
    private static Integer culCGDUpdatedSharedCount;
    private static Integer nyplCGDUpdatedSharedCount;

    private static Integer pulCGDUpdatedOpenCount;
    private static Integer culCGDUpdatedOpenCount;
    private static Integer nyplCGDUpdatedOpenCount;

    /**
     * This method gets pul shared count.
     *
     * @return the pul shared count
     */
    public static synchronized Integer getPulSharedCount() {
        return pulSharedCount;
    }

    /**
     * This method sets pul shared count.
     *
     * @param pulSharedCount the pul shared count
     */
    public static synchronized void setPulSharedCount(Integer pulSharedCount) {
        MatchingCounter.pulSharedCount = pulSharedCount;
    }

    /**
     * This method gets cul shared count.
     *
     * @return the cul shared count
     */
    public static synchronized Integer getCulSharedCount() {
        return culSharedCount;
    }

    /**
     * This method sets cul shared count.
     *
     * @param culSharedCount the cul shared count
     */
    public static synchronized void setCulSharedCount(Integer culSharedCount) {
        MatchingCounter.culSharedCount = culSharedCount;
    }

    /**
     * This method gets nypl shared count.
     *
     * @return the nypl shared count
     */
    public static synchronized Integer getNyplSharedCount() {
        return nyplSharedCount;
    }

    /**
     * This method sets nypl shared count.
     *
     * @param nyplSharedCount the nypl shared count
     */
    public static synchronized void setNyplSharedCount(Integer nyplSharedCount) {
        MatchingCounter.nyplSharedCount = nyplSharedCount;
    }

    /**
     * This method gets pul cgd updated shared count.
     *
     * @return the pul cgd updated shared count
     */
    public static synchronized Integer getPulCGDUpdatedSharedCount() {
        return pulCGDUpdatedSharedCount;
    }

    /**
     * This method sets pul cgd updated shared count.
     *
     * @param pulCGDUpdatedSharedCount the pul cgd updated shared count
     */
    public static synchronized void setPulCGDUpdatedSharedCount(Integer pulCGDUpdatedSharedCount) {
        MatchingCounter.pulCGDUpdatedSharedCount = pulCGDUpdatedSharedCount;
    }

    /**
     * This method gets cul cgd updated shared count.
     *
     * @return the cul cgd updated shared count
     */
    public static synchronized Integer getCulCGDUpdatedSharedCount() {
        return culCGDUpdatedSharedCount;
    }

    /**
     * This method sets cul cgd updated shared count.
     *
     * @param culCGDUpdatedSharedCount the cul cgd updated shared count
     */
    public static synchronized void setCulCGDUpdatedSharedCount(Integer culCGDUpdatedSharedCount) {
        MatchingCounter.culCGDUpdatedSharedCount = culCGDUpdatedSharedCount;
    }

    /**
     * This method gets nypl cgd updated shared count.
     *
     * @return the nypl cgd updated shared count
     */
    public static synchronized Integer getNyplCGDUpdatedSharedCount() {
        return nyplCGDUpdatedSharedCount;
    }

    /**
     * This method sets nypl cgd updated shared count.
     *
     * @param nyplCGDUpdatedSharedCount the nypl cgd updated shared count
     */
    public static synchronized void setNyplCGDUpdatedSharedCount(Integer nyplCGDUpdatedSharedCount) {
        MatchingCounter.nyplCGDUpdatedSharedCount = nyplCGDUpdatedSharedCount;
    }

    /**
     * This method gets pul cgd updated open count.
     *
     * @return the pul cgd updated open count
     */
    public static synchronized Integer getPulCGDUpdatedOpenCount() {
        return pulCGDUpdatedOpenCount;
    }

    /**
     * This method sets pul cgd updated open count.
     *
     * @param pulCGDUpdatedOpenCount the pul cgd updated open count
     */
    public static synchronized void setPulCGDUpdatedOpenCount(Integer pulCGDUpdatedOpenCount) {
        MatchingCounter.pulCGDUpdatedOpenCount = pulCGDUpdatedOpenCount;
    }

    /**
     * This method gets cul cgd updated open count.
     *
     * @return the cul cgd updated open count
     */
    public static synchronized Integer getCulCGDUpdatedOpenCount() {
        return culCGDUpdatedOpenCount;
    }

    /**
     * This method sets cul cgd updated open count.
     *
     * @param culCGDUpdatedOpenCount the cul cgd updated open count
     */
    public static synchronized void setCulCGDUpdatedOpenCount(Integer culCGDUpdatedOpenCount) {
        MatchingCounter.culCGDUpdatedOpenCount = culCGDUpdatedOpenCount;
    }

    /**
     * This method gets nypl cgd updated open count.
     *
     * @return the nypl cgd updated open count
     */
    public static synchronized Integer getNyplCGDUpdatedOpenCount() {
        return nyplCGDUpdatedOpenCount;
    }

    /**
     * This method sets nypl cgd updated open count.
     *
     * @param nyplCGDUpdatedOpenCount the nypl cgd updated open count
     */
    public static synchronized void setNyplCGDUpdatedOpenCount(Integer nyplCGDUpdatedOpenCount) {
        MatchingCounter.nyplCGDUpdatedOpenCount = nyplCGDUpdatedOpenCount;
    }

    /**
     * Gets pul open count.
     *
     * @return the pul open count
     */
    public static synchronized Integer getPulOpenCount() {
        return pulOpenCount;
    }

    /**
     * Sets pul open count.
     *
     * @param pulOpenCount the pul open count
     */
    public static synchronized void setPulOpenCount(Integer pulOpenCount) {
        MatchingCounter.pulOpenCount = pulOpenCount;
    }

    /**
     * Gets cul open count.
     *
     * @return the cul open count
     */
    public static synchronized Integer getCulOpenCount() {
        return culOpenCount;
    }

    /**
     * Sets cul open count.
     *
     * @param culOpenCount the cul open count
     */
    public static synchronized void setCulOpenCount(Integer culOpenCount) {
        MatchingCounter.culOpenCount = culOpenCount;
    }

    /**
     * Gets nypl open count.
     *
     * @return the nypl open count
     */
    public static synchronized Integer getNyplOpenCount() {
        return nyplOpenCount;
    }

    /**
     * Sets nypl open count.
     *
     * @param nyplOpenCount the nypl open count
     */
    public static synchronized void setNyplOpenCount(Integer nyplOpenCount) {
        MatchingCounter.nyplOpenCount = nyplOpenCount;
    }

    /**
     * Reset.
     */
    public static void reset() {
        pulSharedCount = 0;
        pulOpenCount = 0;
        culSharedCount = 0;
        culOpenCount = 0;
        nyplSharedCount = 0;
        nyplOpenCount = 0;
        pulCGDUpdatedSharedCount = 0;
        culCGDUpdatedSharedCount = 0;
        nyplCGDUpdatedSharedCount = 0;
        pulCGDUpdatedOpenCount = 0;
        culCGDUpdatedOpenCount = 0;
        nyplCGDUpdatedOpenCount = 0;
    }

    /**
     * This method is used to update counter.
     *
     * @param owningInstitution the owning institution
     * @param isOpen            the is open
     */
    public static synchronized void updateCounter(Integer owningInstitution, boolean isOpen) {
        if (owningInstitution == 1) {
            if(isOpen) {
                pulCGDUpdatedOpenCount++;
                pulOpenCount++;
                pulSharedCount--;
            } else {
                pulCGDUpdatedSharedCount++;
            }
        } else if (owningInstitution == 2) {
            if(isOpen) {
                culCGDUpdatedOpenCount++;
                culOpenCount++;
                culSharedCount--;
            } else {
                culCGDUpdatedSharedCount++;
            }
        } else if (owningInstitution == 3) {
            if(isOpen) {
                nyplCGDUpdatedOpenCount++;
                nyplOpenCount++;
                nyplSharedCount--;
            } else {
                nyplCGDUpdatedSharedCount++;
            }
        }
    }
}
