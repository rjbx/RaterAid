package com.github.rjbx.proportions;

/**
 * Utility class for adjusting and calibrating proportion arrays.
 */
public class Proportions {

    /**
     * Increments or decrements a {@link Float} array element by the specified magnitude while calibrating
     * other {@link Float} array elements to maintain proportionality.
     * @param proportions {@link Float} array elements to be adjusted if not proportionate
     * @param adjustedIndex index of the array element to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be no more than 1 or -1
     * @return true if proportion was adjusted and false otherwise
     */
    public static boolean adjustFloatArray(Float[] proportions, Integer adjustedIndex, Float magnitude) {

        if (Math.abs(magnitude) > 1f || Math.abs(magnitude) == 0f || proportions.length < 2
        || (proportions[adjustedIndex] == 0f && magnitude < 0f) || (proportions[adjustedIndex] == 1f && magnitude > 0f))
            return false; // magnitudes or proportions outside adjustable limits
        proportions[adjustedIndex] += magnitude;
        if (proportions[adjustedIndex] >= 1f) { // adjusted proportion is whole so rest must be zero
            proportions[adjustedIndex] = 1f;
            for (int i = 0; i < proportions.length; i++) if (adjustedIndex != i) proportions[i] = 0f;
        } else {
            float offset = magnitude * -1;
            if (proportions[adjustedIndex] <= 0f) {
                proportions[adjustedIndex] = 0f; // set to limit
                offset += proportions[adjustedIndex]; // restore unallocated offset
            }
            int excluded = 1; // prevent further allocation after maxing out all elements
            float limit = magnitude > 0f ? 0f : 1f; // limit approached by offset proportions
            float error = magnitude / 10000f;
            while (Math.abs(offset) >= Math.abs(error) && excluded <= proportions.length) { // offset expended or exclusions maxed
                float allocation = (offset / (proportions.length - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < proportions.length; i++) {
                    if (i != adjustedIndex && (proportions[i] != 0f || magnitude < 0f)) { // ignore adjusted and exclude only once
                        proportions[i] += allocation;
                        offset -= allocation; // expend allocated for recalculating offset on iterations
                        if (proportions[i] + error  < limit * -1) { // below limit within margin of error
                            if (proportions[i] < 0f) offset += (proportions[i] + error); // restore unallocated offset
                            proportions[i] = limit; // set to limit
                            excluded++; // decrease offset divisor for fewer allocations
                        }
                    }
                }
            }
        } return true;
    }

    /**
     * Reads {@link Float} array and assigns proportionate values to each {@link Float} array element.
     * @param proportions {@link Float} array elements to be calibrated if not proportionate
     * @return true if proportion was calibrated and false otherwise
     */
    public static boolean calibrateFloatArray(Float[] proportions, boolean forceReset) {
        float sum = 0f;
        for (Float proportion : proportions) sum += proportion;
        if (sum > 1.001f || sum < 0.999f || forceReset) { // elements are not proportionate
            for (int i = 0; i < proportions.length; i++) proportions[i] = (1f / proportions.length);
            return true;
        } else return false;
    }
}