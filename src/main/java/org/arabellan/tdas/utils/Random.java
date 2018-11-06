package org.arabellan.tdas.utils;

public class Random {

    private static final java.util.Random r = new java.util.Random();

    public static double getDoubleBetween(double min, double max) {
        return r.nextDouble() * (max - min) + min;
    }

}
