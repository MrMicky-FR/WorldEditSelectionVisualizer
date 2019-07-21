package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.EllipsoidRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.ShapeProcessor;

public class EllipsoidProcessor extends ShapeProcessor<EllipsoidRegion> {

    public EllipsoidProcessor(WorldEditSelectionVisualizer plugin) {
        super(EllipsoidRegion.class, plugin);
    }

    @Override
    protected void processSelection(SelectionPoints selection, EllipsoidRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        Vector3d radius = regionAdapter.getEllipsoidRadius().add(0.5, 0.5, 0.5);
        Vector3d center = regionAdapter.getCenter().add(0.5, 0.5, 0.5);

        selection.primary().add(center);

        double distance = config.primary().getPointsDistance();

        createEllipsoid(selection, center, radius, config);

        createEllipse(selection.primary(), center, radius, distance);

        double offsetY = radius.getY() / 2.0;

        Vector3d center1 = center.add(0, offsetY, 0);
        Vector3d center2 = center.subtract(0, offsetY, 0);
        Vector3d offsetRadius = radius.multiply(Math.cos(Math.asin(offsetY / radius.getY())));

        createEllipse(selection.secondary(), center1, offsetRadius, config.secondary().getPointsDistance());
        createEllipse(selection.secondary(), center2, offsetRadius, config.secondary().getPointsDistance());
    }

    private void createEllipsoid(SelectionPoints selection, Vector3d center, Vector3d radius, GlobalSelectionConfig config) {
        double maxRadius = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double deltaTheta = config.primary().getPointsDistance() / (maxRadius * DOUBLE_PI);
        double increment = DOUBLE_PI / 8;

        // TODO use secondary distance
        for (double i = 0.0; i < 1.0; i += deltaTheta) {
            double y = radius.getY() * Math.cos(i * DOUBLE_PI);

            for (int j = 0; j < 8; j++) {
                double x = radius.getX() * Math.cos(j * increment) * Math.sin(i * DOUBLE_PI);
                double z = radius.getZ() * Math.sin(j * increment) * Math.sin(i * DOUBLE_PI);

                (j % 2 == 0 ? selection.primary() : selection.secondary()).add(center.add(x, y, z));
            }
        }
    }

}
