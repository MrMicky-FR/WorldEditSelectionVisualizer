package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.Polygonal2DRegion;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.VisualizationConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class Polygonal2DProcessor extends ShapeProcessor<Polygonal2DRegion> {

    public Polygonal2DProcessor() {
        super(Polygonal2DRegion.class);
    }

    @Override
    protected SelectionPoints processSelection(Polygonal2DRegion region,
                                               RegionAdapter adapter,
                                               VisualizationConfig config) {
        double minY = adapter.getMinimumPoint().getY();
        int height = region.getHeight();
        List<Vector3d> polygonalPoints = adapter.getPolygonalPoints();
        List<Vector3d> bottomCorners = new ArrayList<>(polygonalPoints.size());
        List<Shape> primary = new ArrayList<>(polygonalPoints.size() * 3);
        List<Shape> secondary = new ArrayList<>();

        for (Vector3d vec2d : polygonalPoints) {
            bottomCorners.add(vec2d.add(0.5, minY, 0.5));
        }

        createLinesFromBottom(primary, secondary, bottomCorners, height, config);

        return new SelectionPoints(primary, secondary);
    }
}
