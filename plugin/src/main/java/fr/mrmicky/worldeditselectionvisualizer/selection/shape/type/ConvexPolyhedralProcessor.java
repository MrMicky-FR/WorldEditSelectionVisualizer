package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConvexPolyhedralProcessor extends AbstractConvexProcessor<ConvexPolyhedralRegion> {

    public ConvexPolyhedralProcessor(WorldEditSelectionVisualizer plugin) {
        super(ConvexPolyhedralRegion.class, plugin);
    }

    @Override
    protected void processSelection(SelectionPoints selection, ConvexPolyhedralRegion region, RegionAdapter regionAdapter, GlobalSelectionConfig config) {
        selection.primaryPositions().addAll(regionAdapter.getPolyhedralVertices());
        createTriangles(selection.primary(), regionAdapter.getConvexTriangles(), config.primary().getPointsDistance());
    }
}
