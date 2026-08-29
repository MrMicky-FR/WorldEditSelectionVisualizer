package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class Point implements Shape {

    private final Vector3d point;

    public Point(Vector3d point) {
        this.point = Objects.requireNonNull(point, "point");
    }

    @Override
    public void render(Vector3d viewerOrigin, VectorRenderer renderer) {
        renderer.render(this.point.getX(), this.point.getY(), this.point.getZ());
    }
}
