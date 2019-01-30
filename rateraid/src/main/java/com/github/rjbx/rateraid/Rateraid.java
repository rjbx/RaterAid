package com.github.rjbx.rateraid;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.rjbx.calibrater.Calibrater;
import com.github.rjbx.calibrater.TypeConverters;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import androidx.annotation.Nullable;

public class Rateraid {

    public interface RatedObject<T> {
        void setPercent(double percent);
        double getPercent();
        T getObject();
    }

    private List<RatedObject> mRateables;
    public List<RatedObject> getRateables() { return mRateables; }
    public void setRateables(List<RatedObject> rateables) { this.mRateables = rateables; }

    private double[] mPercentages;
    private void setPercentages(double[] percentages) { this.mPercentages = percentages; }
    private void setPercentages(float[] percentages) { this.mPercentages = TypeConverters.arrayFloatToDouble(percentages); }
    private void setPercentages(Double[] percentages) {this.mPercentages = TypeConverters.arrayBoxedToPrimitiveDouble(percentages); }
    private void setPercentages(Float[] percentages) {this.mPercentages = TypeConverters.arrayFloatBoxedToDouble(percentages); }
    public double[] getPercentages() { return mPercentages; }
    public float[] getPercentagesBoxedFloat() { return TypeConverters.arrayDoubleToFloat(mPercentages); }
    public Double[] getPercentagesBoxedDouble() { return TypeConverters.arrayPrimitiveToBoxedDouble(mPercentages); }
    public Float[] getPercentagesFloat() { return TypeConverters.arrayDoubleToFloatBoxed(mPercentages); }

    public static Rateraid.Builder with(double[] percentages, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Rateraid.Builder(percentages, magnitude, precision,  clickListener);
    }

    public static <T extends RatedObject>Rateraid.Builder with(List<RatedObject<T>> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Rateraid.Builder(rateables, magnitude, precision,  clickListener);
    }

    public static class Builder {

        private Rateraid mRateraid;
        private List<RatedObject> mRateables;
        private double[] mPercentages;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        Builder(double[] percentages, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mPercentages = percentages;
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
        }

        <T extends RatedObject> Builder(List<RatedObject<T>> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {

            mRateables = rateables;
            for (int i = 0; i < rateables.size(); i++) { mPercentages[i] = mRateables.get(i).getPercent(); }
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
        }

        public Rateraid.Builder addShifters(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercentages, index, -mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(decrementButton);
            });
            return this;
        }

        public Rateraid.Builder addRemover(View removeButton, List items, int index) {
            removeButton.setOnClickListener(clickedView -> {
                items.remove(index);
                Calibrater.removeRating(mPercentages, index, items.size());
                if (mClickListener != null) mClickListener.onClick(removeButton);
            });
            return this;
        }

        public Rateraid.Builder addEditor(EditText valueEditor, int index) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percentage;
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percentage = percentFormatter.parse(viewText).doubleValue();
                            else percentage = Double.parseDouble(viewText);
                            if (percentage < 0d || percentage > 1d) return false;
                            double magnitude = percentage - mPercentages[index];
                            Calibrater.shiftRatings(mPercentages, index, magnitude, mPrecision);
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
            if (mRateables != null) {
                for (int i = 0; i < mRateables.size(); i++)
                    mRateables.get(i).setPercent(mPercentages[i]);
                mRateraid.setRateables(mRateables);
            }
            return mRateraid;
        }
    }
}