package com.rojel.wesv.v6;

import com.rojel.wesv.ImmutableVector;
import com.rojel.wesv.RegionWrapper;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
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
public class RegionWrapper6 implements RegionWrapper {

    private final Region region;

    public RegionWrapper6(Region region) {
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
                    .map(vec -> new ImmutableVector(vec.getX(), 0, vec.getZ()))
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
        Iterator<BlockVector> blockImmutableVectorIterator = region.iterator();

        return new Iterator<ImmutableVector>() {

            @Override
            public boolean hasNext() {
                return blockImmutableVectorIterator.hasNext();
            }

            @Override
            public ImmutableVector next() {
                return toImmutableVector(blockImmutableVectorIterator.next());
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

    private ImmutableVector toImmutableVector(BlockVector vec) {
        return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
    }

    private ImmutableVector toImmutableVector(Vector vec) {
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
