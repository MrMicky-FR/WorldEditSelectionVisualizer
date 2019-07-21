package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.RegionInfos;

import java.util.List;

public interface RegionAdapter {

    Vector3d getMinimumPoint();

    Vector3d getMaximumPoint();

    Vector3d getCenter();

    List<Vector3d> getPolygonalPoints();

    Vector3d getEllipsoidRadius();

    default List<Vector3d[]> getConvexTriangles() {
        return getConvexTriangles(false);
    }

    List<Vector3d[]> getConvexTriangles(boolean faweSupport);

    Region getRegion();

    default RegionInfos getRegionsInfos() {
        return new RegionInfos(this);
    }

}
