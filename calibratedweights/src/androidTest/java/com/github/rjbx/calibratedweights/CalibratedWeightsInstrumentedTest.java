package com.github.rjbx.calibratedweights;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.InstrumentationRegistry;
import android.widget.Button;

import com.github.rjbx.proportions.Proportions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@android.support.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class CalibratedWeightsInstrumentedTest {

    private Button mIncrementButton;
    private Button mDecrementButton;
    private Context mContext;
    private long mCount;
    private CalibratedWeights.Builder mWeightsBuilder;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mIncrementButton = new Button(mContext);
        mDecrementButton = new Button(mContext);
    }

    @Test
    public void testAddButtonSetClickListenerCallback() {
        Float[] proportions = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude < 0.1f; magnitude += 0.01f) {
            mWeightsBuilder = CalibratedWeights.with(proportions, magnitude);
            for (int index = 0; index < 4; index++) {
                
                mWeightsBuilder.addButtonSet(mIncrementButton, mDecrementButton, index);
                mWeightsBuilder.build();
                float sum;
                
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(.25f, proportions[index], magnitude);
                sum = 0;
                for (int i = 0; i < proportions.length; i++) {
                    sum += proportions[i];
                    if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                }
                while (proportions[index] < 1f) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < proportions.length; i++) {
                        sum += proportions[i];
                        if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                    }
                }

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(1f, proportions[index], magnitude);
                sum = 0;
                for (int i = 0; i < proportions.length; i++) {
                    sum += proportions[i];
                    if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                }
                while (proportions[index] > 0f) {
                    mDecrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < proportions.length; i++) {
                        sum += proportions[i];
                        if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                    }
                }

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                assertEquals(0f, proportions[index], magnitude);
                sum = 0;
                for (int i = 0; i < proportions.length; i++) {
                    sum += proportions[i];
                    if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                }
                while (proportions[index] < .25f) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < proportions.length; i++) {
                        sum += proportions[i];
                        if (i == proportions.length - 1) assertEquals(1f, sum, magnitude);
                    }
                }

                Proportions.calibrateFloatArray(proportions, false);
            }
        }
    }

    @Test
    public void testSetClickListenerUserDefinedCallback() {
        Float[] proportions = { .25f, .25f, .25f, .25f };
        for (float magnitude = 0.01f; magnitude < 0.1f; magnitude += 0.01f) {
            mWeightsBuilder = CalibratedWeights.with(proportions, magnitude);
            for (int index = 0; index < 4; index++) {

                mWeightsBuilder.addButtonSet(mIncrementButton, mDecrementButton, index);
                mWeightsBuilder.build().setOnClickListener(clickedView -> mCount++);

                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                while (proportions[index] < 1f) mIncrementButton.performClick();

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                while (proportions[index] > 0f) mDecrementButton.performClick();

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                while (proportions[index] < .25f) mIncrementButton.performClick();

                Proportions.calibrateFloatArray(proportions, false);
            }
        }
        assertEquals(2896, mCount);
    }
}