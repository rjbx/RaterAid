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

    /**
     * Verifies that incrementing and decrementing a {@link Float} array element resets all element values.
     */
    @Test public final void testAdjustFloatArrayResetSingle() {

        Float[] percentages = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude <= .1f; magnitude += 0.01f) {
            for (int index = 0; index < 4; index++) {
                Calibrater.shiftRatings(percentages, index, magnitude);
                Calibrater.shiftRatings(percentages, index, magnitude * -1f);
            }
            assertEquals(.25f, percentages[0], magnitude / 20000);
            assertEquals(.25f, percentages[1], magnitude / 20000);
            assertEquals(.25f, percentages[2], magnitude / 20000);
            assertEquals(.25f, percentages[3], magnitude / 20000);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@link Float} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustFloatArrayRepeatRange() {

        Float[] percentages = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude <= .1f; magnitude += 0.01f) {
            for (int index = 0; index < percentages.length; index++) {
                while (percentages[index] <= .99f) {
                    Calibrater.shiftRatings(percentages, index, magnitude);
                }
                while (percentages[index] >= .01f) {
                    Calibrater.shiftRatings(percentages, index, magnitude * -1f);
                }
                int nextIndex = index == 3 ? 0 : index + 1;
                while (percentages[index] >= .01f) {
                    Calibrater.shiftRatings(percentages, nextIndex, magnitude * -1);
                }
                while (percentages[nextIndex] <= .99f) {
                    Calibrater.shiftRatings(percentages, nextIndex, magnitude);
                }
            }
            assertEquals(1f, percentages[0], magnitude / 2);
            assertEquals(0f, percentages[1], magnitude / 2);
            assertEquals(0f, percentages[2], magnitude / 2);
            assertEquals(0f, percentages[3], magnitude / 2);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@link Float} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustFloatArrayResetSingleRepeatRange() {

        Float[] percentages = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude < 0.1f; magnitude += 0.01f) {
            for (int index = 0; index < 4; index++) {
                float sum;

                while (percentages[index] < 1f) {
                    Calibrater.shiftRatings(percentages, index, magnitude / 2);
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1f, sum, magnitude / 2);
                    }
                }
                Calibrater.shiftRatings(percentages, index, -magnitude / 2);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2);
                Calibrater.shiftRatings(percentages, index, magnitude / 2);
                Calibrater.shiftRatings(percentages, index, magnitude / 2);
                assertEquals(1f, percentages[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1f, sum, magnitude / 2);
                }

                while (percentages[index] > 0f) {
                    Calibrater.shiftRatings(percentages, index, -magnitude / 2);
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1f, sum, magnitude / 2);
                    }
                }
                Calibrater.shiftRatings(percentages, index, magnitude / 2);
                Calibrater.shiftRatings(percentages, index, magnitude / 2);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2);
                Calibrater.shiftRatings(percentages, index, -magnitude / 2);
                assertEquals(0f, percentages[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1f, sum, magnitude / 2);
                }

                Calibrater.resetRatings(percentages, false);
            }
        }
    }

    /**
     * Verifies that adjusting {@link Float} array elements to offset an element
     * within a certain margin past the lower limit does not over allocate the unused offset.
     */
    @Test public final void testAdjustFloatArrayLimitAllocation() {

        for (float magnitude = 0.001f; magnitude < 1f; magnitude *= 10) {
            Float[] percentages = { 1f, 0f, 0f, 0f, 0f };
            Calibrater.shiftRatings(percentages, 1, magnitude);
            Calibrater.shiftRatings(percentages, 1, magnitude);
            Calibrater.shiftRatings(percentages, 2, magnitude);
            Calibrater.shiftRatings(percentages, 2, magnitude);
            Calibrater.shiftRatings(percentages, 3, magnitude);
            Calibrater.shiftRatings(percentages, 3, magnitude);
            Calibrater.shiftRatings(percentages, 4, magnitude);

            while (percentages[3] > magnitude / 2)
                Calibrater.shiftRatings(percentages, 1, magnitude);

            float sum = 0;
            for (float percentage : percentages) sum += percentage;
            assertEquals(1f, sum, magnitude / 2);
        }
    }

    /**
     * Verifies that adjusting a {@link Float} array with invalid and valid magnitudes returns
     * the expected boolean value.
     */
    @Test public final void testAdjustFloatArrayReturnValue() {

        Float[] percentages = {0f, 0f, 0f, 0f};
        assertFalse(Calibrater.shiftRatings(percentages, 0, -0.01f));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0f));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 1.0001f));

        percentages[0] = 1f;
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0.02f));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 0f));
        assertFalse(Calibrater.shiftRatings(percentages, 0, 2f));

        for (int i = 0; i < percentages.length; i++) percentages[i] = .5f;
        assertTrue(Calibrater.shiftRatings(percentages, 0, -0.1f));
        assertTrue(Calibrater.shiftRatings(percentages, 0, 0.1f));

        for (int i = 0; i < percentages.length; i++) percentages[i] = .1f;
        assertTrue(Calibrater.shiftRatings(percentages, 0, -0.2f));
        assertTrue(Calibrater.shiftRatings(percentages, 0, 0.02f));
    }

    /**
     * Verifies that calibrating a {@link Float} array assigns the expected value to each array element
     * and results in the expected sum of all elements.
     */
    @Test public final void testCalibrateFloatArrayEqualDistribution() {

        float magnitude = 1f;
        for (int i = 1; i < 20; i++) {
            Float[] percentages = new Float[i];
            for (int j = 0; j < percentages.length; j++) percentages[j] = 0f;
            Calibrater.resetRatings(percentages, false);
            assertEquals(1f / i, percentages[i - 1], 0f);
            float sum = 0f;
            for (Float percentage : percentages) sum += percentage;
            assertEquals(1f, sum, magnitude / 200000);
        }
    }

    /**
     * Verifies that calibrating {@link Float} arrays with different element values returns
     * the expected boolean value.
     */
    @Test public final void testCalibrateFloatArrayReturnValue() {

        Float[] percentages = {1f, 0f, 0f, 0f};
        for (int i = 0; i < 2; i++) {
            assertFalse(Calibrater.resetRatings(percentages, false));
            percentages[0] -= .25f;
            percentages[3 - i] +=.25f;
        }
        
        percentages[3] = .5f;
        assertTrue(Calibrater.resetRatings(percentages, false));

        for (int i = 0; i < percentages.length - 1; i++) percentages[i] = 0f;
        assertTrue(Calibrater.resetRatings(percentages, false));

        percentages[3] = 0f;
        assertTrue(Calibrater.resetRatings(percentages, false));
    }
}