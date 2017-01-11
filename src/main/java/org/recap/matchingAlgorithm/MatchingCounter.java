package org.recap.matchingAlgorithm;

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

    public synchronized static Integer getPulSharedCount() {
        return pulSharedCount;
    }

    public synchronized static void setPulSharedCount(Integer pulSharedCount) {
        MatchingCounter.pulSharedCount = pulSharedCount;
    }

    public synchronized static Integer getCulSharedCount() {
        return culSharedCount;
    }

    public synchronized static void setCulSharedCount(Integer culSharedCount) {
        MatchingCounter.culSharedCount = culSharedCount;
    }

    public synchronized static Integer getNyplSharedCount() {
        return nyplSharedCount;
    }

    public synchronized static void setNyplSharedCount(Integer nyplSharedCount) {
        MatchingCounter.nyplSharedCount = nyplSharedCount;
    }

    public synchronized static Integer getPulCGDUpdatedSharedCount() {
        return pulCGDUpdatedSharedCount;
    }

    public synchronized static void setPulCGDUpdatedSharedCount(Integer pulCGDUpdatedSharedCount) {
        MatchingCounter.pulCGDUpdatedSharedCount = pulCGDUpdatedSharedCount;
    }

    public synchronized static Integer getCulCGDUpdatedSharedCount() {
        return culCGDUpdatedSharedCount;
    }

    public synchronized static void setCulCGDUpdatedSharedCount(Integer culCGDUpdatedSharedCount) {
        MatchingCounter.culCGDUpdatedSharedCount = culCGDUpdatedSharedCount;
    }

    public synchronized static Integer getNyplCGDUpdatedSharedCount() {
        return nyplCGDUpdatedSharedCount;
    }

    public synchronized static void setNyplCGDUpdatedSharedCount(Integer nyplCGDUpdatedSharedCount) {
        MatchingCounter.nyplCGDUpdatedSharedCount = nyplCGDUpdatedSharedCount;
    }

    public synchronized static Integer getPulCGDUpdatedOpenCount() {
        return pulCGDUpdatedOpenCount;
    }

    public synchronized static void setPulCGDUpdatedOpenCount(Integer pulCGDUpdatedOpenCount) {
        MatchingCounter.pulCGDUpdatedOpenCount = pulCGDUpdatedOpenCount;
    }

    public synchronized static Integer getCulCGDUpdatedOpenCount() {
        return culCGDUpdatedOpenCount;
    }

    public synchronized static void setCulCGDUpdatedOpenCount(Integer culCGDUpdatedOpenCount) {
        MatchingCounter.culCGDUpdatedOpenCount = culCGDUpdatedOpenCount;
    }

    public synchronized static Integer getNyplCGDUpdatedOpenCount() {
        return nyplCGDUpdatedOpenCount;
    }

    public synchronized static void setNyplCGDUpdatedOpenCount(Integer nyplCGDUpdatedOpenCount) {
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

    public synchronized static void updateCounter(Integer owningInstitution, boolean isOpen) {
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
