package com.github.rjbx.calibrater;

/**
 * Utility class for converting between decimal array types
 */
public final class TypeConverters {

    /**
     * Generates a primitive {@code List} of {@code Double} from a primitive {@code float} array
     */
    public static double[] arrayFloatToDouble(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) doubleArray[i] = floatArray[i];
        return doubleArray;
    }

    /**
     * Generates a primitive {@code List} of {@code Double} from a boxed {@code Float} array
     */
    public static double[] arrayFloatBoxedToDouble(Float[] floatBoxedArray) {
        double[] doubleArray = new double[floatBoxedArray.length];
        for (int i = 0; i < floatBoxedArray.length; i++) doubleArray[i] = floatBoxedArray[i];
        return doubleArray;
    }

    /**
     * Generates a primitive {@code List} of {@code Double} from a boxed {@code List} of {@code Double} array
     */
    public static double[] arrayBoxedToPrimitiveDouble(Double[] doubleBoxedArray) {
        double[] doubleArray = new double[doubleBoxedArray.length];
        for (int i = 0; i < doubleBoxedArray.length; i++) doubleArray[i] = doubleBoxedArray[i];
        return doubleArray;
    }

    /**
     * Generates a primitive {@code float} array from a primitive {@code List} of {@code Double}
     */
    public static float[] arrayDoubleToFloat(double[] doubleArray) {
        float[] floatArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) floatArray[i] = (float) doubleArray[i];
        return floatArray;
    }

    /**
     * Generates a boxed {@code Float} array from a primitive {@code List} of {@code Double}
     */
    public static Float[] arrayDoubleToFloatBoxed(double[] doubleArray) {
        Float[] floatBoxedArray = new Float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) floatBoxedArray[i] = (float) doubleArray[i];
        return floatBoxedArray;
    }

    /**
     * Generates a boxed {@code List} of {@code Double} from a primitive {@code List} of {@code Double}
     */
    public static Double[] arrayPrimitiveToBoxedDouble(double[] doubleArray) {
        Double[] doubleBoxedArray = new Double[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) doubleBoxedArray[i] = doubleArray[i];
        return doubleBoxedArray;
    }
}
