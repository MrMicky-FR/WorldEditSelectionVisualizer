package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

public class Point implements Shape{

    private final Vector3d point;

    public Point(Vector3d point) {
        this.point = point;
    }

    @Override
    public void render(VectorRenderer renderer) {
        renderer.render(point.getX(), point.getY(), point.getZ());
    }
}
