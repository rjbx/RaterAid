package com.github.rjbx.rateraid;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

    public <T extends RatedObject> List<T> getRateables(List<T> objects) {
        for (int i = 0; i < mPercents.length; i++) objects.get(i).setPercent(mPercents[i]);
        return objects;
    }

    private double[] mPercents;
    private void setPercents(double[] percents) { this.mPercents = percents; }
    private void setPercents(float[] percents) { this.mPercents = TypeConverters.arrayFloatToDouble(percents); }
    private void setPercents(Double[] percents) {this.mPercents = TypeConverters.arrayBoxedToPrimitiveDouble(percents); }
    private void setPercents(Float[] percents) {this.mPercents = TypeConverters.arrayFloatBoxedToDouble(percents); }
    public double[] getPercents() { return mPercents; }
    public float[] getPercentsBoxedFloat() { return TypeConverters.arrayDoubleToFloat(mPercents); }
    public Double[] getPercentsBoxedDouble() { return TypeConverters.arrayPrimitiveToBoxedDouble(mPercents); }
    public Float[] getPercentsFloat() { return TypeConverters.arrayDoubleToFloatBoxed(mPercents); }

    public static Arrays with(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Arrays(percents, magnitude, precision,  clickListener);
    }

    public static <T extends RatedObject> Objects with(List<T> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new Objects(rateables, magnitude, precision,  clickListener);
    }

    public static class Arrays {

        private Rateraid mRateraid;
        private double[] mPercents;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        private Arrays(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
            mPercents = percents;
            Calibrater.resetRatings(mPercents, false, precision);
        }

        public Arrays addShifters(View incrementButton, View decrementButton, int index) {
            incrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercents, index, mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(incrementButton);
            });
            decrementButton.setOnClickListener(clickedView -> {
                Calibrater.shiftRatings(mPercents, index, -mMagnitude, mPrecision);
                if (mClickListener != null) mClickListener.onClick(decrementButton);
            });
            return this;
        }

        public Arrays addRemover(View removeButton, int index) {
            removeButton.setOnClickListener(clickedView -> {
                Calibrater.removeRating(mPercents, index);
                if (mClickListener != null) mClickListener.onClick(removeButton);
            }); return this;
        }

        public Arrays addEditor(EditText valueEditor, int index, @Nullable InputMethodManager imm, @Nullable Runnable runnable) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        if (runnable != null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(runnable);
                        }
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percent = mPercents[index];
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percent = percentFormatter.parse(viewText).doubleValue();
                            else if (!viewText.isEmpty()) percent = Double.parseDouble(viewText);
                            else {
                                onEditorActionView.setText(percentFormatter.format(percent));
                                return false;
                            }
                            if (percent < 0d || percent > 1d) return false;
                            double magnitude = percent - mPercents[index];
                            Calibrater.shiftRatings(mPercents, index, magnitude, mPrecision);
                            if (imm != null) imm.toggleSoftInput(0, 0);
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
            mRateraid.setPercents(mPercents);
            return mRateraid;
        }
    }

    public static class Objects<T extends RatedObject> {

        private Rateraid mRateraid;
        private List<T> mRateables;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        private Objects(List<T> rateables, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
            mRateables = rateables;
            resetRatings(rateables, false, precision);
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


        public Objects addRemover(View removeButton, int index, @Nullable DialogInterface dialog) {
            removeButton.setOnClickListener(clickedView -> {
                removeRating(mRateables, index);

                if (dialog != null) dialog.dismiss();
                if (mClickListener != null) mClickListener.onClick(removeButton);
            }); return this;
        }

        public Objects addEditor(EditText valueEditor, int index, @Nullable InputMethodManager imm, @Nullable Runnable runnable) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        if (runnable != null) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(runnable);
                        }
                        final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
                        try {
                            double percent = mRateables.get(index).getPercent();
                            String viewText = onEditorActionView.getText().toString();
                            if (viewText.contains("%")) percent = percentFormatter.parse(viewText).doubleValue();
                            else if (!viewText.isEmpty()) percent = Double.parseDouble(viewText);
                            else {
                                onEditorActionView.setText(percentFormatter.format(percent));
                                return false;
                            }
                            if (percent < 0d || percent > 1d) return false;
                            double magnitude = percent - mRateables.get(index).getPercent();
                            shiftRatings(mRateables, index, magnitude, mPrecision);
                            if (imm != null) imm.toggleSoftInput(0, 0);
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
            mRateraid.setPercents(percents);
            return mRateraid;
        }
    }

    public static <T extends RatedObject> boolean shiftRatings(List<T> objects, int index, double magnitude, int precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.shiftRatings(percents, index, magnitude, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    public static <T extends RatedObject> boolean resetRatings(List<T> objects, boolean forceReset, @Nullable Integer precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.resetRatings(percents, forceReset, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    public static <T extends RatedObject> boolean removeRating(List<T> objects, int index) {
        objects.remove(index);
        return resetRatings(objects, false, null);
    }

    public static <T extends RatedObject> boolean recalibrateRatings(List<T> objects, boolean forceReset, @Nullable Integer precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.recalibrateRatings(percents, false, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }
}