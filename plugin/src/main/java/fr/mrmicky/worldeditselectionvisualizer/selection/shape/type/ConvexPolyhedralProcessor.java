package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;

public class ConvexPolyhedralProcessor extends AbstractConvexProcessor<ConvexPolyhedralRegion> {

    public ConvexPolyhedralProcessor(WorldEditSelectionVisualizer plugin) {
        super(ConvexPolyhedralRegion.class, plugin);
    }

    @Override
    protected void processSelection(SelectionPoints selection, ConvexPolyhedralRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        createTriangles(selection.primary(), regionAdapter.getConvexTriangles(), config.primary().getPointsDistance());
    }
}
