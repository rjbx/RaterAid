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
        for (int i = 0; i < mpercents.length; i++) objects.get(i).setPercent(mpercents[i]);
        return objects;
    }

    private double[] mpercents;
    private void setpercents(double[] percents) { this.mpercents = percents; }
    private void setpercents(float[] percents) { this.mpercents = TypeConverters.arrayFloatToDouble(percents); }
    private void setpercents(Double[] percents) {this.mpercents = TypeConverters.arrayBoxedToPrimitiveDouble(percents); }
    private void setpercents(Float[] percents) {this.mpercents = TypeConverters.arrayFloatBoxedToDouble(percents); }
    public double[] getpercents() { return mpercents; }
    public float[] getpercentsBoxedFloat() { return TypeConverters.arrayDoubleToFloat(mpercents); }
    public Double[] getpercentsBoxedDouble() { return TypeConverters.arrayPrimitiveToBoxedDouble(mpercents); }
    public Float[] getpercentsFloat() { return TypeConverters.arrayDoubleToFloatBoxed(mpercents); }

    public static Arrays with(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Arrays(percents, magnitude, precision,  clickListener);
    }

    public static <T extends RatedObject> Objects with(List<RatedObject<T>> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Objects(rateables, magnitude, precision,  clickListener);
    }

    public static class Arrays {

        private Rateraid mRateraid;
        private double[] mpercents;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        private Arrays(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mpercents = percents;
            Calibrater.resetRatings(mpercents, false, precision);
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
        }

        public Arrays addShifters(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mpercents, index, mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mpercents, index, -mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(decrementButton);
            });
            return this;
        }

        public Arrays addRemover(View removeButton, int index) {
            removeButton.setOnClickListener(clickedView -> {
                Calibrater.removeRating(mpercents, index);
                if (mClickListener != null) mClickListener.onClick(removeButton);
            }); return this;
        }

        public Arrays addEditor(EditText valueEditor, int index) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percent;
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percent = percentFormatter.parse(viewText).doubleValue();
                            else percent = Double.parseDouble(viewText);
                            if (percent < 0d || percent > 1d) return false;
                            double magnitude = percent - mpercents[index];
                            Calibrater.shiftRatings(mpercents, index, magnitude, mPrecision);
                            if (mClickListener != null) mClickListener.onClick(valueEditor);
                        } catch (ParseException e) {
                            throw new NumberFormatException();
                        } return true;
                    default:
                        return false;
                }
            }); return this;
        }

        public Rateraid instance() {
            mRateraid = new Rateraid();
            mRateraid.setpercents(mpercents);
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
            }); return this;
        }

        public Objects addEditor(EditText valueEditor, int index) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percent;
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percent = percentFormatter.parse(viewText).doubleValue();
                            else percent = Double.parseDouble(viewText);
                            if (percent < 0d || percent > 1d) return false;
                            double magnitude = percent - mRateables.get(index).getPercent();
                            shiftRatings(mRateables, index, magnitude, mPrecision);
                            if (mClickListener != null) mClickListener.onClick(valueEditor);
                        } catch (ParseException e) {
                            throw new NumberFormatException();
                        } return true;
                    default:
                        return false;
                }
            }); return this;
        }

        public Rateraid instance() {
            mRateraid = new Rateraid();
            double percents[] = new double[mRateables.size()];
            for (int i = 0; i < percents.length; i++) percents[i] = mRateables.get(i).getPercent();
            mRateraid.setpercents(percents);
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