package fr.mrmicky.worldeditselectionvisualizer.selection.shape;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class ShapeProcessor<R extends Region> {

    public static final double DOUBLE_PI = 2 * Math.PI;

    private final Class<R> regionClass;
    private final WorldEditSelectionVisualizer plugin;

    protected ShapeProcessor(Class<R> regionClass, WorldEditSelectionVisualizer plugin) {
        this.regionClass = regionClass;
        this.plugin = plugin;
    }

    @NotNull
    public SelectionPoints processSelection(RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        SelectionPoints selectionInfo = new SelectionPoints();

        processSelection(selectionInfo, regionClass.cast(regionAdapter.getRegion()), regionAdapter, config);

        return selectionInfo;
    }

    protected abstract void processSelection(SelectionPoints selection, R region, RegionAdapter regionAdapter, GlobalSelectionConfig config);

    protected WorldEditSelectionVisualizer getPlugin() {
        return plugin;
    }

    protected void createLine(Collection<ImmutableVector> vectors, ImmutableVector start, ImmutableVector end, double distance) {
        double length = start.distance(end);
        int points = (int) (length / distance) + 1;

        double gap = length / (points - 1);
        ImmutableVector gapVector = end.subtract(start).normalize().multiply(gap);

        for (int i = 0; i < points; i++) {
            vectors.add(start.add(gapVector.multiply(i)));
        }
    }

    protected void createEllipse(Collection<ImmutableVector> vectors, ImmutableVector center, ImmutableVector radius, double distance) {
        double maxRadius = Math.max(radius.getX(), Math.max(radius.getY(), radius.getZ()));
        double deltaTheta = distance / (maxRadius * DOUBLE_PI);

        for (double i = 0.0; i < 1.0; i += deltaTheta) {
            double angle = i * DOUBLE_PI;

            double x = Math.cos(angle) * radius.getX();
            double z = Math.sin(angle) * radius.getZ();

            vectors.add(center.add(x, 0, z));
        }
    }

    protected void createLinesFromBottom(SelectionPoints selection, List<ImmutableVector> bottomCorners, int height, GlobalSelectionConfig config) {
        selection.primary().addAll(bottomCorners);

        double primaryDistance = config.primary().getPointsDistance();
        double secondaryDistance = config.secondary().getPointsDistance();

        double secondaryGap = config.secondary().getLinesGap();

        for (int i = 0; i < bottomCorners.size(); i++) {
            ImmutableVector bottomMin = bottomCorners.get(i);
            ImmutableVector bottomMax = bottomCorners.get(i < bottomCorners.size() - 1 ? i + 1 : 0);
            ImmutableVector topMin = bottomMin.add(0, height, 0);
            ImmutableVector topMax = bottomMax.add(0, height, 0);

            createLine(selection.primary(), bottomMin, bottomMax, primaryDistance);
            createLine(selection.primary(), bottomMin, topMin, primaryDistance);
            createLine(selection.primary(), topMin, topMax, primaryDistance);

            if (secondaryGap > 0) {
                for (double offset = secondaryGap; offset < height; offset += secondaryGap) {
                    ImmutableVector linePointMin = bottomMin.add(0.0, offset, 0.0);
                    ImmutableVector linePointMax = bottomMax.add(0.0, offset, 0.0);

                    createLine(selection.secondary(), linePointMin, linePointMax, secondaryDistance);
                }
            }
        }
    }
}
