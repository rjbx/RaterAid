package com.github.rjbx.calibrater;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting between decimal array types
 */
public final class TypeConverters {

    /**
     * Generates a primitive {@code List} of {@code Double} from a primitive {@code float} array
     */
    public static List<Double> arrayFloatToDouble(float[] floatArray) {
        List<Double> doubleList = new ArrayList<>();
        for (float f: floatArray) doubleList.add((double) f);
        return doubleList;
    }

    /**
     * Generates a primitive {@code List} of {@code Double} from a boxed {@code Float} array
     */
    public static List<Double> arrayFloatBoxedToDouble(List<Float> floatList) {
        List<Double> doubleList = new ArrayList<>();
        for (Float f : floatList) doubleList.add(f.doubleValue());
        return doubleList;
    }

    /**
     * Generates a primitive {@code List} of {@code Double} from a boxed {@code List} of {@code Double} array
     */
    public static List<Double> arrayBoxedToPrimitiveDouble(double[] doubleArray) {
        List<Double> doubleList = new ArrayList<>();
        for (double d : doubleArray) doubleList.add(d);
        return doubleList;
    }

    /**
     * Generates a boxed {@code List} of {@code Double} from a primitive {@code List} of {@code Double}
     */
    public static float[] listDoubleToArrayFloat(List<Double> doubleList) {
        Double[] doubleArray = doubleList.toArray(new Double[0]);
        float[] doublePrimativeArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) doublePrimativeArray[i] = doubleArray[i].floatValue();
        return doublePrimativeArray;
    }

    /**
     * Generates a boxed {@code List} of {@code Double} from a primitive {@code List} of {@code Double}
     */
    public static List<Float> listDoubleToListFloat(List<Double> doubleList) {
        List<Float> floatList = new ArrayList<>();
        for (Double d : doubleList) floatList.add(d.floatValue());
        return floatList;
    }

    /**
     * Generates a boxed {@code List} of {@code Double} from a primitive {@code List} of {@code Double}
     */
    public static double[] listDoubleToArrayDouble(List<Double> doubleList) {
        Double[] doubleBoxedArray = doubleList.toArray(new Double[0]);
        double[] doublePrimativeArray = new double[doubleBoxedArray.length];
        for (int i = 0; i < doubleBoxedArray.length; i++) doublePrimativeArray[i] = doubleBoxedArray[i];
        return doublePrimativeArray;
    }
}
