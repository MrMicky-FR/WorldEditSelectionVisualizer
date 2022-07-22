package fr.mrmicky.worldeditselectionvisualizer.geometry;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Shape {

    double TWO_PI = 2 * Math.PI;

    void render(@NotNull VectorRenderer renderer);

    @FunctionalInterface
    interface VectorRenderer {
        void render(double x, double y, double z);
    }
}
