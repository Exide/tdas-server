package org.arabellan.tdas.utils;

public class MathConverter {

    public static double degreesToRadians(double degrees) {
        return degrees * (Math.PI / 180);
    }

    public static double radiansToDegress(double radians) {
        return radians * (180 / Math.PI);
    }

}
