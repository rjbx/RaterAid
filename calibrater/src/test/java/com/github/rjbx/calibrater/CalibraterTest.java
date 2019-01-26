package com.github.rjbx.calibrater;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test methods for the {@link Calibrater} class.
 * Equals assertions are more precise as delta parameter approaches zero.
 */
public class CalibraterTest {
    
    int PRECISION = Calibrater.STANDARD_PRECISION;

    /**
     * Verifies that incrementing and decrementing a {@code double} array element resets all element values.
     */
    @Test public final void testAdjustDoubleArrayResetSingle() {

        double[] percentages = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude <= .1d; magnitude += 0.01d) {
            for (int index = 0; index < 4; index++) {
                Calibrater.shiftRatings(percentages, index, magnitude, PRECISION);
                Calibrater.shiftRatings(percentages, index, magnitude * -1d, PRECISION);
            }
            assertEquals(.25d, percentages[0], magnitude / 20000);
            assertEquals(.25d, percentages[1], magnitude / 20000);
            assertEquals(.25d, percentages[2], magnitude / 20000);
            assertEquals(.25d, percentages[3], magnitude / 20000);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@code double} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustDoubleArrayRepeatRange() {

        double[] percentages = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude <= .1d; magnitude += 0.01d) {
            for (int index = 0; index < percentages.length; index++) {
                while (percentages[index] <= .99d) {
                    Calibrater.shiftRatings(percentages, index, magnitude, PRECISION);
                }
                while (percentages[index] >= .01d) {
                    Calibrater.shiftRatings(percentages, index, magnitude * -1d, PRECISION);
                }
                int nextIndex = index == 3 ? 0 : index + 1;
                while (percentages[index] >= .01d) {
                    Calibrater.shiftRatings(percentages, nextIndex, magnitude * -1, PRECISION);
                }
                while (percentages[nextIndex] <= .99d) {
                    Calibrater.shiftRatings(percentages, nextIndex, magnitude, PRECISION);
                }
            }
            assertEquals(1d, percentages[0], magnitude / 2);
            assertEquals(0f, percentages[1], magnitude / 2);
            assertEquals(0f, percentages[2], magnitude / 2);
            assertEquals(0f, percentages[3], magnitude / 2);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@code double} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustDoubleArrayResetSingleRepeatRange() {

        double[] percentages = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            for (int index = 0; index < 4; index++) {
                double sum;

                while (percentages[index] < 1d) {
                    Calibrater.shiftRatings(percentages, index, magnitude / 2, PRECISION);
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1d, sum, magnitude / 2);
                    }
                }
                Calibrater.shiftRatings(percentages, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, magnitude / 2, PRECISION);
                assertEquals(1d, percentages[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1d, sum, magnitude / 2);
                }

                while (percentages[index] > 0f) {
                    Calibrater.shiftRatings(percentages, index, -magnitude / 2, PRECISION);
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1d, sum, magnitude / 2);
                    }
                }
                Calibrater.shiftRatings(percentages, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2, PRECISION);
                assertEquals(0f, percentages[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1d, sum, magnitude / 2);
                }

                Calibrater.resetRatings(percentages, false);
            }
        }
    }

    /**
     * Verifies that adjusting {@code double} array elements to offset an element
     * within a certain margin past the lower limit does not over allocate the unused offset.
     */
    @Test public final void testAdjustDoubleArrayLimitAllocation() {

        for (double magnitude = 0.001d; magnitude < 1d; magnitude *= 10) {
            double[] percentages = { 1d, 0f, 0f, 0f, 0f };
            Calibrater.shiftRatings(percentages, 1, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 1, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 2, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 2, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 3, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 3, magnitude, PRECISION);
            Calibrater.shiftRatings(percentages, 4, magnitude, PRECISION);

            while (percentages[3] > magnitude / 2)
                Calibrater.shiftRatings(percentages, 1, magnitude, PRECISION);

            double sum = 0;
            for (double percentage : percentages) sum += percentage;
            assertEquals(1d, sum, magnitude / 2);
        }
    }

    /**
     * Verifies that adjusting a {@code double} array with invalid and valid magnitudes returns
     * the expected boolean value.
     */
    @Test public final void testAdjustDoubleArrayReturnValue() {

        double[] percentages = {0f, 0f, 0f, 0f};
        assertFalse(Calibrater.shiftRatings(percentages, 0, -0.01d, PRECISION));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0f, PRECISION));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 1.0001d, PRECISION));

        percentages[0] = 1d;
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0.02d, PRECISION));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0f, PRECISION));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 2d, PRECISION));

        for (int i = 0; i < percentages.length; i++) percentages[i] = .5d;
        assertTrue(Calibrater.shiftRatings(percentages, 0, -0.1d, PRECISION));
        assertTrue(Calibrater.shiftRatings(percentages, 0, 0.1d, PRECISION));

        for (int i = 0; i < percentages.length; i++) percentages[i] = .1d;
        assertTrue(Calibrater.shiftRatings(percentages, 0, -0.2d, PRECISION));
        assertTrue(Calibrater.shiftRatings(percentages, 0, 0.02d, PRECISION));
    }

    /**
     * Verifies that calibrating a {@code double} array assigns the expected value to each array element
     * and results in the expected sum of all elements.
     */
    @Test public final void testCalibrateDoubleArrayEqualDistribution() {

        double magnitude = 1d;
        for (int i = 1; i < 20; i++) {
            double[] percentages = new double[i];
            for (int j = 0; j < percentages.length; j++) percentages[j] = 0f;
            Calibrater.resetRatings(percentages, false);
            assertEquals(1d / i, percentages[i - 1], 0f);
            double sum = 0f;
            for (Double percentage : percentages) sum += percentage;
            assertEquals(1d, sum, magnitude / 200000);
        }
    }

    /**
     * Verifies that calibrating {@code double} arrays with different element values returns
     * the expected boolean value.
     */
    @Test public final void testCalibrateDoubleArrayReturnValue() {

        double[] percentages = {1d, 0f, 0f, 0f};
        for (int i = 0; i < 2; i++) {
            assertFalse(Calibrater.resetRatings(percentages, false));
            percentages[0] -= .25d;
            percentages[3 - i] +=.25d;
        }
        
        percentages[3] = .5d;
        assertTrue(Calibrater.resetRatings(percentages, false));

        for (int i = 0; i < percentages.length - 1; i++) percentages[i] = 0f;
        assertTrue(Calibrater.resetRatings(percentages, false));

        percentages[3] = 0f;
        assertTrue(Calibrater.resetRatings(percentages, false));
    }
}