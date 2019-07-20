package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;

public class FawePolyhedralProcessor extends AbstractConvexProcessor<PolyhedralRegion> {

    public FawePolyhedralProcessor(WorldEditSelectionVisualizer plugin) {
        super(PolyhedralRegion.class, plugin);
    }

    @Override
    protected void processSelection(SelectionPoints selection, PolyhedralRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        createTriangles(selection.primary(), regionAdapter.getConvexTriangles(true), config.primary().getPointsDistance());
    }

    public static Class<? extends Region> getRegionClass() {
        return PolyhedralRegion.class;
    }
}
