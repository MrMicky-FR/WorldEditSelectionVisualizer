package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.DisplayConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class Ellipse implements Shape {

    private static final double TWO_PI = 2 * Math.PI;

    private final Vector3d center;
    private final int points;
    private final double[] x;
    private final double[] z;

    public Ellipse(Vector3d center, Vector3d radius, DisplayConfig config) {
        this.center = Objects.requireNonNull(center, "center");

        double maxRadius = Math.max(radius.getX(), radius.getZ());
        this.points = Math.abs((int) Math.round((maxRadius * TWO_PI) / config.getPointsDistance()));
        double increment = TWO_PI / Math.max(1, this.points);

        this.x = new double[this.points];
        this.z = new double[this.points];

        for (int i = 0; i < this.points; i++) {
            double angle = i * increment;
            this.x[i] = center.getX() + Math.cos(angle) * radius.getX();
            this.z[i] = center.getZ() + Math.sin(angle) * radius.getZ();
        }
    }

    @Override
    public void render(Vector3d viewerOrigin, VectorRenderer renderer) {
        for (int i = 0; i < this.points; i++) {
            renderer.render(this.x[i], this.center.getY(), this.z[i]);
        }
    }
}
