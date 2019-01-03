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
    public static boolean shiftRatings(Float[] percentages, Integer adjustedIndex, Float magnitude) {

        if (Math.abs(magnitude) > 1f || Math.abs(magnitude) == 0f || percentages.length < 2
        || (percentages[adjustedIndex] == 0f && magnitude < 0f) || (percentages[adjustedIndex] == 1f && magnitude > 0f))
            return false; // magnitudes or percentages outside adjustable limits
        percentages[adjustedIndex] += magnitude;
        if (percentages[adjustedIndex] >= 1f) { // adjusted percentage is whole so rest must be zero
            percentages[adjustedIndex] = 1f;
            for (int i = 0; i < percentages.length; i++) if (adjustedIndex != i) percentages[i] = 0f;
        } else {
            float offset = magnitude * -1;
            if (percentages[adjustedIndex] <= 0f) {
                percentages[adjustedIndex] = 0f; // set to limit
                offset += percentages[adjustedIndex]; // restore unallocated offset
            }
            int excluded = 1; // prevent further allocation after maxing out all elements
            float limit = magnitude > 0f ? 0f : 1f; // limit approached by offset percentages
            float error = magnitude / 10000f;
            while (Math.abs(offset) >= Math.abs(error) && excluded <= percentages.length) { // offset expended or exclusions maxed
                float allocation = (offset / (percentages.length - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < percentages.length; i++) {
                    if (i != adjustedIndex && (percentages[i] != 0f || magnitude < 0f)) { // ignore adjusted and exclude only once
                        percentages[i] += allocation;
                        offset -= allocation; // expend allocated for recalculating offset on iterations
                        if (percentages[i] + error  < limit * -1) { // below limit within margin of error
                            if (percentages[i] < 0f) offset += (percentages[i] + error); // restore unallocated offset
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
    public static boolean resetRatings(Float[] percentages, boolean forceReset) {
        float sum = 0f;
        for (Float percentage : percentages) sum += percentage;
        if (sum > 1.001f || sum < 0.999f || forceReset) { // elements are not proportionate
            for (int i = 0; i < percentages.length; i++) percentages[i] = (1f / percentages.length);
            return true;
        } else return false;
    }
}