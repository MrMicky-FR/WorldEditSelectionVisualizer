package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.DisplayConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class VerticalEllipse implements Shape {

    private static final double TWO_PI = 2 * Math.PI;

    private final Vector3d center;
    private final Vector3d radius;
    private final int points;
    private final double[] cos;
    private final double[] y;

    public VerticalEllipse(Vector3d center, Vector3d radius, DisplayConfig config) {
        this.center = Objects.requireNonNull(center, "center");
        this.radius = Objects.requireNonNull(radius, "radius");

        double maxRadius = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        this.points = Math.abs((int) Math.round((maxRadius * TWO_PI) / config.getPointsDistance()));
        double increment = TWO_PI / Math.max(1, this.points);

        this.cos = new double[this.points];
        this.y = new double[this.points];

        for (int i = 0; i < this.points; i++) {
            double angle = i * increment;
            this.cos[i] = Math.cos(angle);
            this.y[i] = center.getY() + Math.sin(angle) * radius.getY();
        }
    }

    @Override
    public void render(Vector3d viewerOrigin, VectorRenderer renderer) {
        for (int i = 0; i < this.points; i++) {
            double x = this.center.getX() + this.cos[i] * this.radius.getX();
            double z = this.center.getZ() + this.cos[i] * this.radius.getZ();

            renderer.render(x, this.y[i], z);
        }
    }
}
