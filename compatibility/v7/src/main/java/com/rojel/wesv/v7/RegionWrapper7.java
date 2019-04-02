package com.rojel.wesv.v7;

import com.rojel.wesv.ImmutableVector;
import com.rojel.wesv.RegionWrapper;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.polyhedron.Triangle;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author MrMicky
 */
public class RegionWrapper7 implements RegionWrapper {

    private final Region region;

    public RegionWrapper7(Region region) {
        this.region = Objects.requireNonNull(region, "region");
    }

    @Override
    public ImmutableVector getMinimumPoint() {
        return toImmutableVector(region.getMinimumPoint());
    }

    @Override
    public ImmutableVector getMaximumPoint() {
        return toImmutableVector(region.getMaximumPoint());
    }

    @Override
    public ImmutableVector getCenter() {
        return toImmutableVector(region.getCenter());
    }

    @Override
    public List<ImmutableVector> getPolygonalRegionPoints() {
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polygonalRegion = (Polygonal2DRegion) region;

            return polygonalRegion.getPoints().stream()
                    .map(BlockVector2::toBlockVector3)
                    .map(this::toImmutableVector)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException();
    }

    @Override
    public ImmutableVector getEllipsoidRegionRadius() {
        if (region instanceof EllipsoidRegion) {
            return toImmutableVector(((EllipsoidRegion) region).getRadius());
        }
        throw new IllegalArgumentException();
    }

    @Override
    public List<ImmutableVector[]> getConvexRegionTriangles() {
        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion polygonalRegion = (ConvexPolyhedralRegion) region;

            return polygonalRegion.getTriangles().stream()
                    .map(this::triangleToImmutableVectors)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException();
    }

    @Override
    public Iterator<ImmutableVector> iterator() {
        Iterator<BlockVector3> blockImmutableVector3Iterator = region.iterator();

        return new Iterator<ImmutableVector>() {
            @Override
            public boolean hasNext() {
                return blockImmutableVector3Iterator.hasNext();
            }

            @Override
            public ImmutableVector next() {
                return toImmutableVector(blockImmutableVector3Iterator.next());
            }
        };
    }

    @Override
    public Region getRegion() {
        return region;
    }

    @Override
    public boolean regionEquals(Region region1) {
        return region1 != null
                && region.getWidth() == region1.getWidth()
                && region.getHeight() == region1.getHeight()
                && region.getArea() == region1.getArea()
                && region.getMinimumPoint().equals(region1.getMinimumPoint());
    }

    @Override
    public String toString() {
        return region.toString();
    }

    private ImmutableVector toImmutableVector(Vector3 vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    private ImmutableVector toImmutableVector(BlockVector3 vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    private ImmutableVector[] triangleToImmutableVectors(Triangle triangle) {
        ImmutableVector[] vectors = new ImmutableVector[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = toImmutableVector(triangle.getVertex(i));
        }

        return vectors;
    }
}
