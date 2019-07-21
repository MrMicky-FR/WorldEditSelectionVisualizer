package fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils;

import com.boydti.fawe.object.regions.PolyhedralRegion;
import com.boydti.fawe.object.regions.Triangle;
import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;

import java.util.List;
import java.util.stream.Collectors;

public final class FaweAdapter6 {

    private FaweAdapter6() {
        throw new UnsupportedOperationException();
    }

    public static List<Vector3d[]> getConvexTriangles(Region region) {
        if (region instanceof PolyhedralRegion) {
            return ((PolyhedralRegion) region).getTriangles().stream()
                    .map(FaweAdapter6::triangleToVectors)
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException();
    }

    private static Vector3d[] triangleToVectors(Triangle triangle) {
        Vector3d[] vectors = new Vector3d[3];

        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = Vectors6.toVector3d(triangle.getVertex(i));
        }

        return vectors;
    }
}
