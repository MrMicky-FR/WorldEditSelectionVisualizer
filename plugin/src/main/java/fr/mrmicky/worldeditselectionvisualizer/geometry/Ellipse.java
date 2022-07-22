package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ellipse implements Shape {

    private final @NotNull Vector3d center;
    private final int points;
    private final double[] x;
    private final double[] z;

    public Ellipse(Vector3d center, Vector3d radius, SelectionConfig config) {
        this.center = Objects.requireNonNull(center, "center");

        double maxRadius = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
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
    public void render(@NotNull VectorRenderer renderer) {
        for (int i = 0; i < this.points; i++) {
            renderer.render(this.x[i], this.center.getY(), this.z[i]);
        }
    }
}
