package com.github.rjbx.rateraid;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.rjbx.calibrater.Calibrater;

import java.text.NumberFormat;
import java.text.ParseException;

public class Rateraid {

    private Float[] mPercentages;
    private Float[] getPercentages() { return mPercentages; }
    private void setPercentages(Float[] percentages) { this.mPercentages = percentages; }

    private View.OnClickListener mClickListener;
    private View.OnClickListener getClickListener() { return mClickListener; }
    public void setOnClickListener(View.OnClickListener clickListener) { mClickListener = clickListener; }

    public static Rateraid.Builder with(Float[] percentages, float magnitude) {
        return new Rateraid.Builder(percentages, magnitude);
    }

    public static class Builder {

        Rateraid mRateraid;
        Float[] mPercentages;
        float mMagnitude;

        Builder(Float[] percentages, float magnitude) {
            mPercentages = percentages;
            mMagnitude = magnitude;
        }

        public Rateraid.Builder addButtonSet(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, mMagnitude);
                View.OnClickListener clickListener = mRateraid.getClickListener();
                if (clickListener != null) clickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, -mMagnitude);
                View.OnClickListener clickListener = mRateraid.getClickListener();
                if (clickListener != null) clickListener.onClick(decrementButton);
            });
            return this;
        }

        public Rateraid.Builder addValueEditor(TextView valueEditor, int index) {
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            float percentage = percentFormatter.parse(onEditorActionView.getText().toString()).floatValue();
                            float magnitude = percentage - mPercentages[index];
                            Calibrater.shiftRatings(mPercentages, index, magnitude);
                            View.OnClickListener clickListener = mRateraid.getClickListener();
                            if (clickListener != null) clickListener.onClick(valueEditor);
                        } catch (ParseException e) {
                            throw new NumberFormatException();
                        }
                        return true;
                    default:
                        return false;
                }
            });
            return this;
        }

        public Rateraid build() {
            mRateraid = new Rateraid();
            mRateraid.setPercentages(mPercentages);
            return mRateraid;
        }
    }
}