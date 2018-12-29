package com.rojel.wesv;

import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * @author MrMicky
 */
public class ImmutableVector {

    private final double x;
    private final double y;
    private final double z;

    public ImmutableVector(final Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public ImmutableVector(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public ImmutableVector withX(double x) {
        return new ImmutableVector(x, y, z);
    }

    public double getY() {
        return y;
    }

    public ImmutableVector withY(double y) {
        return new ImmutableVector(x, y, z);
    }

    public double getZ() {
        return z;
    }

    public ImmutableVector withZ(double z) {
        return new ImmutableVector(x, y, z);
    }

    public ImmutableVector add(ImmutableVector vec) {
        return add(vec.getX(), vec.getY(), vec.getZ());
    }

    public ImmutableVector add(double x, double y, double z) {
        return new ImmutableVector(this.x + x, this.y + y, this.z + z);
    }

    public ImmutableVector subtract(ImmutableVector vec) {
        return subtract(vec.getX(), vec.getY(), vec.getZ());
    }

    public ImmutableVector subtract(double x, double y, double z) {
        return new ImmutableVector(this.x - x, this.y - y, this.z - z);
    }

    public ImmutableVector multiply(double n) {
        return multiply(n, n, n);
    }

    public ImmutableVector multiply(ImmutableVector vec) {
        return multiply(vec.getX(), vec.getY(), vec.getZ());
    }

    public ImmutableVector multiply(double x, double y, double z) {
        return new ImmutableVector(this.x * x, this.y * y, this.z * z);
    }

    public ImmutableVector divide(double n) {
        return divide(n, n, n);
    }

    public ImmutableVector divide(ImmutableVector vec) {
        return divide(vec.getX(), vec.getY(), vec.getZ());
    }

    public ImmutableVector divide(double x, double y, double z) {
        return new ImmutableVector(this.x / x, this.y / y, this.z / z);
    }

    public ImmutableVector normalize() {
        return divide(lenght());
    }

    public double distance(ImmutableVector vec) {
        return distance(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    public double distanceSquared(ImmutableVector vec) {
        return distanceSquared(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distanceSquared(double x, double y, double z) {
        return NumberConversions.square(this.x - x) + NumberConversions.square(this.y - y) + NumberConversions.square(this.z - z);
    }

    public double lenght() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ImmutableVector)) {
            return false;
        }

        ImmutableVector vector = (ImmutableVector) o;
        return x == vector.getX() && y == vector.getY() && z == vector.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "ImmutableVector{x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
