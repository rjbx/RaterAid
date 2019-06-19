package com.github.rjbx.rateraid;

import android.content.DialogInterface;
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

/**
 * Classes for specifying adjustments to the values of,
 * as well as behaviors for the views affecting, a percent series.
 * Users can bind predefined as well as custom behaviors to their adjusting views
 * to increment and decrement, as well as optionally remove or text edit the values of
 * a percent series. The values remain synchronized throughout adjustments to the extent of
 * the user-defined precision which articulates the acceptable {@link Calibrater} response.
 */
public class Rateraid {

    /**
     * Wrapper interface for an object associated withRateables a percent value.
     * @param <T> to specify the type of the implementing class for generating appropriately cast
     *            objects from {@link #getObject()}
     */
    public interface Rateable<T> {
        void setPercent(double percent);
        double getPercent();
        T getObject();
    }

    /*
     * Instance field updated from subclasses
     */
    private double[] mPercents;

    /**
     * Transfers the values from an adjusted {@link ValueSeries} to an
     * {@code ArrayList} of {@link Rateable} elements.
     * @param objects {@code ArrayList} of {@link Rateable} elements
     * @return argument {@code ArrayList} of {@link Rateable} elements withRateables updated percents
     */
    public <T extends Rateable> List<T> updatedRateablesFromPercentSeries(List<T> objects) {
        for (int i = 0; i < mPercents.length; i++) objects.get(i).setPercent(mPercents[i]);
        return objects;
    }

    /*
     * Helper methods for setting the percent array field withRateables primitive and boxed double and float array types
     */
    private void setPercents(double[] percents) { this.mPercents = percents; }
    private void setPercents(float[] percents) { this.mPercents = TypeConverters.arrayFloatToDouble(percents); }
    private void setPercents(Double[] percents) {this.mPercents = TypeConverters.arrayBoxedToPrimitiveDouble(percents); }
    private void setPercents(Float[] percents) {this.mPercents = TypeConverters.arrayFloatBoxedToDouble(percents); }

    /*
     * Helper methods for getting the percent array field as primitive and boxed double and float array types
     */
    public double[] getPercents() { return mPercents; }
    public float[] getPercentsBoxedFloat() { return TypeConverters.arrayDoubleToFloat(mPercents); }
    public Double[] getPercentsBoxedDouble() { return TypeConverters.arrayPrimitiveToBoxedDouble(mPercents); }
    public Float[] getPercentsFloat() { return TypeConverters.arrayDoubleToFloatBoxed(mPercents); }


    /**
     * Initialize the {@code double} array percent series, attributes and behavior
     * associated withRateables all view binding method calls.
     * @param percents {@code double} array elements
     * @param magnitude amount of the adjustment; non-zero value should be between 1 and -1
     * @param precision number of decimal places to move the permitted error from the whole
     * @param clickListener behavior to be applied on click of relevant views
     * @return {@link ValueSeries} from which to chain view binding method calls
     */
    // TODO: Convert from array to ArrayList
    public static ValueSeries withValues(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new ValueSeries(percents, magnitude, precision,  clickListener);
    }

    /**
     * Initialize the {@link Rateable} {@code ArrayList} percent series, attributes and behavior
     * associated withRateables all view binding method calls.
     * @param objects {@link Rateable} {@code ArrayList} elements
     * @param magnitude amount of the adjustment; non-zero value should be between 1 and -1
     * @param precision number of decimal places to move the permitted error from the whole
     * @param clickListener behavior to be applied on click of relevant views
     * @return {@link ObjectSeries} from which to chain view binding method calls
     */
    public static <T extends Rateable> ObjectSeries withRateables(List<T> objects, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
        return new ObjectSeries(objects, magnitude, precision,  clickListener);
    }

    /**
     * Class for chaining method calls for defining behaviors of views
     * associated withRateables a {@code double} array percent series
     */
    public static class ValueSeries {

