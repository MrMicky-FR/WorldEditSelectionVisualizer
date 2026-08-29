package fr.mrmicky.worldeditselectionvisualizer.compat.v7;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils.RegionTransforms7;
import fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils.Vectors7;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NullMarked
public class RegionAdapter7 implements RegionAdapter {

    private final Region region;

    public RegionAdapter7(Region region) {
        this.region = Objects.requireNonNull(region, "region");
    }

    @Override
    public Vector3d getMinimumPoint() {
        return Vectors7.toVector3d(this.region.getMinimumPoint());
    }

    @Override
    public Vector3d getMaximumPoint() {
        return Vectors7.toVector3d(this.region.getMaximumPoint());
    }

    @Override
    public Vector3d getCenter() {
        return Vectors7.toVector3d(this.region.getCenter());
    }

    @Override
    public long getVolume() {
        return this.region.getVolume();
    }

    @Override
    public Vector3d getCuboidPos1() {
        if (!(this.region instanceof CuboidRegion)) {
            throw new UnsupportedOperationException();
        }

        return Vectors7.toVector3d(((CuboidRegion) this.region).getPos1());
    }

    @Override
    public Vector3d getCuboidPos2() {
        if (!(this.region instanceof CuboidRegion)) {
            throw new UnsupportedOperationException();
        }

        return Vectors7.toVector3d(((CuboidRegion) this.region).getPos2());
    }

    @Override
    public List<Vector3d> getPolygonalPoints() {
        if (!(this.region instanceof Polygonal2DRegion)) {
            throw new UnsupportedOperationException();
        }

        return ((Polygonal2DRegion) this.region).getPoints()
                .stream()
                .map(vec -> new Vector3d(vec.getX(), 0, vec.getZ()))
                .collect(Collectors.toList());
    }

    @Override
    public Vector3d getEllipsoidRadius() {
        if (!(this.region instanceof EllipsoidRegion)) {
            throw new UnsupportedOperationException();
        }

        return Vectors7.toVector3d(((EllipsoidRegion) this.region).getRadius());
    }

    @Override
    public List<Vector3d[]> getConvexTriangles() {
        if (!(this.region instanceof ConvexPolyhedralRegion)) {
            throw new UnsupportedOperationException();
        }

        return ((ConvexPolyhedralRegion) this.region).getTriangles()
                .stream()
                .map(this::triangleToVectors)
                .collect(Collectors.toList());
    }

    @Override
    public List<Vector3d> getConvexVertices() {
        if (!(this.region instanceof ConvexPolyhedralRegion)) {
            throw new UnsupportedOperationException();
        }

        return ((ConvexPolyhedralRegion) this.region).getVertices()
                .stream()
                .map(Vectors7::toVector3d)
                .collect(Collectors.toList());
    }

    @Override
    public Region transform(Transform transform, Vector3d origin) {
        Vector3 originVec = Vectors7.toVector3(origin);

        return RegionTransforms7.originTransform(this.region, transform, originVec);
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    private Vector3d[] triangleToVectors(Triangle triangle) {
        Vector3d[] vectors = new Vector3d[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors7.toVector3d(triangle.getVertex(i));
        }

        return vectors;
    }
}
