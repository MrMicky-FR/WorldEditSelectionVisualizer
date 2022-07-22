package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Point implements Shape {

    private final @NotNull Vector3d point;

    public Point(Vector3d point) {
        this.point = Objects.requireNonNull(point, "point");
    }

    @Override
    public void render(@NotNull VectorRenderer renderer) {
        renderer.render(this.point.getX(), this.point.getY(), this.point.getZ());
    }
}