        // Instance fields of this class
        private Rateraid mRateraid;
        private double[] mPercents;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        /**
         * Initialize the instance fields of this class from the parent class accessor
         * @param percents 
         * @param magnitude
         * @param precision
         * @param clickListener
         */
        private ValueSeries(double[] percents, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
            mPercents = percents;
            Calibrater.recalibrateRatings(mPercents, false, precision);
        }

        /**
         * Define the controllers by which the value of the percent series is incremented and decremented
         * @param incrementButton view that, when clicked, should increment the targeted value
         * @param decrementButton view that, when clicked, should decrement the targeted value
         * @param index location of the value to be adjusted
         * @return {@link ValueSeries} from which to chain view binding method calls
         */
        public ValueSeries addShifters(View incrementButton, View decrementButton, int index) {
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

        /**
         * Define the controller by which an element of the percent series is invalidated.
         * @param removeButton view that, when clicked, should invalidate the targeted element
         *                     from further adjustment. The element cannot be outright removed as
         *                     copying a primitive array withRateables fewer elements disassociates the
         *                     array reference from the previous reference.
         * @param index location of the value to be invalidated
         * @param dialog removal message to be dismissed 
         * @return {@link ValueSeries} from which to chain view binding method calls
         */
        public ValueSeries addRemover(View removeButton, int index, @Nullable DialogInterface dialog) {
            removeButton.setOnClickListener(clickedView -> {
                Calibrater.removeRating(mPercents, index);

                if (dialog != null) dialog.dismiss();
                if (mClickListener != null) mClickListener.onClick(removeButton);
            }); return this;
        }

        /**
         * Define the controller by which the value of the percent series is replaced by text entry.
         * @param valueEditor editor that, when receiving entry, should replace the targeted value 
         * @param index location of the value to be replaced
         * @param imm input manager to be dismissed 
         * @return {@link ValueSeries} from which to chain view binding method calls
         */
        public ValueSeries addEditor(EditText valueEditor, int index, @Nullable InputMethodManager imm) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
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

        /**
         * Retrieve a new reference to a {@link ValueSeries} withRateables values initialized.
         * @return new reference to a {@link ValueSeries}
         */
        public Rateraid instance() {
            mRateraid = new Rateraid();
            mRateraid.setPercents(mPercents);
            return mRateraid;
        }
    }
    
    /**
     * Class for chaining method calls for defining behaviors of views
     * associated withRateables a {@link Rateable} {@code ArrayList} percent series
     */
    public static class ObjectSeries<T extends Rateable> {

        // Instance fields of this class
        private Rateraid mRateraid;
        private List<T> mRateables;
        private double mMagnitude;
        private int mPrecision;
        private View.OnClickListener mClickListener;

        /**
         * Initialize the instance fields of this class from the parent class accessor
         * @param objects
         * @param magnitude
         * @param precision
         * @param clickListener
         */
        private ObjectSeries(List<T> objects, double magnitude, int precision, @Nullable View.OnClickListener clickListener) {
            mMagnitude = magnitude;
            mPrecision = precision;
            mClickListener = clickListener;
            mRateables = objects;
            recalibrateRatings(objects, false, precision);
        }

