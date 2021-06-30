package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

import java.util.List;

public class ConvexPolyhedralProcessor extends AbstractConvexProcessor<ConvexPolyhedralRegion> {

    public ConvexPolyhedralProcessor(WorldEditSelectionVisualizer plugin) {
        super(ConvexPolyhedralRegion.class, plugin);
    }

    @Override
    protected List<Vector3d[]> getTriangles(RegionAdapter region) {
        return region.getConvexTriangles();
    }
}
