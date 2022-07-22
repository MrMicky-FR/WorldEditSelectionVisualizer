package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.CylinderRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.config.SelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Ellipse;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Line;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Point;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;

import java.util.ArrayList;
import java.util.List;

public class CylinderProcessor extends ShapeProcessor<CylinderRegion> {

    private static final double INCREMENT = Math.PI / 4;

    public CylinderProcessor(WorldEditSelectionVisualizer plugin) {
        super(CylinderRegion.class, plugin);
    }

    @Override
    protected SelectionPoints processSelection(CylinderRegion region, RegionAdapter adapter, GlobalSelectionConfig config) {
        Vector3d min = adapter.getMinimumPoint();
        Vector3d max = adapter.getMaximumPoint().add(1, 1, 1);
        Vector3d radius = new Vector3d(region.getWidth() / 2.0, 0, region.getLength() / 2.0);
        Vector3d bottomCenter = adapter.getCenter().withY(min.getY()).add(0.5, 0.0, 0.5);
        Vector3d topCenter = bottomCenter.withY(max.getY());
        Vector3d middleCenter = bottomCenter.add(0, region.getHeight() / 2.0, 0);
        List<Shape> primary = new ArrayList<>(14); // 2 + 3 * 4
        List<Shape> secondary = new ArrayList<>(12); // 3 * 4

        primary.add(new Ellipse(bottomCenter, radius, config.primary()));
        primary.add(new Ellipse(topCenter, radius, config.primary()));

        for (int i = 0; i < 8; i++) {
            double x = radius.getX() * Math.cos(i * INCREMENT);
            double z = radius.getZ() * Math.sin(i * INCREMENT);

            Vector3d bottom = bottomCenter.add(x, 0, z);
            Vector3d top = bottom.withY(topCenter.getY());

            List<Shape> lines = (i % 2 == 0) ? primary : secondary;
            SelectionConfig localConfig = ((i % 2 == 0) ? config.primary() : config.secondary());

            lines.add(new Line(bottom, top, localConfig));
            lines.add(new Line(bottom, bottomCenter, localConfig));
            lines.add(new Line(top, topCenter, localConfig));
        }

        return new SelectionPoints(primary, secondary, new Point(middleCenter));
    }
}
