package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Line;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ShapeProcessor<R extends Region> {

    private final Class<R> regionClass;
    private final WorldEditSelectionVisualizer plugin;

    protected ShapeProcessor(Class<R> regionClass, WorldEditSelectionVisualizer plugin) {
        this.regionClass = regionClass;
        this.plugin = plugin;
    }

    public @NotNull SelectionPoints processSelection(RegionAdapter regionAdapter,
                                                     GlobalSelectionConfig config) {
        R region = this.regionClass.cast(regionAdapter.getRegion());
        return processSelection(region, regionAdapter, config);
    }

    protected abstract SelectionPoints processSelection(R region, RegionAdapter adapter, GlobalSelectionConfig config);

    protected WorldEditSelectionVisualizer getPlugin() {
        return this.plugin;
    }

    protected void createLinesFromBottom(List<Shape> primary,
                                         List<Shape> secondary,
                                         List<Vector3d> bottomCorners,
                                         int height,
                                         GlobalSelectionConfig config) {
        double secondaryGap = config.secondary().getLinesGap();

        for (int i = 0; i < bottomCorners.size(); i++) {
            Vector3d bottomMin = bottomCorners.get(i);
            Vector3d bottomMax = bottomCorners.get(i < bottomCorners.size() - 1 ? i + 1 : 0);
            Vector3d topMin = bottomMin.add(0, height, 0);
            Vector3d topMax = bottomMax.add(0, height, 0);

            primary.add(new Line(bottomMin, bottomMax, config.primary()));
            primary.add(new Line(bottomMin, topMin, config.primary()));
            primary.add(new Line(topMin, topMax, config.primary()));

            if (secondaryGap <= 0) {
                continue;
            }

            for (double offset = secondaryGap; offset < height; offset += secondaryGap) {
                Vector3d linePointMin = bottomMin.add(0.0, offset, 0.0);
                Vector3d linePointMax = bottomMax.add(0.0, offset, 0.0);

                secondary.add(new Line(linePointMin, linePointMax, config.secondary()));
            }
        }
    }
}
