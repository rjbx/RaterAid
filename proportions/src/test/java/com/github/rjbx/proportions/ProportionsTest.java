package com.github.rjbx.proportions;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test methods for the {@link Proportions} class.
 * Equals assertions are more precise as delta parameter approaches zero.
 */
public class ProportionsTest {

    /**
     * Verifies that incrementing and decrementing a {@link Float} array element resets all element values.
     */
    @Test public final void testAdjustFloatArrayResetSingle() {

        Float[] proportions = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude <= .1f; magnitude += 0.01f) {
            for (int index = 0; index < 4; index++) {
                Proportions.adjustFloatArray(proportions, index, magnitude);
                Proportions.adjustFloatArray(proportions, index, magnitude * -1f);
            }
            assertEquals(.25f, proportions[0], magnitude / 20000);
            assertEquals(.25f, proportions[1], magnitude / 20000);
            assertEquals(.25f, proportions[2], magnitude / 20000);
            assertEquals(.25f, proportions[3], magnitude / 20000);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@link Float} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustFloatArrayRepeatRange() {

        Float[] proportions = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude <= .1f; magnitude += 0.01f) {
            for (int index = 0; index < proportions.length; index++) {
                while (proportions[index] <= .99f) {
                    Proportions.adjustFloatArray(proportions, index, magnitude);
                }
                while (proportions[index] >= .01f) {
                    Proportions.adjustFloatArray(proportions, index, magnitude * -1f);
                }
                int nextIndex = index == 3 ? 0 : index + 1;
                while (proportions[index] >= .01f) {
                    Proportions.adjustFloatArray(proportions, nextIndex, magnitude * -1);
                }
                while (proportions[nextIndex] <= .99f) {
                    Proportions.adjustFloatArray(proportions, nextIndex, magnitude);
                }
            }
            assertEquals(1f, proportions[0], magnitude / 2);
            assertEquals(0f, proportions[1], magnitude / 2);
            assertEquals(0f, proportions[2], magnitude / 2);
            assertEquals(0f, proportions[3], magnitude / 2);
        }
    }

    /**
     * Verifies that incrementing and decrementing through the range of possible values for
     * two adjacent {@link Float} arrays assigns the expected value to each array element.
     */
    @Test public final void testAdjustFloatArrayResetSingleRepeatRange() {

        Float[] proportions = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude < 0.1f; magnitude += 0.01f) {
            for (int index = 0; index < 4; index++) {
                float sum;

                while (proportions[index] < 1f) {
                    Proportions.adjustFloatArray(proportions, index, magnitude / 2);
                    sum = 0;
                    for (int i = 0; i < proportions.length; i++) {
                        sum += proportions[i];
                        if (i == proportions.length - 1) assertEquals(1f, sum, magnitude / 2);
                    }
                }
                Proportions.adjustFloatArray(proportions, index, -magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, -magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, magnitude / 2);
                assertEquals(1f, proportions[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < proportions.length; i++) {
                    sum += proportions[i];
                    if (i == proportions.length - 1) assertEquals(1f, sum, magnitude / 2);
                }

                while (proportions[index] > 0f) {
                    Proportions.adjustFloatArray(proportions, index, -magnitude / 2);
                    sum = 0;
                    for (int i = 0; i < proportions.length; i++) {
                        sum += proportions[i];
                        if (i == proportions.length - 1) assertEquals(1f, sum, magnitude / 2);
                    }
                }
                Proportions.adjustFloatArray(proportions, index, magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, -magnitude / 2);
                Proportions.adjustFloatArray(proportions, index, -magnitude / 2);
                assertEquals(0f, proportions[index], magnitude / 2);
                sum = 0;
                for (int i = 0; i < proportions.length; i++) {
                    sum += proportions[i];
                    if (i == proportions.length - 1) assertEquals(1f, sum, magnitude / 2);
                }

                Proportions.calibrateFloatArray(proportions, false);
            }
        }
    }

    /**
     * Verifies that adjusting {@link Float} array elements to offset an element
     * within a certain margin past the lower limit does not over allocate the unused offset.
     */
    @Test public final void testAdjustFloatArrayLimitAllocation() {

        for (float magnitude = 0.001f; magnitude < 1f; magnitude *= 10) {
            Float[] proportions = { 1f, 0f, 0f, 0f, 0f };
            Proportions.adjustFloatArray(proportions, 1, magnitude);
            Proportions.adjustFloatArray(proportions, 1, magnitude);
            Proportions.adjustFloatArray(proportions, 2, magnitude);
            Proportions.adjustFloatArray(proportions, 2, magnitude);
            Proportions.adjustFloatArray(proportions, 3, magnitude);
            Proportions.adjustFloatArray(proportions, 3, magnitude);
            Proportions.adjustFloatArray(proportions, 4, magnitude);

            while (proportions[3] > magnitude / 2)
                Proportions.adjustFloatArray(proportions, 1, magnitude);

            float sum = 0;
            for (float proportion : proportions) sum += proportion;
            assertEquals(1f, sum, magnitude / 2);
        }
    }

    /**
     * Verifies that adjusting a {@link Float} array with invalid and valid magnitudes returns
     * the expected boolean value.
     */
    @Test public final void testAdjustFloatArrayReturnValue() {

        Float[] proportions = {0f, 0f, 0f, 0f};
        assertFalse(Proportions.adjustFloatArray(proportions, 0, -0.01f));
        assertFalse(Proportions.adjustFloatArray(proportions, 0, 0f));
        assertFalse(Proportions.adjustFloatArray(proportions, 0, 1.0001f));

        proportions[0] = 1f;
        assertFalse(Proportions.adjustFloatArray(proportions, 0, 0.02f));
        assertFalse(Proportions.adjustFloatArray(proportions, 0, 0f));
        assertFalse(Proportions.adjustFloatArray(proportions, 0, 2f));

        for (int i = 0; i < proportions.length; i++) proportions[i] = .5f;
        assertTrue(Proportions.adjustFloatArray(proportions, 0, -0.1f));
        assertTrue(Proportions.adjustFloatArray(proportions, 0, 0.1f));

        for (int i = 0; i < proportions.length; i++) proportions[i] = .1f;
        assertTrue(Proportions.adjustFloatArray(proportions, 0, -0.2f));
        assertTrue(Proportions.adjustFloatArray(proportions, 0, 0.02f));
    }

    /**
     * Verifies that calibrating a {@link Float} array assigns the expected value to each array element
     * and results in the expected sum of all elements.
     */
    @Test public final void testCalibrateFloatArrayEqualDistribution() {

        float magnitude = 1f;
        for (int i = 1; i < 20; i++) {
            Float[] proportions = new Float[i];
            for (int j = 0; j < proportions.length; j++) proportions[j] = 0f;
            Proportions.calibrateFloatArray(proportions, false);
            assertEquals(1f / i, proportions[i - 1], 0f);
            float sum = 0f;
            for (Float proportion : proportions) sum += proportion;
            assertEquals(1f, sum, magnitude / 200000);
        }
    }

    /**
     * Verifies that calibrating {@link Float} arrays with different element values returns
     * the expected boolean value.
     */
    @Test public final void testCalibrateFloatArrayReturnValue() {

        Float[] proportions = {1f, 0f, 0f, 0f};
        for (int i = 0; i < 2; i++) {
            assertFalse(Proportions.calibrateFloatArray(proportions, false));
            proportions[0] -= .25f;
            proportions[3 - i] +=.25f;
        }
        
        proportions[3] = .5f;
        assertTrue(Proportions.calibrateFloatArray(proportions, false));

        for (int i = 0; i < proportions.length - 1; i++) proportions[i] = 0f;
        assertTrue(Proportions.calibrateFloatArray(proportions, false));

        proportions[3] = 0f;
        assertTrue(Proportions.calibrateFloatArray(proportions, false));
    }
}