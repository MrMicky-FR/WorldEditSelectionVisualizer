package fr.mrmicky.worldeditselectionvisualizer.math;

import org.bukkit.util.Vector;

public class Vector3d {

    public static final Vector3d ZERO = new Vector3d(0, 0, 0);

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
        return this.x;
    }

    public Vector3d withX(double x) {
        return new Vector3d(x, this.y, this.z);
    }

    public double getY() {
        return this.y;
    }

    public Vector3d withY(double y) {
        return new Vector3d(this.x, y, this.z);
    }

    public double getZ() {
        return this.z;
    }

    public Vector3d withZ(double z) {
        return new Vector3d(this.x, this.y, z);
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
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3d floor() {
        return new Vector3d(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public Vector3d ceil() {
        return new Vector3d(Math.ceil(this.x), Math.ceil(this.y), Math.ceil(this.z));
    }

    public Vector3d round() {
        return new Vector3d(Math.floor(this.x + 0.5), Math.floor(this.y + 0.5), Math.floor(this.z + 0.5));
    }

    public Vector3d abs() {
        return new Vector3d(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
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
        return this.x == vec.getX() && this.y == vec.getY() && this.z == vec.getZ();
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + Double.hashCode(this.x);
        hash = 31 * hash + Double.hashCode(this.y);
        hash = 31 * hash + Double.hashCode(this.z);
        return hash;
    }

    @Override
    public String toString() {
        return "Vector3d{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
    }
}
