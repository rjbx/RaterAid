package com.github.rjbx.calibratedweights;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.rjbx.proportions.Proportions;

import java.text.NumberFormat;
import java.text.ParseException;

import timber.log.Timber;

public class CalibratedWeights {

    private Float[] mProportions;
    private Float[] getProportions() { return mProportions; }
    private void setProportions(Float[] proportions) { this.mProportions = proportions; }

    private View.OnClickListener mClickListener;
    private View.OnClickListener getClickListener() { return mClickListener; }
    public void setOnClickListener(View.OnClickListener clickListener) { mClickListener = clickListener; }

    public static CalibratedWeights.Builder with(Float[] proportions, float magnitude) {
        return new CalibratedWeights.Builder(proportions, magnitude);
    }

    public static class Builder {

        CalibratedWeights mCalibratedWeights;
        Float[] mProportions;
        float mMagnitude;

        Builder(Float[] proportions, float magnitude) {
            mProportions = proportions;
            mMagnitude = magnitude;
        }

        public CalibratedWeights.Builder addButtonSet(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Proportions.adjustFloatArray(mProportions, index, mMagnitude);
                View.OnClickListener clickListener = mCalibratedWeights.getClickListener();
                if (clickListener != null) clickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Proportions.adjustFloatArray(mProportions, index, -mMagnitude);
                View.OnClickListener clickListener = mCalibratedWeights.getClickListener();
                if (clickListener != null) clickListener.onClick(decrementButton);
            });
            return this;
        }

        public CalibratedWeights.Builder addValueEditor(TextView valueEditor, int index) {
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            float percentage = percentFormatter.parse(onEditorActionView.getText().toString()).floatValue();
                            float magnitude = percentage - mProportions[index];
                            Proportions.adjustFloatArray(mProportions, index, magnitude);
                            View.OnClickListener clickListener = mCalibratedWeights.getClickListener();
                            if (clickListener != null) clickListener.onClick(valueEditor);
                        } catch (ParseException e) {
                            Timber.e(e);
                            return false;
                        }
                        return true;
                    default:
                        return false;
                }
            });
            return this;
        }

        public CalibratedWeights build() {
            mCalibratedWeights = new CalibratedWeights();
            mCalibratedWeights.setProportions(mProportions);
            return mCalibratedWeights;
        }
    }
}