        /**
         * Define the controllers by which the value of the percent series is incremented and decremented
         * @param incrementButton view that, when clicked, should increment the targeted value
         * @param decrementButton view that, when clicked, should decrement the targeted value
         * @param index location of the value to be adjusted
         * @return {@link ObjectSeries} from which to chain view binding method calls
         */
        public ObjectSeries addShifters(View incrementButton, View decrementButton, int index) {
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

        /**
         * Define the controller by which an element of the percent series is removed.
         * @param removeButton view that, when clicked, should remove the targeted element
         * @param index location of the value to be invalidated
         * @param dialog removal message to be dismissed 
         * @return {@link ObjectSeries} from which to chain view binding method calls
         */
        public ObjectSeries addRemover(View removeButton, int index, @Nullable DialogInterface dialog) {
            removeButton.setOnClickListener(clickedView -> {
                removeRating(mRateables, index);

                if (dialog != null) dialog.dismiss();
                if (mClickListener != null) mClickListener.onClick(removeButton);
            }); return this;
        }

        /**
         * Define the controller by which the value of the percent series is replaced by text entry.
         * @param valueEditor editor that, when receiving entry, should replace the targeted value 
         * @param index location of the value to be replaced
         * @param imm input manager to be dismissed 
         * @return {@link ObjectSeries} from which to chain view binding method calls
         */
        public ObjectSeries addEditor(EditText valueEditor, int index, @Nullable InputMethodManager imm) {
            valueEditor.setImeOptions(EditorInfo.IME_ACTION_DONE);
            valueEditor.setInputType(EditorInfo.TYPE_CLASS_NUMBER|EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
            valueEditor.setOnEditorActionListener((onEditorActionView, onEditorActionId, onEditorActionEvent) -> {
                switch (onEditorActionId) {
                    case EditorInfo.IME_ACTION_DONE:
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
        
        /**
         * Retrieve a new reference to a {@link ObjectSeries} withRateables values initialized.
         * @return new reference to a {@link ObjectSeries}
         */
        public Rateraid instance() {
            mRateraid = new Rateraid();
            double percents[] = new double[mRateables.size()];
            for (int i = 0; i < percents.length; i++) percents[i] = mRateables.get(i).getPercent();
            mRateraid.setPercents(percents);
            return mRateraid;
        }
    }

    /**
     * Increments or decrements a {@link Rateable} {@code ArrayList} element by
     * the specified magnitude while calibrating other {@link Rateable} {@code ArrayList} elements
     * to maintain proportionality.
     * @param objects {@link Rateable} {@code ArrayList }elements to be adjusted if not proportionate
     * @param index index of the array element to be adjusted
     * @param magnitude amount of the adjustment; non-zero value should be no more than 1 or -1
     * @param precision number of decimal places to move the allowed error from the whole
     * @return true if percent was adjusted and false otherwise
     */
    public static <T extends Rateable> boolean shiftRatings(List<T> objects, int index, double magnitude, int precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.shiftRatings(percents, index, magnitude, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    /**
     * Assigns equivalent percents to each {@link Rateable} {@code ArrayList} element.
     * @param objects {@link Rateable} {@code ArrayList} elements to be reset if not equivalent
     * @param forceReset applies reset even if sum of array elements is as precise as specified
     * @param precision number of decimal places to move the permitted error from the whole
     * @return true if values were adjusted; false otherwise
     */
    public static <T extends Rateable> boolean resetRatings(List<T> objects, boolean forceReset, @Nullable Integer precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.resetRatings(percents, forceReset, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }

    /**
     * Removes {@link Rateable} from {@code ArrayList} at the specified index.
     * The whole is then distributed among the remaining elements
     * according to {@link #recalibrateRatings(List, boolean, Integer)}
     * @param objects {@link Rateable} {@code ArrayList} elements to be calibrated if not proportionate
     * @param index to be removed
     * @return true if values were adjusted; false otherwise
     */
    public static <T extends Rateable> boolean removeRating(List<T> objects, int index) {
        objects.remove(index);
        return recalibrateRatings(objects, false, null);
    }

    /**
     * Reads {@link Rateable} {@code ArrayList} and equally distributes to each array element
     * the difference between the whole and the sum of all array elements.
     * @param objects {@link Rateable} {@code ArrayList} to be calibrated closer to the whole
     * @param forceReset applies reset even if sum of array elements is as precise as specified
     * @param precision number of decimal places to move the permitted error from the whole
     * @return true if values were adjusted; false otherwise
     */
    public static <T extends Rateable> boolean recalibrateRatings(List<T> objects, boolean forceReset, @Nullable Integer precision) {
        boolean result;
        double percents[] = new double[objects.size()];
        for (int i = 0; i < percents.length; i++) percents[i] = objects.get(i).getPercent();
        result = Calibrater.recalibrateRatings(percents, false, precision);
        for (int i = 0; i < percents.length; i++) objects.get(i).setPercent(percents[i]);
        return result;
    }
}