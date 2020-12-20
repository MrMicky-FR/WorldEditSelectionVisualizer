package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.CuboidRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.ShapeProcessor;

import java.util.Arrays;
import java.util.List;

public class CuboidProcessor extends ShapeProcessor<CuboidRegion> {

    public CuboidProcessor(WorldEditSelectionVisualizer plugin) {
        super(CuboidRegion.class, plugin);
    }

    @Override
    public void processSelection(SelectionPoints selection, CuboidRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        Vector3d min = regionAdapter.getMinimumPoint();
        Vector3d max = regionAdapter.getMaximumPoint().add(1, 1, 1);
        int height = region.getHeight();
        int width = region.getWidth();
        int length = region.getLength();

        List<Vector3d> bottomCorners = Arrays.asList(
                min,
                min.withX(max.getX()),
                max.withY(min.getY()),
                min.withZ(max.getZ())
        );

        createLinesFromBottom(selection, bottomCorners, height, config);

        double lineGap = config.secondary().getLinesGap();
        double distance = config.secondary().getPointsDistance();

        if (lineGap > 0 && getPlugin().getConfig().getBoolean("cuboid-top-bottom")) {
            for (double offset = lineGap; offset < width; offset += lineGap) {
                createLine(selection.secondary(), min.add(offset, 0, 0), min.add(offset, 0, length), distance);
                createLine(selection.secondary(), min.add(offset, height, 0), min.add(offset, height, length), distance);
            }
        }
    }
}
