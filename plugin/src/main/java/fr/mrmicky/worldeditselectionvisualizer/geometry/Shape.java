package fr.mrmicky.worldeditselectionvisualizer.geometry;

@FunctionalInterface
public interface Shape {

    double TWO_PI = 2 * Math.PI;

    void render(VectorRenderer renderer);

    @FunctionalInterface
    interface VectorRenderer {
        void render(double x, double y, double z);
    }
}
