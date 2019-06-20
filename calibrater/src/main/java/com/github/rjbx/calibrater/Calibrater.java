package com.github.rjbx.calibrater;

import java.util.List;

/**
 * Utility class for adjusting and calibrating percent arrays.
 */
public final class Calibrater {

    public static final int STANDARD_PRECISION = 4;
    public static final double STANDARD_MAGNITUDE = .01d;
    
    /**
     * Increments or decrements a {@code List<Double>} element by the specified magnitude while calibrating
     * other {@code List<Double>} elements to maintain proportionality.
     * @param percents {@code List<Double>} elements to be adjusted if not proportionate
     * @param index location of the value to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be between 1 and -1
     * @param precision number of decimal places to move the allowed error from the whole
     * @return true if percent was adjusted and false otherwise
     */
    public static boolean shiftRatings(List<Double> percents, int index, double magnitude, int precision) {

        if (precision > 16 || precision < 0 || magnitude > 1d || magnitude < -1d) {
            throw new IllegalArgumentException("Parameter value is out of bounds");
        }

        if (magnitude == 0 || percents.size() < 2) {
            return false; // nothing to adjust
        }

        if ((percents.get(index) == 0d && magnitude < 0d)
        || (percents.get(index) == 1d && magnitude > 0d)) {
            return false; // percent outside adjustable limits
        }

        percents.set(index, percents.get(index) + magnitude);
        if (percents.get(index) >= 1d) { // adjusted percent is whole so rest must be zero
            percents.set(index, 1d);
            for (int i = 0; i < percents.size(); i++) if (index != i) percents.set(i, 0d);
        } else {

            magnitude *= -1;
            if (percents.get(index) <= 0d) {
                magnitude += percents.get(index); // restore unallocated offset
                percents.set(index, 0d); // set to limit
            }

            int excluded = 1; // prevent further allocation after maxing out all elements
            double limit = magnitude < 0d ? 0d : 1d; // limit approached by offset percents
            double error = Math.pow(10, -precision);

            while (Math.abs(magnitude) >= Math.abs(error) && excluded <= percents.size()) { // offset expended or exclusions maxed
                double allocation = (magnitude / (percents.size() - excluded)); // factor in exclusions on iterations
                for (int i = 0; i < percents.size(); i++) {
                    if (i != index && (percents.get(i) != 0d || magnitude > 0d)) { // ignore adjusted and exclude only once
                        percents.set(i, percents.get(i) + allocation);
                        magnitude -= allocation; // expend allocated for recalculating offset on iterations
                        if (percents.get(i) + error  < limit * -1) { // below limit within margin of error
                            if (percents.get(i) < 0d) magnitude += percents.get(i); // restore unallocated offset
                            percents.set(i, limit); // set to limit
                            excluded++; // decrease offset divisor for fewer allocations
                        }
                    } else if (percents.get(i) < 0) {
                        percents.set(i, 0d);
                        magnitude += percents.get(i);
                    }
                }
            }

        } return true;
    }

    /**
     * Reads {@code List<Double>} and assigns equivalent percents to each {@code List<Double>} element.
     * @param percents {@code List<Double>} elements to be reset if not equivalent
     * @param forceReset applies reset even if sum of array elements is as precise as specified
     * @param precision number of decimal places to move the permitted error from the whole
     * @return true if values were adjusted; false otherwise
     */
    public static boolean resetRatings(List<Double> percents, boolean forceReset, int precision) {
        double sum = 0d;
        for (double percent : percents) sum += percent;
        double error = Math.pow(10, -precision);
        if (sum > 1d + error || sum < 1d - error || forceReset) { // elements are not proportionate
            for (int i = 0; i < percents.size(); i++) percents.set(i, 1d / percents.size());
            return true;
        } else return false;
    }

    /**
     * As copying the primitive array creates a new reference that breaks the link to the bound values,
     * a negative value, which outscopes the index from calibration as negatives are not considered,
     * is assigned to the removed index which is moved to the last index of the parameter array reference.
     * The whole is then equally distributed among the remaining elements.
     * @param percents {@code List<Double>} elements to be calibrated if not proportionate
     * @param index location of the value to be removed
     * @param precision number of decimal places to move the permitted error from the whole
     * @return true if values were adjusted; false otherwise
     */
    public static boolean removeRating(List<Double> percents, int index, int precision) {
        percents.remove(index);
        return recalibrateRatings(percents, false, precision);
    }

    /**
     * Equally distributes to each {@code List<Double>} element the difference between
     * the whole and the sum of all array elements.
     * @param percents {@code List<Double>} to be calibrated closer to the whole
     * @param forceReset applies reset even if sum of array elements is as precise as specified
     * @param precision number of decimal places to move the permitted error from the whole
     * @return true if values were adjusted; false otherwise
     */
    public static boolean recalibrateRatings(List<Double> percents, boolean forceReset, int precision) {
        double sum = 0d;
        for (double percent : percents) sum += percent;
        double difference = (1d - sum) / percents.size();
        double error = Math.pow(10, -precision);
        if (sum > 1d + error || sum < 1d - error || forceReset) { // elements are not proportionate
            for (int i = 0; i < percents.size(); i++) {
                percents.set(i, percents.get(i) + difference);
                if (percents.get(i) > 1d) percents.set(i, 1d);
                else if (percents.get(i) < 0d) percents.set(i, 0d);
            }
            return true;
        } return false;
    }
}