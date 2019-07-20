package fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils;

import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.boydti.fawe.object.regions.Triangle;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.selection.ImmutableVector;

import java.util.List;
import java.util.stream.Collectors;

public final class FaweAdapter7 {

    private FaweAdapter7() {
        throw new UnsupportedOperationException();
    }

    public static List<ImmutableVector[]> getConvexTriangles(Region region) {
        if (region instanceof PolyhedralRegion) {
            return ((PolyhedralRegion) region).getTriangles().stream()
                    .map(FaweAdapter7::triangleToImmutableVectors)
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }


    private static ImmutableVector[] triangleToImmutableVectors(Triangle triangle) {
        ImmutableVector[] vectors = new ImmutableVector[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors7.toImmutableVector(triangle.getVertex(i));
        }

        return vectors;
    }
}