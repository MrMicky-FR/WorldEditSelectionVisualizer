package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

import java.util.List;

public class FawePolyhedralProcessor extends AbstractConvexProcessor<PolyhedralRegion> {

    public FawePolyhedralProcessor(WorldEditSelectionVisualizer plugin) {
        super(PolyhedralRegion.class, plugin);
    }

    @Override
    protected List<Vector3d[]> getTriangles(RegionAdapter region) {
        return region.getConvexTriangles(true);
    }

    public static Class<? extends Region> getRegionClass() {
        return PolyhedralRegion.class;
    }
}
