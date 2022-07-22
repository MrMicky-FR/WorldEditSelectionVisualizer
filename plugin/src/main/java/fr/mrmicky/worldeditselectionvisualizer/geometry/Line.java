package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

public class Line implements Shape {

    private final @NotNull Vector3d start;
    private final @NotNull Vector3d gap;
    private final int points;

    public Line(Vector3d start, Vector3d end, SelectionConfig config) {
        this.start = start;
        double length = start.distance(end);
        int count = (int) (length / config.getPointsDistance());
        this.points = (length % config.getPointsDistance() == 0) ? count : count + 1;
        this.gap = end.subtract(start).normalize().multiply(length / Math.max(1, count));
    }

    @Override
    public void render(@NotNull VectorRenderer renderer) {
        for (int i = 0; i < this.points; i++) {
            double x = this.start.getX() + this.gap.getX() * i;
            double y = this.start.getY() + this.gap.getY() * i;
            double z = this.start.getZ() + this.gap.getZ() * i;

            renderer.render(x, y, z);
        }
    }
}
