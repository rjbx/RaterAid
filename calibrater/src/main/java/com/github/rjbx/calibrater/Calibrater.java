package com.github.rjbx.calibrater;

/**
 * Utility class for adjusting and calibrating percent arrays.
 */
public final class Calibrater {

    public static final int STANDARD_PRECISION = 4;
    public static final double STANDARD_MAGNITUDE = .01d;
    
    /**
     * Increments or decrements a {@code double} array element by the specified magnitude while calibrating
     * other {@code double} array elements to maintain proportionality.
     * @param percents {@code double} array elements to be adjusted if not proportionate
     * @param targetIndex index of the array element to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be no more than 1 or -1
     * @param precision number of decimal places to move the allowed error from the whole
     * @return true if percent was adjusted and false otherwise
     */
    public static boolean shiftRatings(double[] percents, int targetIndex, double magnitude, int precision) {

        if (precision > 16 || precision < 0 || magnitude > 1d || magnitude < -1d) {
            throw new IllegalArgumentException("Parameter value is out of bounds");
        }

        if (magnitude == 0 || percents.length < 2) {
            return false; // nothing to adjust
        }

        if ((percents[targetIndex] == 0d && magnitude < 0d)
        || (percents[targetIndex] == 1d && magnitude > 0d)) {
            return false; // percent outside adjustable limits
        }

        percents[targetIndex] += magnitude;
        if (percents[targetIndex] >= 1d) { // adjusted percent is whole so rest must be zero
            percents[targetIndex] = 1d;
            for (int i = 0; i < percents.length; i++) if (targetIndex != i) percents[i] = 0d;
        } else {

            magnitude *= -1;
            if (percents[targetIndex] <= 0d) {
                magnitude += percents[targetIndex]; // restore unallocated offset
                percents[targetIndex] = 0d; // set to limit
            }

            int excluded = 1; // prevent further allocation after maxing out all elements
            double limit = magnitude < 0d ? 0d : 1d; // limit approached by offset percents
            double error = Math.pow(10, -precision);

            while (Math.abs(magnitude) >= Math.abs(error) && excluded <= percents.length) { // offset expended or exclusions maxed
                double allocation = (magnitude / (percents.length - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < percents.length; i++) {
                    if (i != targetIndex && (percents[i] != 0d || magnitude > 0d)) { // ignore adjusted and exclude only once
                        percents[i] += allocation;
                        magnitude -= allocation; // expend allocated for recalculating offset on iterations
                        if (percents[i] + error  < limit * -1) { // below limit within margin of error
                            if (percents[i] < 0d) magnitude += percents[i]; // restore unallocated offset
                            percents[i] = limit; // set to limit
                            excluded++; // decrease offset divisor for fewer allocations
                        }
                    }
                }
            }

        } return true;
    }

    /**
     * Reads {@code double} array and assigns equivalent percents to each {@code double} array element.
     * @param percents {@code double} array elements to be reset if not equivalent
     * @param precision number of decimal places to move the allowed error from the whole
     * @return true if array was reset and false otherwise
     */
    public static boolean resetRatings(double[] percents, boolean forceReset, Integer precision) {
        double sum = 0d;
        for (double percent : percents) sum += percent;
        double error = precision != null ? Math.pow(10, -precision) : 0;
        if (sum > 1d + error || sum < 1d - error || forceReset) { // elements are not proportionate
            for (int i = 0; i < percents.length; i++) percents[i] = (1d / percents.length);
            return true;
        } else return false;
    }

    /**
     * Reads {@code double} array and assigns equivalent percents to each {@code double} array element.
     * @param percents {@code double} array elements to be calibrated if not proportionate
     */
    public static boolean removeRating(double[] percents, int index) {

        if (percents[index] == -1d) return false;

        int lastIndex = -1;
        for (int i = 0; i < percents.length; i++) if (percents[i] == -1d) lastIndex = i - 1;

        if (lastIndex != -1 && index > lastIndex) return false;

        System.arraycopy(percents, index + 1, percents, index, percents.length - index - 1 );
        percents[percents.length - 1] = -1d;

        for (int i = 0; i < lastIndex; i++) percents[i] = 1d / (lastIndex + 1);

        return true;
    }

    /**
     * Reads {@code double} array and equally distributes to each array element the difference
     * between the whole and the sum of all array elements.
     * @param percents {@code double} array to be calibrated closer to the whole
     */
    public static boolean recalibrateRatings(double[] percents, boolean forceReset, Integer precision) {
        double sum = 0d;
        for (double percent : percents) sum += percent;
        double difference = (1d - sum) / percents.length;
        double error = precision != null ? Math.pow(10, -precision) : 0;
        if (sum > 1d + error || sum < 1d - error || forceReset) { // elements are not proportionate
            for (int i = 0; i < percents.length; i++) {
                percents[i] += difference;
                if (percents[1] > 1d) percents[i] = 1d;
                else if (percents[i] < 0d) percents[i] = 0d;
                return true;
            }
        } return false;
    }
}