package fr.mrmicky.worldeditselectionvisualizer.compat.v6;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.polyhedron.Triangle;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.FaweAdapter6;
import fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils.Vectors6;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;
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

    @Override
    public ImmutableVector getMinimumPoint() {
        return Vectors6.toImmutableVector(region.getMinimumPoint());
    }

    @Override
    public ImmutableVector getMaximumPoint() {
        return Vectors6.toImmutableVector(region.getMaximumPoint());
    }

    @Override
    public ImmutableVector getCenter() {
        return Vectors6.toImmutableVector(region.getCenter());
    }

    @Override
    public List<ImmutableVector> getPolygonalPoints() {
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polygonalRegion = (Polygonal2DRegion) region;

            return polygonalRegion.getPoints().stream()
                    .map(vec -> new ImmutableVector(vec.getX(), 0, vec.getZ()))
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableVector getEllipsoidRadius() {
        if (region instanceof EllipsoidRegion) {
            return Vectors6.toImmutableVector(((EllipsoidRegion) region).getRadius());
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ImmutableVector[]> getConvexTriangles(boolean faweSupport) {
        if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion polygonalRegion = (ConvexPolyhedralRegion) region;

            return polygonalRegion.getTriangles().stream()
                    .map(this::triangleToImmutableVectors)
                    .collect(Collectors.toList());
        }

        if (faweSupport) {
            return FaweAdapter6.getConvexTriangles(region);
        }

        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Region getRegion() {
        return region;
    }

    private ImmutableVector[] triangleToImmutableVectors(Triangle triangle) {
        ImmutableVector[] vectors = new ImmutableVector[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors6.toImmutableVector(triangle.getVertex(i));
        }

        return vectors;
    }
}
