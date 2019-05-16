package com.github.rjbx.calibrater;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test methods for the {@link Calibrater} class.
 * Equals assertions are more precise as delta parameter approaches zero.
 */
public class CalibraterTest {
    
    private static int PRECISION = Calibrater.STANDARD_PRECISION;
    private static double ERROR = Math.pow(10, -PRECISION);

    /**
     * Asserts whether incrementing and decrementing a {@code double} array element resets all element values.
     */
    @Test public final void testShiftDoubleArrayResetSingle() {

        double[] percents = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude <= .1d; magnitude += 0.01d) {
            for (int index = 0; index < 4; index++) {
                Calibrater.shiftRatings(percents, index, magnitude, PRECISION);
                Calibrater.shiftRatings(percents, index, magnitude * -1d, PRECISION);
            }
            assertEquals(.25d, percents[0], ERROR);
            assertEquals(.25d, percents[1], ERROR);
            assertEquals(.25d, percents[2], ERROR);
            assertEquals(.25d, percents[3], ERROR);
        }
    }

    /**
     * Asserts whether incrementing and decrementing through the range of possible values for
     * two adjacent {@code double} arrays assigns the expected value to each array element.
     */
    @Test public final void testShiftDoubleArrayRepeatRange() {

        int multiplier = 0;
        double[] percents = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude <= .1d; magnitude += 0.01d) {
            for (int index = 0; index < percents.length; index++) {
                while (percents[index] <= .99d) {
                    Calibrater.shiftRatings(percents, index, magnitude, PRECISION);
                    multiplier++;
                }
                while (percents[index] >= .01d) {
                    Calibrater.shiftRatings(percents, index, magnitude * -1d, PRECISION);
                    multiplier++;
                }
                int nextIndex = index == 3 ? 0 : index + 1;
                while (percents[index] >= .01d) {
                    Calibrater.shiftRatings(percents, nextIndex, magnitude * -1d, PRECISION);
                    multiplier++;
                }
                while (percents[nextIndex] <= .99d) {
                    Calibrater.shiftRatings(percents, nextIndex, magnitude, PRECISION);
                    multiplier++;
                }
            }

            double error = ERROR + (ERROR * multiplier);

            assertEquals(1d, percents[0], error);
            assertEquals(0d, percents[1], error);
            assertEquals(0d, percents[2], error);
            assertEquals(0d, percents[3], error);

            double sum = 0d;
            Calibrater.recalibrateRatings(percents, false, PRECISION);
            for (double percent : percents) sum += percent;
            assertEquals(1d, sum, PRECISION);
        }
    }

    /**
     * Asserts whether incrementing and decrementing through the range of possible values for
     * two adjacent {@code double} arrays assigns the expected value to each array element.
     */
    @Test public final void testShiftDoubleArrayResetSingleRepeatRange() {

        double[] percents = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            for (int index = 0; index < 4; index++) {
                double sum;


                for (int i = 0; percents[index] < 1d; i++) {
                    Calibrater.shiftRatings(percents, index, magnitude / 2, PRECISION);
                    sum = 0;
                    for (double percent : percents) sum += percent;
                    double error = ERROR + (ERROR * i++);
                    assertEquals(1d, sum, error);
                }
                Calibrater.shiftRatings(percents, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, magnitude / 2, PRECISION);
                assertEquals(1d, percents[index], ERROR);
                sum = 0;
                for (int i = 0; i < percents.length; i++) {
                    sum += percents[i];
                    if (i == percents.length - 1) assertEquals(1d, sum, ERROR);
                }

                while (percents[index] > 0d) {
                    Calibrater.shiftRatings(percents, index, -magnitude / 2, PRECISION);
                    sum = 0;
                    for (int i = 0; i < percents.length; i++) {
                        sum += percents[i];
                        if (i == percents.length - 1) assertEquals(1d, sum, ERROR);
                    }
                }

                Calibrater.shiftRatings(percents, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, -magnitude / 2, PRECISION);
                Calibrater.shiftRatings(percents, index, -magnitude / 2, PRECISION);
                assertEquals(0d, percents[index], ERROR);
                sum = 0;
                for (int i = 0; i < percents.length; i++) {
                    sum += percents[i];
                    if (i == percents.length - 1) assertEquals(1d, sum, ERROR);
                }

                assertFalse(Calibrater.resetRatings(percents, false, PRECISION));
            }
        }
    }

    /**
     * Asserts whether shifting {@code double} array elements to offset an element
     * within a certain margin past the lower limit does not over allocate the unused offset.
     */
    @Test public final void testShiftDoubleArrayLimitAllocation() {

        for (double magnitude = 0.001d; magnitude < 1d; magnitude *= 10) {
            double[] percents = { 1d, 0d, 0d, 0d, 0d };
            Calibrater.shiftRatings(percents, 1, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 1, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 2, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 2, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 3, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 3, magnitude, PRECISION);
            Calibrater.shiftRatings(percents, 4, magnitude, PRECISION);

            while (percents[3] > magnitude / 2)
                Calibrater.shiftRatings(percents, 1, magnitude, PRECISION);

            double sum = 0;
            for (double percent : percents) sum += percent;
            double error = ERROR + (ERROR * percents.length);
            assertEquals(1d, sum, error);
        }
    }

    /**
     * Asserts whether shifting a {@code double} array with different magnitudes returns
     * the expected boolean value.
     */
    @Test public final void testShiftDoubleArrayReturnValue() {

        double[] percents = {0d, 0d, 0d, 0d};
        assertFalse(Calibrater.shiftRatings(percents, 0, -0.01d, PRECISION));
        assertFalse(Calibrater.shiftRatings(percents, 0, 0d, PRECISION));

        percents[0] = 1d;
        assertFalse(Calibrater.shiftRatings(percents, 0, 0.02d, PRECISION));
        assertFalse(Calibrater.shiftRatings(percents, 0, 0d, PRECISION));

        for (int i = 0; i < percents.length; i++) percents[i] = .5d;
        assertTrue(Calibrater.shiftRatings(percents, 0, -0.1d, PRECISION));
        assertTrue(Calibrater.shiftRatings(percents, 0, 0.1d, PRECISION));

        for (int i = 0; i < percents.length; i++) percents[i] = .1d;
        assertTrue(Calibrater.shiftRatings(percents, 0, -0.2d, PRECISION));
        assertTrue(Calibrater.shiftRatings(percents, 0, 0.02d, PRECISION));
    }
    
    /**
     * Asserts whether resetting a {@code double} array assigns the expected value to each array element
     * and results in the expected sum of all elements.
     */
    @Test public final void testResetDoubleArrayEqualDistribution() {

        for (int i = 1; i < 20; i++) {
            double[] percents = new double[i];
            for (int j = 0; j < percents.length; j++) percents[j] = 0d;
            Calibrater.resetRatings(percents, false, PRECISION);
            assertEquals(1d / i, percents[i - 1], 0d);
            double sum = 0d;
            for (Double percent : percents) sum += percent;
            assertEquals(1d, sum, PRECISION);
        }
    }

    /**
     * Asserts whether resetting {@code double} arrays with different element values returns
     * the expected boolean value.
     */
    @Test public final void testResetDoubleArrayReturnValue() {

        double[] percents = {1d, 0d, 0d, 0d};
        for (int i = 0; i < 2; i++) {
            assertFalse(Calibrater.resetRatings(percents, false, PRECISION));
            percents[0] -= .25d;
            percents[3 - i] +=.25d;
        }
        
        percents[3] = .5d;
        assertTrue(Calibrater.resetRatings(percents, false, PRECISION));

        for (int i = 0; i < percents.length - 1; i++) percents[i] = 0d;
        assertTrue(Calibrater.resetRatings(percents, false, PRECISION));

        percents[3] = 0d;
        assertTrue(Calibrater.resetRatings(percents, false, PRECISION));
    }

    /**
     * Asserts whether shifting a {@code double} array with invalid magnitudes and precisions throws
     * the expected exception.
     */
    @Test public final void testShiftWithIllegalArguments() {

        double[] percents = {0d, 0d, 0d, 0d};

        assertShiftThrows(IllegalArgumentException.class, percents, 0, 1.0001d, PRECISION);
        assertShiftThrows(IllegalArgumentException.class, percents, 0, -1.0001d, PRECISION);
        assertShiftThrows(IllegalArgumentException.class, percents, 0, 0d, 17);
        assertShiftThrows(IllegalArgumentException.class, percents, 0, 0d, -1);
    }

    /**
     * Asserts whether the parameter {@link Class} of type {@link Throwable} is thrown when
     * invoking {@link Calibrater#shiftRatings(double[], int, double, int)} with the parameter args
     */
    private static <T extends Throwable>void assertShiftThrows(Class<T> throwableClass, Object... args) {

        try {

            Calibrater.shiftRatings(
                    (double[]) args[0], (int) args[1], (double) args[2], (int) args[3]
            ); fail(); // Exception not thrown

        } catch (Throwable e){ assertTrue(throwableClass.isInstance(e)); }
    }
}