package fr.mrmicky.worldeditselectionvisualizer.geometry;

import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

public class Line implements Shape {

    private final Vector3d start;
    private final Vector3d end;
    private final Vector3d gap;
    private final int points;

    public Line(Vector3d start, Vector3d end, SelectionConfig config) {
        this.start = start;
        this.end = end;
        double length = start.distance(end);
        int count = (int) (length / config.getPointsDistance());
        this.points = (length % config.getPointsDistance() == 0) ? count : count + 1;
        this.gap = end.subtract(start).normalize().multiply(length / Math.max(1, count));
    }

    @Override
    public void render(VectorRenderer renderer) {
        for (int i = 0; i < this.points; i++) {
            double x = this.start.getX() + this.gap.getX() * i;
            double y = this.start.getY() + this.gap.getY() * i;
            double z = this.start.getZ() + this.gap.getZ() * i;

            renderer.render(x, y, z);
        }
    }

    public Vector3d getStart() {
        return this.start;
    }

    public Vector3d getEnd() {
        return this.end;
    }
}
