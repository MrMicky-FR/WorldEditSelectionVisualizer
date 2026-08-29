package fr.mrmicky.worldeditselectionvisualizer.selection.shapes;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import fr.mrmicky.worldeditselectionvisualizer.compat.RegionAdapter;
import fr.mrmicky.worldeditselectionvisualizer.config.VisualizationConfig;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Line;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Point;
import fr.mrmicky.worldeditselectionvisualizer.geometry.Shape;
import fr.mrmicky.worldeditselectionvisualizer.math.Vector3d;
import fr.mrmicky.worldeditselectionvisualizer.selection.SelectionPoints;
import org.jspecify.annotations.NullMarked;

import java.util.*;

@NullMarked
public class ConvexPolyhedralProcessor extends ShapeProcessor<ConvexPolyhedralRegion> {

    public ConvexPolyhedralProcessor() {
        super(ConvexPolyhedralRegion.class);
    }

    @Override
    public SelectionPoints processSelection(ConvexPolyhedralRegion region,
                                            RegionAdapter adapter,
                                            VisualizationConfig config) {
        List<Vector3d[]> triangles = adapter.getConvexTriangles();
        List<Vector3d> vertices = adapter.getConvexVertices();
        List<Shape> primary = new ArrayList<>(triangles.size() * 3);
        Set<Edge> edges = new HashSet<>(triangles.size() * 3);

        for (Vector3d[] triangle : triangles) {
            for (int i = 0; i < 3; i++) {
                Vector3d a = triangle[i].add(0.5, 0.5, 0.5);
                Vector3d b = triangle[(i + 1) % 3].add(0.5, 0.5, 0.5);

                if (edges.add(new Edge(a, b))) {
                    primary.add(new Line(a, b, config.primary()));
                }
            }
        }

        Point origin = vertices.isEmpty()
                ? null
                : new Point(vertices.get(0).add(0.5, 0.5, 0.5));

        return new SelectionPoints(primary, Collections.emptyList(), origin);
    }

    private static final class Edge {
        private final Vector3d a;
        private final Vector3d b;

        private Edge(Vector3d a, Vector3d b) {
            this.a = Objects.requireNonNull(a, "a");
            this.b = Objects.requireNonNull(b, "b");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge)) {
                return false;
            }

            Edge other = (Edge) obj;

            return (this.a.equals(other.a) && this.b.equals(other.b))
                    || (this.a.equals(other.b) && this.b.equals(other.a));
        }

        @Override
        public int hashCode() {
            return this.a.hashCode() + this.b.hashCode();
        }
    }
}
