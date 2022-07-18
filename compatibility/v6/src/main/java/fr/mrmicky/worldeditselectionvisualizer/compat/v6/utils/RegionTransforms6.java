package fr.mrmicky.worldeditselectionvisualizer.compat.v6.utils;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

import java.util.List;
import java.util.stream.Collectors;

public final class RegionTransforms6 {

    private RegionTransforms6() {
        throw new UnsupportedOperationException();
    }

    public static Region applyTransform(Region region, Transform transform, Vector origin) {
        if (region instanceof CuboidRegion) {
            return handle((CuboidRegion) region, transform, origin);
        }

        if (region instanceof EllipsoidRegion) {
            return handle((EllipsoidRegion) region, transform, origin);
        }

        if (region instanceof CylinderRegion) {
            return handle((CylinderRegion) region, transform, origin);
        }

        if (region instanceof Polygonal2DRegion) {
            return handle((Polygonal2DRegion) region, transform, origin);
        }

        if (region instanceof ConvexPolyhedralRegion) {
            return handle((ConvexPolyhedralRegion) region, transform, origin);
        }

        return region.clone();
    }

    private static Region handle(CuboidRegion region, Transform transform, Vector origin) {
        Vector pos1 = originTransform(transform, origin, region.getPos1());
        Vector pos2 = originTransform(transform, origin, region.getPos2());

        return new CuboidRegion(region.getWorld(), pos1, pos2);
    }

    private static Region handle(EllipsoidRegion region, Transform transform, Vector origin) {
        Vector center = originTransform(transform, origin, region.getCenter());
        Vector radius = transform.apply(region.getRadius());

        return new EllipsoidRegion(region.getWorld(), center, radius);
    }

    private static Region handle(CylinderRegion region, Transform transform, Vector origin) {
        Vector center = originTransform(transform, origin, region.getCenter());
        double radiusY = (region.getHeight() - 1) / 2.0;
        Vector radius = transform.apply(region.getRadius().toVector(radiusY));
        Vector2D newRadius = radius.toVector2D();
        int minY = (int) (center.getY() - radiusY);
        int maxY = (int) (center.getY() + radiusY);

        return new CylinderRegion(region.getWorld(), center, newRadius, minY, maxY);
    }

    private static Region handle(Polygonal2DRegion region, Transform transform, Vector origin) {
        List<BlockVector2D> points = region.getPoints()
                .stream()
                .map(point -> originTransform(transform, origin, point.toVector()))
                .map(point -> point.toVector2D().toBlockVector2D())
                .collect(Collectors.toList());

        Vector min = originTransform(transform, origin, region.getMinimumPoint());
        Vector max = originTransform(transform, origin, region.getMaximumPoint());
        int minY = (int) Math.min(min.getY(), max.getY());
        int maxY = (int) Math.max(min.getY(), max.getY());

        return new Polygonal2DRegion(region.getWorld(), points, minY, maxY);
    }

    private static Region handle(ConvexPolyhedralRegion region, Transform transform, Vector origin) {
        ConvexPolyhedralRegion result = new ConvexPolyhedralRegion(region.getWorld());

        for (Vector vertex : region.getVertices()) {
            result.addVertex(originTransform(transform, origin, vertex));
        }

        return result;
    }

    private static Vector originTransform(Transform transform, Vector origin, Vector vec) {
        return transform.apply(vec.subtract(origin)).add(origin);
    }
}
