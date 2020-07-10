package fr.mrmicky.worldeditselectionvisualizer.compat.v6;

import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.FaweAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.Vectors6;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegionAdapter6 implements RegionAdapter {

    @NotNull
    private final Region region;

    public RegionAdapter6(@NotNull Region region) {
        this.region = Objects.requireNonNull(region, "region");
    }

    @NotNull
    @Override
    public Vector3d getMinimumPoint() {
        return Vectors6.toVector3d(region.getMinimumPoint());
    }

    @NotNull
    @Override
    public Vector3d getMaximumPoint() {
        return Vectors6.toVector3d(region.getMaximumPoint());
    }

    @NotNull
    @Override
    public Vector3d getPos1() {
        if (region instanceof CuboidRegion)
            return convPos(((CuboidRegion) region).getPos1());
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Vector3d getPos2() {
        if (region instanceof CuboidRegion)
            return convPos(((CuboidRegion) region).getPos2());
        throw new UnsupportedOperationException();
    }

    private Vector3d convPos(Vector pos) {
        if (pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0)
            // This isn't ideal, as Vector3d.ZERO is a specific instance of Vector3d which denotes an uninitialized value.
            // WEv7 uses BlockVector3.ZERO for this purpose, while v6 does not. So in v6, we're accepting the low risk
            // possibility that someone actually did select 0,0,0 to be able to use ==.ZERO for uninitialized position.
            return Vector3d.ZERO;
        return Vectors6.toVector3d(pos);
    }

    @NotNull
    @Override
    public Vector3d getCenter() {
        return Vectors6.toVector3d(region.getCenter());
    }

    @NotNull
    @Override
    public List<Vector3d> getPolygonalPoints() {
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polygonalRegion = (Polygonal2DRegion) region;

            return polygonalRegion.getPoints().stream()
                    .map(vec -> new Vector3d(vec.getX(), 0, vec.getZ()))
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<Vector3d> getPolyhedralVertices() {
        if (region instanceof PolyhedralRegion) {
            PolyhedralRegion polyhedralRegion = (PolyhedralRegion) region;

            return polyhedralRegion.getVertices().stream()
                    .map(vec -> new Vector3d(vec.getX(), vec.getY(), vec.getZ()))
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Vector3d getEllipsoidRadius() {
        if (region instanceof EllipsoidRegion) {
            return Vectors6.toVector3d(((EllipsoidRegion) region).getRadius());
        }
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<Vector3d[]> getConvexTriangles(boolean faweSupport) {
        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion polygonalRegion = (ConvexPolyhedralRegion) region;

            return polygonalRegion.getTriangles().stream()
                    .map(this::triangleToVectors)
                    .collect(Collectors.toList());
        }

        if (faweSupport) {
            return FaweAdapter6.getConvexTriangles(region);
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public void shift(Vector3d vector) throws RegionOperationException {
        region.shift(Vectors6.toVector(vector));
    }

    @NotNull
    @Override
    public Region transform(Transform transform, Vector3d origin) {
        if (region instanceof CuboidRegion) {
            Vector originVector = Vectors6.toVector(origin);
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector pos1 = applyTransform(transform, originVector, cuboidRegion.getPos1());
            Vector pos2 = applyTransform(transform, originVector, cuboidRegion.getPos2());

            return new CuboidRegion(region.getWorld(), pos1, pos2);
        }

        return region.clone();
    }

    @NotNull
    @Override
    public Region getRegion() {
        return region;
    }

    @NotNull
    private Vector3d[] triangleToVectors(Triangle triangle) {
        Vector3d[] vectors = new Vector3d[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors6.toVector3d(triangle.getVertex(i));
        }

        return vectors;
    }

    private Vector applyTransform(Transform transform, Vector origin, Vector vector) {
        return transform.apply(vector.subtract(origin)).add(origin);
    }
}
