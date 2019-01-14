package com.github.rjbx.calibrater;

/**
 * Utility class for adjusting and calibrating percentage arrays.
 */
public class Calibrater {

    /**
     * Increments or decrements a {@link Float} array element by the specified magnitude while calibrating
     * other {@link Float} array elements to maintain proportionality.
     * @param percentages {@link Float} array elements to be adjusted if not proportionate
     * @param adjustedIndex index of the array element to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be no more than 1 or -1
     * @return true if percentage was adjusted and false otherwise
     */
    public static boolean shiftRatings(Double[] percentages, Integer adjustedIndex, double magnitude) {

        if (Math.abs(magnitude) > 1d || Math.abs(magnitude) == 0d || percentages.length < 2
        || (percentages[adjustedIndex] == 0d && magnitude < 0d) || (percentages[adjustedIndex] == 1d && magnitude > 0d))
            return false; // magnitudes or percentages outside adjustable limits
        percentages[adjustedIndex] += magnitude;
        if (percentages[adjustedIndex] >= 1d) { // adjusted percentage is whole so rest must be zero
            percentages[adjustedIndex] = 1d;
            for (int i = 0; i < percentages.length; i++) if (adjustedIndex != i) percentages[i] = 0d;
        } else {
            double offset = magnitude * -1;
            if (percentages[adjustedIndex] <= 0d) {
                percentages[adjustedIndex] = 0d; // set to limit
                offset += percentages[adjustedIndex]; // restore unallocated offset
            }
            int excluded = 1; // prevent further allocation after maxing out all elements
            double limit = magnitude > 0d ? 0d : 1d; // limit approached by offset percentages
            double error = magnitude / 10000d;
            while (Math.abs(offset) >= Math.abs(error) && excluded <= percentages.length) { // offset expended or exclusions maxed
                double allocation = (offset / (percentages.length - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < percentages.length; i++) {
                    if (i != adjustedIndex && (percentages[i] != 0d || magnitude < 0d)) { // ignore adjusted and exclude only once
                        percentages[i] += allocation;
                        offset -= allocation; // expend allocated for recalculating offset on iterations
                        if (percentages[i] + error  < limit * -1) { // below limit within margin of error
                            if (percentages[i] < 0d) offset += (percentages[i] + error); // restore unallocated offset
                            percentages[i] = limit; // set to limit
                            excluded++; // decrease offset divisor for fewer allocations
                        }
                    }
                }
            }
        } return true;
    }

    /**
     * Reads {@link Float} array and assigns proportionate values to each {@link Float} array element.
     * @param percentages {@link Float} array elements to be calibrated if not proportionate
     * @return true if percentage was calibrated and false otherwise
     */
    public static boolean resetRatings(Double[] percentages, Boolean forceReset) {
        float sum = 0f;
        for (Double percentage : percentages) sum += percentage;
        if (sum > 1.001f || sum < 0.999f || forceReset) { // elements are not proportionate
            for (int i = 0; i < percentages.length; i++) percentages[i] = (1d / percentages.length);
            return true;
        } else return false;
    }

    public static boolean shiftRatings(double[] percentages, int adjustedIndex, double magnitude) {
        Double[] boxedPercentages = new Double[percentages.length];
        int i = 0; for (double percentage : percentages)  boxedPercentages[i++] = percentage;
        return shiftRatings(boxedPercentages, adjustedIndex, magnitude);
    }

    public static boolean resetRatings(double[] percentages, boolean forceReset) {
        Double[] boxedPercentages = new Double[percentages.length];
        int i = 0; for (double percentage : percentages)  boxedPercentages[i++] = percentage;
        return resetRatings(boxedPercentages, forceReset);
    }
}