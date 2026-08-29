package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.DisplayConfig;
import fr.mrmicky.worldeditselectionvisualizer.config.VisualizationConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Ellipse;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Point;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.geometry.VerticalEllipse;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class EllipsoidProcessor extends ShapeProcessor<EllipsoidRegion> {

    // The secondary ellipses are offset by half the Y radius, so the scale factor is
    // cos(asin(offsetY / radiusY)) = cos(asin(0.5)) = sqrt(3) / 2
    private static final double SECONDARY_RADIUS_FACTOR = Math.sqrt(3) / 2;
    private static final double INCREMENT = Math.PI / 4;

    public EllipsoidProcessor() {
        super(EllipsoidRegion.class);
    }

    @Override
    protected SelectionPoints processSelection(EllipsoidRegion region,
                                               RegionAdapter adapter,
                                               VisualizationConfig config) {
        List<Shape> primary = new ArrayList<>(5);
        List<Shape> secondary = new ArrayList<>(6);
        Vector3d radius = adapter.getEllipsoidRadius().add(0.5, 0.5, 0.5);
        Vector3d center = adapter.getCenter().add(0.5, 0.5, 0.5);

        primary.add(new Ellipse(center, radius, config.primary()));

        double offsetY = radius.getY() / 2.0;
        Vector3d offRadius = radius.multiply(SECONDARY_RADIUS_FACTOR).withY(0);

        secondary.add(new Ellipse(center.add(0, offsetY, 0), offRadius, config.secondary()));
        secondary.add(new Ellipse(center.subtract(0, offsetY, 0), offRadius, config.secondary()));

        for (int i = 0; i < 8; i++) {
            double cos = Math.cos(i * INCREMENT);
            double sin = Math.sin(i * INCREMENT);

            List<Shape> ellipses = (i % 2 == 0) ? primary : secondary;
            DisplayConfig localConfig = (i % 2 == 0) ? config.primary() : config.secondary();

            ellipses.add(new VerticalEllipse(center, radius.multiply(sin, 1, cos), localConfig));
        }

        return new SelectionPoints(primary, secondary, new Point(center));
    }
}
