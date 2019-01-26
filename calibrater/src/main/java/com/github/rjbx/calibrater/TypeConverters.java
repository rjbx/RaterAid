package com.github.rjbx.calibrater;

public final class TypeConverters {
    
    public static double[] arrayFloatToDouble(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) doubleArray[i] = floatArray[i];
        return doubleArray;
    }

    public static double[] arrayFloatBoxedToDouble(Float[] floatBoxedArray) {
        double[] doubleArray = new double[floatBoxedArray.length];
        for (int i = 0; i < floatBoxedArray.length; i++) doubleArray[i] = floatBoxedArray[i];
        return doubleArray;
    }

    public static double[] arrayBoxedToPrimitiveDouble(Double[] doubleBoxedArray) {
        double[] doubleArray = new double[doubleBoxedArray.length];
        for (int i = 0; i < doubleBoxedArray.length; i++) doubleArray[i] = doubleBoxedArray[i];
        return doubleArray;
    }

    public static float[] arrayDoubleToFloat(double[] doubleArray) {
        float[] floatArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) floatArray[i] = (float) doubleArray[i];
        return floatArray;
    }

    public static Float[] arrayDoubleToFloatBoxed(double[] doubleArray) {
        Float[] floatBoxedArray = new Float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) floatBoxedArray[i] = (float) doubleArray[i];
        return floatBoxedArray;
    }

    public static Double[] arrayPrimitiveToBoxedDouble(double[] doubleArray) {
        Double[] doubleBoxedArray = new Double[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) doubleBoxedArray[i] = doubleArray[i];
        return doubleBoxedArray;
    }
}
