package com.github.rjbx.rateraid;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.github.rjbx.calibrater.Calibrater;

import java.text.NumberFormat;
import java.text.ParseException;

import androidx.annotation.Nullable;

public class Rateraid {

    private Double[] mPercentages;
    private Double[] getPercentages() { return mPercentages; }
    private void setPercentages(Double[] percentages) { this.mPercentages = percentages; }

    private View.OnClickListener mClickListener;
    private View.OnClickListener getClickListener() { return mClickListener; }
    public void setOnClickListener(View.OnClickListener clickListener) { mClickListener = clickListener; }

    public static Rateraid.Builder with(Double[] percentages, Double magnitude) {
        return new Rateraid.Builder(percentages, magnitude, null);
    }

    public static Rateraid.Builder with(Double[] percentages, Double magnitude, View.OnClickListener clickListener) {
        return new Rateraid.Builder(percentages, magnitude, clickListener);
    }

    public static Rateraid.Builder with(double[] percentages, double magnitude) {
        Double[] boxedPercentages = new Double[percentages.length];
        int i = 0; for (double percentage : percentages)  boxedPercentages[i++] = percentage;
        return new Rateraid.Builder(boxedPercentages, magnitude, null);
    }

    public static Rateraid.Builder with(double[] percentages, double magnitude, View.OnClickListener clickListener) {
        Double[] boxedPercentages = new Double[percentages.length];
        int i = 0; for (double percentage : percentages)  boxedPercentages[i++] = percentage;
        return new Rateraid.Builder(boxedPercentages, magnitude, clickListener);
    }

    public static class Builder {

        Rateraid mRateraid;
        Double[] mPercentages;
        Double mMagnitude;
        View.OnClickListener mClickListener;

        Builder(Double[] percentages, Double magnitude, @Nullable View.OnClickListener clickListener) {
            mPercentages = percentages;
            mMagnitude = magnitude;
            mClickListener = clickListener;
        }

        public Rateraid.Builder addButtonSet(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, mMagnitude);
                if (mClickListener != null) mClickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, -mMagnitude);
                if (mClickListener != null) mClickListener.onClick(decrementButton);
            });
            return this;
        }

        public Rateraid.Builder addValueEditor(TextView valueEditor, int index) {
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percentage;
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percentage = percentFormatter.parse(viewText).floatValue();
                            else percentage = Float.parseFloat(viewText);
                            double magnitude = percentage - mPercentages[index];
                            Calibrater.shiftRatings(mPercentages, index, magnitude);
                            if (mClickListener != null) mClickListener.onClick(valueEditor);
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
            if (mClickListener != null) mRateraid.setOnClickListener(mClickListener);
            return mRateraid;
        }
    }
}