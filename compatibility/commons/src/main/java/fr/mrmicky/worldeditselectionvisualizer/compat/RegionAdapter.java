package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.RegionInfo;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface RegionAdapter {

    Vector3d getMinimumPoint();

    Vector3d getMaximumPoint();

    Vector3d getCenter();

    long getVolume();

    Vector3d getCuboidPos1();

    Vector3d getCuboidPos2();

    List<Vector3d> getPolygonalPoints();

    Vector3d getEllipsoidRadius();

    List<Vector3d[]> getConvexTriangles();

    List<Vector3d> getConvexVertices();

    Region transform(Transform transform, Vector3d origin);

    Region getRegion();

    default RegionInfo getRegionInfo() {
        return new RegionInfo(this);
    }
}
