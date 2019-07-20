package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;
import fr.mrmicky.worldeditselectionvisualizer.selection.RegionInfos;

import java.util.List;

public interface RegionAdapter {

    ImmutableVector getMinimumPoint();

    ImmutableVector getMaximumPoint();

    ImmutableVector getCenter();

    List<ImmutableVector> getPolygonalPoints();

    ImmutableVector getEllipsoidRadius();

    default List<ImmutableVector[]> getConvexTriangles() {
        return getConvexTriangles(false);
    }

    List<ImmutableVector[]> getConvexTriangles(boolean faweSupport);

    Region getRegion();

    default RegionInfos getRegionsInfos() {
        return new RegionInfos(this);
    }

}
