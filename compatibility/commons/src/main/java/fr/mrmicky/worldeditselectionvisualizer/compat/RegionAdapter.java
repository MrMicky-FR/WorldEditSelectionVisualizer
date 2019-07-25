package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.RegionInfos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RegionAdapter {

    @NotNull
    Vector3d getMinimumPoint();

    @NotNull
    Vector3d getMaximumPoint();

    @NotNull
    Vector3d getCenter();

    List<Vector3d> getPolygonalPoints();

    @NotNull
    Vector3d getEllipsoidRadius();

    @NotNull
    default List<Vector3d[]> getConvexTriangles() {
        return getConvexTriangles(false);
    }

    @NotNull
    List<Vector3d[]> getConvexTriangles(boolean faweSupport);

    @NotNull
    Region transform(Transform transform);

    void shift(Vector3d vector) throws RegionOperationException;

    @NotNull
    Region getRegion();

    @NotNull
    default RegionInfos getRegionsInfos() {
        return new RegionInfos(this);
    }
}
