package fr.mrmicky.worldeditselectionvisualizer.selection.shape.type;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.shape.ShapeProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractConvexProcessor<R extends Region> extends ShapeProcessor<R> {

    public AbstractConvexProcessor(Class<R> regionClass, WorldEditSelectionVisualizer plugin) {
        super(regionClass, plugin);
    }

    protected void createTriangles(Collection<Vector3d> vectors, List<Vector3d[]> triangles, double distance) {
        List<Vector3d> corners = new ArrayList<>(triangles.size() * 3);

        for (Vector3d[] triangle : triangles) {
            for (Vector3d vector : triangle) {
                corners.add(vector.add(0.5, 0.5, 0.5));
            }
        }

        for (int i = 0; i < corners.size(); i++) {
            Vector3d start = corners.get(i);
            Vector3d end = corners.get(i + 1 < corners.size() ? i + 1 : 0);

            createLine(vectors, start, end, distance);
        }
    }
}
