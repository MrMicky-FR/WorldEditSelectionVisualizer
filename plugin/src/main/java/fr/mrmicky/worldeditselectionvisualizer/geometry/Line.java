package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.DisplayConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Line implements Shape {

    private final Vector3d start;
    private final Vector3d direction;
    private final Vector3d gap;
    private final int points;
    private final double gapLength;

    public Line(Vector3d start, Vector3d end, DisplayConfig config) {
        this.start = start;
        this.direction = end.subtract(start);
        double length = start.distance(end);
        int segments = (int) (length / config.getPointsDistance());
        this.points = (length % config.getPointsDistance() == 0) ? segments : segments + 1;
        this.gap = end.subtract(start).normalize().multiply(length / Math.max(1, segments));
        this.gapLength = length / Math.max(1, segments);
    }

    @Override
    public void render(Vector3d viewerOrigin, VectorRenderer renderer) {
        // Find the closest point on the line to the viewerOrigin
        double t = viewerOrigin.subtract(this.start).dot(this.direction) / this.direction.lengthSquared();
        Vector3d closest = this.start.add(this.direction.multiply(Math.min(Math.max(t, 0), 1)));

        // Find the closest particle point
        int closestIndex = (int) Math.round(this.start.distance(closest) / this.gapLength);

        // Render toward the end
        for (int i = closestIndex; i < this.points; i++) {
            double x = this.start.getX() + this.gap.getX() * i;
            double y = this.start.getY() + this.gap.getY() * i;
            double z = this.start.getZ() + this.gap.getZ() * i;

            if (!renderer.render(x, y, z)) {
                break;
            }
        }

        // Render toward the start
        for (int i = closestIndex - 1; i >= 0; i--) {
            double x = this.start.getX() + this.gap.getX() * i;
            double y = this.start.getY() + this.gap.getY() * i;
            double z = this.start.getZ() + this.gap.getZ() * i;

            if (!renderer.render(x, y, z)) {
                break;
            }
        }
    }
}
