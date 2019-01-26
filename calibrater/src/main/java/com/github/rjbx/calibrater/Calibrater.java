package com.github.rjbx.calibrater;

/**
 * Utility class for adjusting and calibrating percent arrays.
 */
public class Calibrater {

    public static final int STANDARD_PRECISION = 5;
    public static final double STANDARD_MAGNITUDE = .01d;
    
    /**
     * Increments or decrements a {@code double} array element by the specified magnitude while calibrating
     * other {@code double} array elements to maintain proportionality.
     * @param percents {@code double} array elements to be adjusted if not proportionate
     * @param targetIndex index of the array element to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be no more than 1 or -1
     * @return true if percent was adjusted and false otherwise
     */
    public static boolean shiftRatings(double[] percents, int targetIndex, double magnitude, int precision) {

        if (precision > 16 || precision < 0 || magnitude > 1d || magnitude < -1d)
            throw new IllegalArgumentException("Parameter value is out of bounds");
        
        if (magnitude == 0 || percents.length < 2) 
            return false; // nothing to adjust
        
        double targetPercent = percents[targetIndex];
        if ((targetPercent == 0d && magnitude < 0d)
        || (targetPercent == 1d && magnitude > 0d))
            return false; // percent outside adjustable limits
        
        targetPercent += magnitude;
        if (targetPercent >= 1d) { // adjusted percent is whole so rest must be zero
            percents[targetIndex] = 1d;
            for (int i = 0; i < percents.length; i++) if (targetIndex != i) percents[i] = 0d;
        } else {

            double offset = magnitude * -1;
            if (targetPercent <= 0d) {
                percents[targetIndex] = 0d; // set to limit
                offset += targetPercent; // restore unallocated offset
            }

            int excluded = 1; // prevent further allocation after maxing out all elements
            double limit = magnitude > 0d ? 0d : 1d; // limit approached by offset percents
            double error = 1d / Math.pow(10, precision);

            while (Math.abs(offset) >= Math.abs(error) && excluded <= percents.length) { // offset expended or exclusions maxed
                double allocation = (offset / (percents.length - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < percents.length; i++) {
                    if (i != targetIndex && (percents[i] != 0d || magnitude < 0d)) { // ignore adjusted and exclude only once
                        percents[i] += allocation;
                        double indexedPercent = percents[i];
                        offset -= allocation; // expend allocated for recalculating offset on iterations
                        if (indexedPercent + error  < limit * -1) { // below limit within margin of error
                            if (indexedPercent < 0d) offset += (indexedPercent + error); // restore unallocated offset
                            percents[i] = limit; // set to limit
                            excluded++; // decrease offset divisor for fewer allocations
                        }
                    }
                }
            }
        } return true;
    }

    /**
     * Reads {@code double} array and assigns proportionate values to each {@code double} array element.
     * @param percents {@code double} array elements to be calibrated if not proportionate
     * @return true if percent was calibrated and false otherwise
     */
    public static boolean resetRatings(double[] percents, boolean forceReset) {
        double sum = 0d;
        for (double percent : percents) sum += percent;
        if (sum > 1.001d || sum < 0.999d || forceReset) { // elements are not proportionate
            for (int i = 0; i < percents.length; i++) percents[i] = (1d / percents.length);
            return true;
        } else return false;
    }
}