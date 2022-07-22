package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.CuboidRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Line;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Point;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CuboidProcessor extends ShapeProcessor<CuboidRegion> {

    public CuboidProcessor(WorldEditSelectionVisualizer plugin) {
        super(CuboidRegion.class, plugin);
    }

    @Override
    protected SelectionPoints processSelection(CuboidRegion region,
                                               RegionAdapter adapter,
                                               GlobalSelectionConfig config) {
        List<Shape> primary = new ArrayList<>(12);
        List<Shape> secondary = new ArrayList<>();
        Vector3d min = adapter.getMinimumPoint();
        Vector3d max = adapter.getMaximumPoint().add(1, 1, 1);
        Vector3d origin = adapter.getCuboidPos1().add(0.5, 0.5, 0.5);
        int height = region.getHeight();
        double lineGap = config.secondary().getLinesGap();

        List<Vector3d> bottomCorners = Arrays.asList(
                min,
                min.withX(max.getX()),
                max.withY(min.getY()),
                min.withZ(max.getZ())
        );

        createLinesFromBottom(primary, secondary, bottomCorners, height, config);

        if (lineGap > 0 && getPlugin().getConfig().getBoolean("cuboid-top-bottom")) {
            int width = region.getWidth();
            for (double offset = lineGap; offset < width; offset += lineGap) {
                Vector3d start = min.add(offset, 0, 0);
                Vector3d startTop = start.add(0, height, 0);
                Vector3d end = min.add(offset, 0, region.getLength());
                Vector3d endTop = end.add(0, height, 0);

                secondary.add(new Line(start, end, config.secondary()));
                secondary.add(new Line(startTop, endTop, config.secondary()));
            }
        }

        return new SelectionPoints(primary, secondary, new Point(origin));
    }
}
