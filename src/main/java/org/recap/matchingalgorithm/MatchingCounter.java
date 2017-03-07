package org.recap.matchingalgorithm;

/**
 * Created by angelind on 6/1/17.
 */
public class MatchingCounter {

    private static Integer pulSharedCount;
    private static Integer culSharedCount;
    private static Integer nyplSharedCount;

    private static Integer pulCGDUpdatedSharedCount;
    private static Integer culCGDUpdatedSharedCount;
    private static Integer nyplCGDUpdatedSharedCount;

    private static Integer pulCGDUpdatedOpenCount;
    private static Integer culCGDUpdatedOpenCount;
    private static Integer nyplCGDUpdatedOpenCount;

    public static synchronized Integer getPulSharedCount() {
        return pulSharedCount;
    }

    public static synchronized void setPulSharedCount(Integer pulSharedCount) {
        MatchingCounter.pulSharedCount = pulSharedCount;
    }

    public static synchronized Integer getCulSharedCount() {
        return culSharedCount;
    }

    public static synchronized void setCulSharedCount(Integer culSharedCount) {
        MatchingCounter.culSharedCount = culSharedCount;
    }

    public static synchronized Integer getNyplSharedCount() {
        return nyplSharedCount;
    }

    public static synchronized void setNyplSharedCount(Integer nyplSharedCount) {
        MatchingCounter.nyplSharedCount = nyplSharedCount;
    }

    public static synchronized Integer getPulCGDUpdatedSharedCount() {
        return pulCGDUpdatedSharedCount;
    }

    public static synchronized void setPulCGDUpdatedSharedCount(Integer pulCGDUpdatedSharedCount) {
        MatchingCounter.pulCGDUpdatedSharedCount = pulCGDUpdatedSharedCount;
    }

    public static synchronized Integer getCulCGDUpdatedSharedCount() {
        return culCGDUpdatedSharedCount;
    }

    public static synchronized void setCulCGDUpdatedSharedCount(Integer culCGDUpdatedSharedCount) {
        MatchingCounter.culCGDUpdatedSharedCount = culCGDUpdatedSharedCount;
    }

    public static synchronized Integer getNyplCGDUpdatedSharedCount() {
        return nyplCGDUpdatedSharedCount;
    }

    public static synchronized void setNyplCGDUpdatedSharedCount(Integer nyplCGDUpdatedSharedCount) {
        MatchingCounter.nyplCGDUpdatedSharedCount = nyplCGDUpdatedSharedCount;
    }

    public static synchronized Integer getPulCGDUpdatedOpenCount() {
        return pulCGDUpdatedOpenCount;
    }

    public static synchronized void setPulCGDUpdatedOpenCount(Integer pulCGDUpdatedOpenCount) {
        MatchingCounter.pulCGDUpdatedOpenCount = pulCGDUpdatedOpenCount;
    }

    public static synchronized Integer getCulCGDUpdatedOpenCount() {
        return culCGDUpdatedOpenCount;
    }

    public static synchronized void setCulCGDUpdatedOpenCount(Integer culCGDUpdatedOpenCount) {
        MatchingCounter.culCGDUpdatedOpenCount = culCGDUpdatedOpenCount;
    }

    public static synchronized Integer getNyplCGDUpdatedOpenCount() {
        return nyplCGDUpdatedOpenCount;
    }

    public static synchronized void setNyplCGDUpdatedOpenCount(Integer nyplCGDUpdatedOpenCount) {
        MatchingCounter.nyplCGDUpdatedOpenCount = nyplCGDUpdatedOpenCount;
    }

    public static void reset() {
        pulSharedCount = 0;
        culSharedCount = 0;
        nyplSharedCount = 0;
        pulCGDUpdatedSharedCount = 0;
        culCGDUpdatedSharedCount = 0;
        nyplCGDUpdatedSharedCount = 0;
        pulCGDUpdatedOpenCount = 0;
        culCGDUpdatedOpenCount = 0;
        nyplCGDUpdatedOpenCount = 0;
    }

    public static synchronized void updateCounter(Integer owningInstitution, boolean isOpen) {
        if (owningInstitution == 1) {
            if(isOpen) {
                pulCGDUpdatedOpenCount++;
                pulSharedCount--;
            } else {
                pulCGDUpdatedSharedCount++;
            }
        } else if (owningInstitution == 2) {
            if(isOpen) {
                culCGDUpdatedOpenCount++;
                culSharedCount--;
            } else {
                culCGDUpdatedSharedCount++;
            }
        } else if (owningInstitution == 3) {
            if(isOpen) {
                nyplCGDUpdatedOpenCount++;
                nyplSharedCount--;
            } else {
                nyplCGDUpdatedSharedCount++;
            }
        }
    }
}
