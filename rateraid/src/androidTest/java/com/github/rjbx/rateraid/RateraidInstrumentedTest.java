package com.github.rjbx.rateraid;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import android.widget.Button;

import com.github.rjbx.calibrater.Calibrater;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@androidx.test.filters.LargeTest
@RunWith(AndroidJUnit4.class)
public class RateraidInstrumentedTest {

    private Button mIncrementButton;
    private Button mDecrementButton;
    private Context mContext;
    private long mCount;
    private Rateraid.Arrays mRateArrays;
    private Rateraid.Objects mRateObjects;

    private static int PRECISION = Calibrater.STANDARD_PRECISION;

    @Before public final void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getContext();
        mIncrementButton = new Button(mContext);
        mDecrementButton = new Button(mContext);
    }

    @Test public final void testArraysAddButtonSetClickListenerCallback() {
        double[] percentages = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            mRateArrays = Rateraid.with(percentages, magnitude, PRECISION, null);
            for (int index = 0; index < 4; index++) {

                mRateArrays.addShifters(mIncrementButton, mDecrementButton, index);
                mRateArrays.instance();
                double sum;

                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(.25d, percentages[index], magnitude);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                }
                while (percentages[index] < 1d) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(1d, percentages[index], magnitude);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                }
                while (percentages[index] > 0d) {
                    mDecrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                assertEquals(0d, percentages[index], magnitude);
                sum = 0;
                for (int i = 0; i < percentages.length; i++) {
                    sum += percentages[i];
                    if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                }
                while (percentages[index] < .25d) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        sum += percentages[i];
                        if (i == percentages.length - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                Calibrater.resetRatings(percentages, true, PRECISION);
            }
        }
    }

    @Test public final void testArraysSetClickListenerUserDefinedCallback() {
        double[] percentages = { .25d, .25d, .25d, .25d };
        for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            mRateArrays = Rateraid.with(percentages, magnitude, PRECISION, clickedView -> mCount++);
            for (int index = 0; index < 4; index++) {

                mRateArrays.addShifters(mIncrementButton, mDecrementButton, index);

                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                while (percentages[index] < 1d) mIncrementButton.performClick();

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                while (percentages[index] > 0d) mDecrementButton.performClick();

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                while (percentages[index] < .25d) mIncrementButton.performClick();

                Calibrater.resetRatings(percentages, true, PRECISION);
            }
        }
        assertEquals(2872, mCount);
    }

    @Test public final void testObjectsAddButtonSetClickListenerCallback() {
        List<Rateraid.RatedObject<Rateable>> rateables = new ArrayList<>(4);
        Rateable rateable = new Rateable();
        rateable.setPercent(.25d);
        for (int i = 0; i < 4; i++) rateables.add(rateable.clone());
        for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            mRateObjects = Rateraid.with(rateables, magnitude, PRECISION, null);
            for (int index = 0; index < 4; index++) {

                mRateObjects.addShifters(mIncrementButton, mDecrementButton, index);
                mRateObjects.instance();
                double sum;

                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(.25d, rateables.get(index).getPercent(), magnitude);
                sum = 0;
                for (int i = 0; i < rateables.size(); i++) {
                    sum += rateables.get(i).getPercent();
                    if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                }
                while (rateables.get(index).getPercent() < 1d) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < rateables.size(); i++) {
                        sum += rateables.get(i).getPercent();
                        if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                assertEquals(1d, rateables.get(index).getPercent(), magnitude);
                sum = 0;
                for (int i = 0; i < rateables.size(); i++) {
                    sum += rateables.get(i).getPercent();
                    if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                }
                while (rateables.get(index).getPercent() > 0d) {
                    mDecrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < rateables.size(); i++) {
                        sum += rateables.get(i).getPercent();
                        if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                assertEquals(0d, rateables.get(index).getPercent(), magnitude);
                sum = 0;
                for (int i = 0; i < rateables.size(); i++) {
                    sum += rateables.get(i).getPercent();
                    if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                }
                while (rateables.get(index).getPercent() < .25d) {
                    mIncrementButton.performClick();
                    sum = 0;
                    for (int i = 0; i < rateables.size(); i++) {
                        sum += rateables.get(i).getPercent();
                        if (i == rateables.size() - 1) assertEquals(1d, sum, magnitude);
                    }
                }

                Rateraid.resetRatings(rateables, true, PRECISION);
            }
        }
    }

    @Test public final void testSetClickListenerUserDefinedCallback() {
        List<Rateraid.RatedObject<Rateable>> rateables = new ArrayList<>(4);
        Rateable rateable = new Rateable();
        rateable.setPercent(.25d);
        for (int i = 0; i < 4; i++) rateables.add(rateable.clone());for (double magnitude = 0.01d; magnitude < 0.1d; magnitude += 0.01d) {
            mRateObjects = Rateraid.with(rateables, magnitude, PRECISION, clickedView -> mCount++);
            for (int index = 0; index < 4; index++) {

                mRateObjects.addShifters(mIncrementButton, mDecrementButton, index);

                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                while (rateables.get(index).getPercent() < 1d) mIncrementButton.performClick();

                mDecrementButton.performClick();
                mDecrementButton.performClick();
                mIncrementButton.performClick();
                mIncrementButton.performClick();
                while (rateables.get(index).getPercent() > 0d) mDecrementButton.performClick();

                mIncrementButton.performClick();
                mIncrementButton.performClick();
                mDecrementButton.performClick();
                mDecrementButton.performClick();
                while (rateables.get(index).getPercent() < .25d) mIncrementButton.performClick();

                Rateraid.resetRatings(rateables, true, PRECISION);
            }
        }
        assertEquals(2872, mCount);
    }

    private class Rateable implements Rateraid.RatedObject<Rateable>, Cloneable {
        
        private double percent;
        
        @Override public void setPercent(double percent) { this.percent = percent; }
        @Override public double getPercent() { return percent; }
        @Override public Rateable getObject() { return this; }

        @Override public Rateable clone() {
            Rateable clone  = new Rateable();
            clone.setPercent(this.percent);
            try { super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Class must implement Cloneable interface");
            }
            return clone;
        }
    }
}