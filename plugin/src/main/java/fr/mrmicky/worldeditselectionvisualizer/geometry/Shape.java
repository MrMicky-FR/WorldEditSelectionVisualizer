package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Shape {

    void render(Vector3d viewerOrigin, VectorRenderer renderer);

    @FunctionalInterface
    interface VectorRenderer {
        boolean render(double x, double y, double z);
    }
}
