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

    public <T extends RatedObject> List<RatedObject<T>> getRateables(List<RatedObject<T>> objects) {
        for (int i = 0; i < mPercentages.length; i++) objects.get(i).setPercent(mPercentages[i]);
        return objects;
    }

    private double[] mPercentages;
    private void setPercentages(double[] percentages) { this.mPercentages = percentages; }
    private void setPercentages(float[] percentages) { this.mPercentages = TypeConverters.arrayFloatToDouble(percentages); }
    private void setPercentages(Double[] percentages) {this.mPercentages = TypeConverters.arrayBoxedToPrimitiveDouble(percentages); }
    private void setPercentages(Float[] percentages) {this.mPercentages = TypeConverters.arrayFloatBoxedToDouble(percentages); }
    public double[] getPercentages() { return mPercentages; }
    public float[] getPercentagesBoxedFloat() { return TypeConverters.arrayDoubleToFloat(mPercentages); }
    public Double[] getPercentagesBoxedDouble() { return TypeConverters.arrayPrimitiveToBoxedDouble(mPercentages); }
    public Float[] getPercentagesFloat() { return TypeConverters.arrayDoubleToFloatBoxed(mPercentages); }

    public static Arrays with(double[] percentages, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Arrays(percentages, magnitude, precision,  clickListener);
    }

    public static <T extends RatedObject> Objects with(List<RatedObject<T>> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Objects(rateables, magnitude, precision,  clickListener);
    }

    public static class Arrays {

        private Rateraid mRateraid;
        private double[] mPercentages;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        private Arrays(double[] percentages, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mPercentages = percentages;
            Calibrater.resetRatings(mPercentages, false, precision);
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
        }

        public Arrays addShifters(View incrementButton, View decrementButton, int index) {
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

        public Arrays addRemover(View removeButton, List items, int index) {
            removeButton.setOnClickListener(clickedView -> {
                items.remove(index);
                Calibrater.removeRating(mPercentages, index);
                if (mClickListener != null) mClickListener.onClick(removeButton);
            });
            return this;
        }

        public Arrays addEditor(EditText valueEditor, int index) {
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

        public Rateraid instance() {
            mRateraid = new Rateraid();
            mRateraid.setPercentages(mPercentages);
            return mRateraid;
        }
    }

    public static class Objects<T extends RatedObject> {

        private Rateraid mRateraid;
        private List<RatedObject<T>> mRateables;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        private Objects(List<RatedObject<T>> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mRateables = rateables;
            resetRatings(rateables, false, precision);
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
        }

        public Objects addShifters(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                shiftRatings(mRateables, index, mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                shiftRatings(mRateables, index, -mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(decrementButton);
            });
            return this;
        }

        public Objects addRemover(View removeButton, int index) {
            removeButton.setOnClickListener(clickedView -> {
                removeRating(mRateables, index);
                if (mClickListener != null) mClickListener.onClick(removeButton);
            });
            return this;
        }

        public Objects addEditor(EditText valueEditor, int index) {
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
                            double magnitude = percentage - mRateables.get(index).getPercent();
                            shiftRatings(mRateables, index, magnitude, mPrecision);
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

        public Rateraid instance() {
            mRateraid = new Rateraid();
            double percents[] = new double[mRateables.size()];
            for (int i = 0; i < percents.length; i++) percents[i] = mRateables.get(i).getPercent();
            mRateraid.setPercentages(percents);
            return mRateraid;
        }
    }

    public static <T extends RatedObject> boolean shiftRatings(List<RatedObject<T>> objects, int index, double magnitude, int precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.shiftRatings(percents, index, magnitude, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    public static <T extends RatedObject> boolean resetRatings(List<RatedObject<T>> objects, boolean forceReset, @Nullable Integer precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.resetRatings(percents, forceReset, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    public static <T extends RatedObject> boolean removeRating(List<RatedObject<T>> objects, int index) {
        objects.remove(index);
        return resetRatings(objects, false, null);
    }

    public static <T extends RatedObject> boolean recalibrateRatings(List<RatedObject<T>> objects) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.recalibrateRatings(percents);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }
}