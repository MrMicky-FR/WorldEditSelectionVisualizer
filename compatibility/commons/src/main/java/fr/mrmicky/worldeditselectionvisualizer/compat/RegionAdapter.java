package fr.mrmicky.worldeditselectionvisualizer.compat;

import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.RegionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RegionAdapter {

    @NotNull Vector3d getMinimumPoint();

    @NotNull Vector3d getMaximumPoint();

    @NotNull Vector3d getCenter();

    long getVolume();

    @NotNull Vector3d getCuboidPos1();

    @NotNull Vector3d getCuboidPos2();

    @NotNull List<Vector3d> getPolygonalPoints();

    @NotNull Vector3d getEllipsoidRadius();

    @NotNull List<Vector3d[]> getConvexTriangles();

    @NotNull List<Vector3d> getConvexVertices();

    @NotNull Region transform(@NotNull Transform transform,
                              @NotNull Vector3d origin);

    void shift(@NotNull Vector3d vector) throws RegionOperationException;

    @NotNull Region getRegion();

    default @NotNull RegionInfo getRegionInfo() {
        return new RegionInfo(this);
    }
}
