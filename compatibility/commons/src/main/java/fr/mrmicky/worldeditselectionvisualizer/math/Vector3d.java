package fr.mrmicky.worldeditselectionvisualizer.math;

import org.bukkit.util.Vector;

public class Vector3d {

    public static final Vector3d ZERO = new Vector3d(0, 0, 0);
    public static final Vector3d ONE = new Vector3d(1, 1, 1);

    private final double x;
    private final double y;
    private final double z;

    public Vector3d(Vector vec) {
        this(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public Vector3d withX(double x) {
        return new Vector3d(x, y, z);
    }

    public double getY() {
        return y;
    }

    public Vector3d withY(double y) {
        return new Vector3d(x, y, z);
    }

    public double getZ() {
        return z;
    }

    public Vector3d withZ(double z) {
        return new Vector3d(x, y, z);
    }

    public Vector3d add(Vector3d vec) {
        return add(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d subtract(Vector3d vec) {
        return subtract(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3d subtract(double x, double y, double z) {
        return new Vector3d(this.x - x, this.y - y, this.z - z);
    }

    public Vector3d multiply(double n) {
        return multiply(n, n, n);
    }

    public Vector3d multiply(Vector3d vec) {
        return multiply(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3d multiply(double x, double y, double z) {
        return new Vector3d(this.x * x, this.y * y, this.z * z);
    }

    public Vector3d divide(double n) {
        return divide(n, n, n);
    }

    public Vector3d divide(Vector3d vec) {
        return divide(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3d divide(double x, double y, double z) {
        return new Vector3d(this.x / x, this.y / y, this.z / z);
    }

    public Vector3d normalize() {
        return divide(length());
    }

    public double distance(Vector3d vec) {
        return distance(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distance(double x, double y, double z) {
        return Math.sqrt(distanceSquared(x, y, z));
    }

    public double distanceSquared(Vector3d vec) {
        return distanceSquared(vec.getX(), vec.getY(), vec.getZ());
    }

    public double distanceSquared(double x, double y, double z) {
        double dx = this.x - x;
        double dy = this.y - y;
        double dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public Vector3d floor() {
        return new Vector3d(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public Vector3d ceil() {
        return new Vector3d(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    public Vector3d round() {
        return new Vector3d(Math.floor(x + 0.5), Math.floor(y + 0.5), Math.floor(z + 0.5));
    }

    public Vector3d abs() {
        return new Vector3d(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Vector3d)) {
            return false;
        }

        Vector3d vec = (Vector3d) o;
        return x == vec.getX() && y == vec.getY() && z == vec.getZ();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + Double.hashCode(x);
        hash = 31 * hash + Double.hashCode(y);
        hash = 31 * hash + Double.hashCode(z);
        return hash;
    }

    @Override
    public String toString() {
        return "Vector3d{x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
