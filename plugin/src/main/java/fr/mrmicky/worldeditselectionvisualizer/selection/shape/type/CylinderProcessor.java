package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.CylinderRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.ShapeProcessor;

import java.util.Collection;

public class CylinderProcessor extends ShapeProcessor<CylinderRegion> {

    public CylinderProcessor(WorldEditSelectionVisualizer plugin) {
        super(CylinderRegion.class, plugin);
    }

    @Override
    protected void processSelection(SelectionPoints selection, CylinderRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        Vector3d min = regionAdapter.getMinimumPoint();
        Vector3d max = regionAdapter.getMaximumPoint().add(1, 1, 1);
        Vector3d bottomCenter = regionAdapter.getCenter().withY(min.getY()).add(0.5, 0.0, 0.5);
        Vector3d topCenter = bottomCenter.withY(max.getY());

        Vector3d radius = new Vector3d(region.getWidth() / 2.0, 0, region.getLength() / 2.0);

        createEllipse(selection.primary(), bottomCenter, radius, config.primary().getPointsDistance());
        createEllipse(selection.primary(), topCenter, radius, config.primary().getPointsDistance());

        double increment = DOUBLE_PI / 8;
        for (int i = 0; i < 8; i++) {
            double angle = i * increment;
            double x = bottomCenter.getX() + (radius.getX() * Math.cos(angle));
            double z = bottomCenter.getZ() + (radius.getZ() * Math.sin(angle));

            Vector3d bottom = new Vector3d(x, bottomCenter.getY(), z);
            Vector3d top = bottom.withY(topCenter.getY());

            Collection<Vector3d> vectors = (i % 2 == 0) ? selection.primary() : selection.secondary();
            double distance = ((i % 2 == 0) ? config.primary() : config.secondary()).getPointsDistance();

            createLine(vectors, bottom, top, distance);

            createLine(vectors, bottom, bottomCenter, distance);
            createLine(vectors, top, topCenter, distance);
        }
    }
}
