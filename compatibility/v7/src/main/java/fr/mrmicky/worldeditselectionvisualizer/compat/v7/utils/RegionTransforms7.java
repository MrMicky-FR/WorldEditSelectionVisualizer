package fr.mrmicky.worldeditselectionvisualizer.compat.v7.utils;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector2;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

import java.util.List;
import java.util.stream.Collectors;

public final class RegionTransforms7 {

    private RegionTransforms7() {
        throw new UnsupportedOperationException();
    }

    public static Region originTransform(Region region, Transform transform, Vector3 origin) {
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

    private static Region handle(CuboidRegion region, Transform transform, Vector3 origin) {
        BlockVector3 pos1 = originTransform(transform, origin, region.getPos1());
        BlockVector3 pos2 = originTransform(transform, origin, region.getPos2());

        return new CuboidRegion(region.getWorld(), pos1, pos2);
    }

    private static Region handle(EllipsoidRegion region, Transform transform, Vector3 origin) {
        Vector3 center = originTransform(transform, origin, region.getCenter());
        Vector3 radius = transform.apply(region.getRadius());

        return new EllipsoidRegion(region.getWorld(), center.toBlockPoint(), radius);
    }

    private static Region handle(CylinderRegion region, Transform transform, Vector3 origin) {
        Vector3 center = originTransform(transform, origin, region.getCenter());
        double radiusY = (region.getHeight() - 1) / 2.0;
        Vector3 radius = transform.apply(region.getRadius().toVector3(radiusY));
        Vector2 newRadius = radius.toVector2();
        int minY = (int) (center.getY() - radiusY);
        int maxY = (int) (center.getY() + radiusY);

        return new CylinderRegion(region.getWorld(), center.toBlockPoint(), newRadius, minY, maxY);
    }

    private static Region handle(Polygonal2DRegion region, Transform transform, Vector3 origin) {
        List<BlockVector2> points = region.getPoints()
                .stream()
                .map(point -> originTransform(transform, origin, point.toVector3()))
                .map(point -> point.toVector2().toBlockPoint())
                .collect(Collectors.toList());

        BlockVector3 min = originTransform(transform, origin, region.getMinimumPoint());
        BlockVector3 max = originTransform(transform, origin, region.getMaximumPoint());
        int minY = Math.min(min.getY(), max.getY());
        int maxY = Math.max(min.getY(), max.getY());

        return new Polygonal2DRegion(region.getWorld(), points, minY, maxY);
    }

    private static Region handle(ConvexPolyhedralRegion region, Transform transform, Vector3 origin) {
        ConvexPolyhedralRegion result = new ConvexPolyhedralRegion(region.getWorld());

        for (BlockVector3 vertex : region.getVertices()) {
            result.addVertex(originTransform(transform, origin, vertex));
        }

        return result;
    }

    private static BlockVector3 originTransform(Transform transform, Vector3 origin, BlockVector3 vector) {
        return originTransform(transform, origin, vector.toVector3()).toBlockPoint();
    }

    private static Vector3 originTransform(Transform transform, Vector3 origin, Vector3 vector) {
        return transform.apply(vector.subtract(origin)).add(origin);
    }
}
