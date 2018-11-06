package org.arabellan.tdas.math;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vector {

    @Builder.Default
    double x = 0;

    @Builder.Default
    double y = 0;

    @Builder.Default
    double z = 0;

    public Vector() {
        this(0, 0, 0);
    }

    public Vector(double x, double y) {
        this(x, y, 0);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double magnitude() {
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public Vector add(double scalar) {
        return builder()
                .x(x + scalar)
                .y(y + scalar)
                .z(z + scalar)
                .build();
    }

    public Vector add(Vector other) {
        return builder()
                .x(x + other.x)
                .y(y + other.y)
                .z(z + other.z)
                .build();
    }

    public Vector subtract(double scalar) {
        return builder()
                .x(x - scalar)
                .y(y - scalar)
                .z(z - scalar)
                .build();
    }

    public Vector subtract(Vector other) {
        return builder()
                .x(x - other.x)
                .y(y - other.y)
                .z(z - other.z)
                .build();
    }

    public Vector multiply(double scalar) {
        return builder()
                .x(x * scalar)
                .y(y * scalar)
                .z(z * scalar)
                .build();
    }

    public Vector multiply(Vector other) {
        return builder()
                .x(x * other.x)
                .y(y * other.y)
                .z(z * other.z)
                .build();
    }

    public Vector divide(double scalar) {
        return builder()
                .x(x / scalar)
                .y(y / scalar)
                .z(z / scalar)
                .build();
    }

    public Vector divide(Vector other) {
        return builder()
                .x(x / other.x)
                .y(y / other.y)
                .z(z / other.z)
                .build();
    }

    public Vector normalize() {
        double magnitude = magnitude();
        return builder()
                .x(x/magnitude)
                .y(y/magnitude)
                .z(z/magnitude)
                .build();
    }

    public double dot(Vector other) {
        return (x * other.x) + (y * other.y) + (z * other.z);
    }

    public Vector cross(Vector other) {
        return builder()
                .x((y * other.z) - (z * other.y))
                .y((z * other.x) - (x * other.z))
                .z((x * other.y) - (y * other.x))
                .build();
    }

}
