package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.Region;
import fr.mrmicky.worldeditselectionvisualizer.WorldEditSelectionVisualizer;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.GlobalSelectionConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Line;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConvexProcessor<R extends Region> extends ShapeProcessor<R> {

    protected AbstractConvexProcessor(Class<R> regionClass, WorldEditSelectionVisualizer plugin) {
        super(regionClass, plugin);
    }

    @Override
    public SelectionPoints processSelection(R region, RegionAdapter adapter, GlobalSelectionConfig config) {
        List<Vector3d[]> triangles = getTriangles(adapter);
        List<Vector3d> corners = new ArrayList<>(triangles.size() * 3);

        for (Vector3d[] triangle : triangles) {
            for (Vector3d vector : triangle) {
                corners.add(vector.add(0.5, 0.5, 0.5));
            }
        }

        List<Shape> primary = new ArrayList<>(corners.size());

        for (int i = 0; i < corners.size(); i++) {
            Vector3d end = corners.get(i + 1 < corners.size() ? i + 1 : 0);
            primary.add(new Line(corners.get(i), end, config.primary()));
        }

        return new SelectionPoints(primary, Collections.emptyList());
    }

    protected abstract List<Vector3d[]> getTriangles(RegionAdapter region);
}
